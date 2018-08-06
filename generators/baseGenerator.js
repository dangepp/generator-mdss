var Generator = require('yeoman-generator');
var validate = require('jsonschema').validate;
var _ = require('lodash');
var basePrompts = require('./basePrompts');
var path = require('path');

/**
 *  Generator base class.
 * It provides all the public API methods exposed to the generators.
 *
 */
module.exports = class extends Generator {

	constructor(args, opts) {
		super(args, opts);
		this._ = _;
		let jsonSchemaPath = path.join(__dirname, '..', 'schemas', 'json');
		this.schemas = {
			json: {
				category: this.fs.readJSON(path.join(jsonSchemaPath, 'category.json')),
				frequency: this.fs.readJSON(path.join(jsonSchemaPath, 'frequency.json')),
				likelihood: this.fs.readJSON(path.join(jsonSchemaPath, 'likelihood.json'))
			}
		};

	}

	/**
	 * Prompt the base options to the user.
	 */
	askForBaseOptions() {
		return {
			askForBaseName: basePrompts.askForBaseName,
			askForBasicOptionsFormat: basePrompts.askForBasicOptionsFormat,
			askForUserInput: basePrompts.askForUserInput
		}
	}

	/**
	 * Creates a function for validating the 
	 * @param {string} objectName - The name of the object the validator should be created for
	 */
	createValidationFunction(objectName) {
		if (this.inputFormat === '.json') {
			var schema = this.schemas.json[objectName];
			return function (input, hash) {
				var inputObject;
				try {
					inputObject = JSON.parse(input);
				}
				catch (err) {
					return err;
				}

				var validation = validate(inputObject, schema);

				if (validation.errors.length === 0) {
					return true
				} else {
					return validation.errors[0].property + ' ' + validation.errors[0].message
				}
			};
		} else if (this.inputFormat === '.expertdsl') {
			//todo implement
			return null;
		} else {
			//todo error
		}
	}

	/**
	 * Parses the input from the user according to the 
	 * specified input format
	 * @param {string} input - the input to be parsed
	 */
	constructInputObjectFromInput(input) {
		// todo make out of user input object regarding input format
		if (this.inputFormat === '.json') {
			return JSON.parse(input); // assume it is validated
		} else if (this.inputFormat === '.expertdsl') {
			this.log.error('expertdsl Input Format not not yet supported.');
			process.exit(1);
			// todo once implemented
		} else {
			this.log.error('Input Format not recogniced to consturct Object');
			process.exit(1);
		}
	}

/**
 * Writes files given to the disk
 * @param {array} files - the files to be written
 */
	writeFileToDisk(files) {
		for (var key in files) {

			//construct template and copyTo names
			var fileNames = [];
			if (files[key].folder !== undefined && files[key].copyTo !== undefined && files[key].files !== undefined) {
				//folder of files
				files[key].files.forEach(function (file) {
					fileNames.push({
						template: files[key].folder + file,
						to: files[key].copyTo + file.substr(1)
					});
				});
			} else if (files[key].file && files[key].renameTo){
				//single file
				fileNames.push({
					template: files[key].file,
					to: files[key].renameTo
				});
			} 

			
			//construct params
			var parameters = {};
			if (files[key].parameters) {
				parameters = typeof files[key].parameters === 'function' ?
					files[key].parameters(files[key]) :
					files[key].parameters;
			}
			parameters['config'] = this.configOptions;
			parameters['lodash'] = this._;

			//copy files
			if (fileNames.length > 0) {
				for (var copyKey in fileNames) {
					this.fs.copyTpl(
						this.templatePath(fileNames[copyKey].template),
						this.destinationPath(fileNames[copyKey].to),
						parameters);
				}
			} else if (files[key].folder !== undefined && files[key].copyTo !== undefined) {
				// copy complete folder WITHOUT Templating
				this.fs.copy(
					this.templatePath(files[key].folder),
					this.destinationPath(files[key].copyTo)
				);
			}
		}

	}

};