var _ = require('lodash');

/**
 * Class for the generation of Server file configurations
 */
module.exports = class Files {

	constructor() {
		this.baseDirectory = 'src/main/webapp/';
	}

	/**
	 * Creates all base file configurations for the server
	 * @param {Array} categoriesInput - The user input for categories
	 */
	createBaseFiles(categoriesInput) {
		var categories = {};
		for (var key in categoriesInput) {
			createCategory(categoriesInput[key], categories, '', '', this.directories);
		}
		return [
			{
				folder: this.baseDirectory + 'build/',
				copyTo: this.baseDirectory + 'build/'
			},
			{
				folder: this.baseDirectory + 'config/',
				copyTo: this.baseDirectory + 'config/'
			},
			{
				folder: this.baseDirectory + 'static/',
				copyTo: this.baseDirectory + 'static/'
			},
			{
				folder: this.baseDirectory + 'server/',
				copyTo: this.baseDirectory + 'server/'
			},
			{
				folder: this.baseDirectory,
				copyTo: this.baseDirectory,
				files: ['_.babelrc', '_.editorconfig', '_.eslintignore', '_.eslintrc.js', '_.gitignore', '_.postcssrc.js', '_index.html', '_package.json', '_README.md']
			},
			{
				folder: this.baseDirectory + 'src/assets/',
				copyTo: this.baseDirectory + 'src/assets/'
			},
			{
				folder: this.baseDirectory + 'src/components/',
				copyTo: this.baseDirectory + 'src/components/'
			},
			{
				folder: this.baseDirectory + 'src/util/',
				copyTo: this.baseDirectory + 'src/util/'
			},
			{
				folder: this.baseDirectory + 'src/containers/',
				copyTo: this.baseDirectory + 'src/containers/'
			},
			{
				folder: this.baseDirectory + 'src/router/',
				copyTo: this.baseDirectory + 'src/router/'
			},
			{
				folder: this.baseDirectory + 'src/views/',
				copyTo: this.baseDirectory + 'src/views/'
			},
			{
				folder: this.baseDirectory + 'src/',
				copyTo: this.baseDirectory + 'src/',
				files: ['_config.js', '__nav.js', '_App.vue', '_main.js'],
				parameters: { categories: categories }
			}];
	}

};

//private helper functions

function createCategory(input, obj, prefix, prevVarName) {
	var name = _.lowerFirst(prefix + _.upperFirst(_.camelCase(prevVarName)) + _.upperFirst(_.camelCase(input.name)));
	obj[name] = {
		values: [],
		exclusive: input.patient ? input.patient.exclusive : false
	};

	for (var key in input.values) {
		if (typeof input.values[key] === 'string') {
			obj[name].values.push(_.toUpper(_.snakeCase(input.values[key])));
		}
		else  if (input.values[key].dependent) {
			let dependentValue = {
				name: _.toUpper(_.snakeCase(input.values[key].name)),
				dependent: []
			}
			for (var dep_key in input.values[key].dependent) {
				dependentValue.dependent.push(createCategory(input.values[key].dependent[dep_key], obj, name, input.values[key].name));
			}
			obj[name].values.push(dependentValue);
		} else if (input.values[key].parts) {
			obj[name].values = obj[name].values.concat(createParts('', input.values[key]));
		}
	}
	return name
}

function createParts(previousPart, partValue) {
	let arr = [previousPart + _.toUpper(_.snakeCase(partValue.name))];
	for (var key in partValue.parts) {
		let value = partValue.parts[key];
		if (typeof value === "string") {
			arr.push(arr[0] + '_' + _.toUpper(_.snakeCase(value)));
		} else {
			arr = arr.concat(createParts(arr[0] + '_', value));
		}
	}
	return arr;
};