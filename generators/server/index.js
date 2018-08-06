var baseGenerator = require('../baseGenerator');
var prompts = require('./prompts');
var constants = require('../constants');
var Files = require('./files');
var chalk = require('chalk');

module.exports = class extends baseGenerator {

	constructor(args, opts) {
		super(args, opts);

		if (this.options.baseOptions) {
			this.isSubGenerator = true;
			this.configOptions = this.options.baseOptions.configOptions;
			this.inputFormat = this.options.baseOptions.inputFormat
			this.baseName = this.options.baseOptions.baseName
			this.categoryUserInput = this.options.baseOptions.categoryUserInput
			this.frequencyUserInput = this.options.baseOptions.frequencyUserInput
			this.likelihoodUserInput = this.options.baseOptions.likelihoodUserInput
		} 
	}
	get initializing() {
		return {
			getBaseConfig() {
				if(!this.isSubGenerator) {
					this.inputFormat = this.config.get('inputFormat');
					this.baseName = this.config.get('baseName');

					this.existingProject = this.inputFormat !== undefined &&
						this.baseName !== undefined;
					if (this.existingProject) {
						this.log(chalk.green('This is an existing project, reading the configuration from your .yo-rc.json file'));
					}
				}
				this.packageName = this.config.get('packageName');
				if (this.packageName)
					this.packageFolder = this.packageName.replace(/\./g, '/') + '/';
			}
		}
	}

	get prompting() {
		let promptObject = this.askForBaseOptions();
		promptObject['askForPackageName'] = prompts.askForPackageName;
		promptObject['askForScores'] = prompts.askForScores;
		return promptObject;
	}

	get configuring() {
		return {

			constructInputObjectFromInput() {
				this.frequencyTemplateObject = this.constructInputObjectFromInput(this.frequencyUserInput);
				this.categoryTemplateObject = this.constructInputObjectFromInput(this.categoryUserInput);
				this.likelihoodTemplateObject = this.constructInputObjectFromInput(this.likelihoodUserInput);
			},

			configureDirectories() {
				this.fileGenerator = new Files(constants.createFolders(this.packageFolder));
			},

			configureGlobal() {

				if (!this.configOptions) {
					this.configOptions = {
						baseNames: {
							base: this.baseName,
							camelized: this._.camelCase(this.baseName),
							dasherized: this._.kebabCase(this.baseName),
							lowercase: this.baseName.toLowerCase(),
							humanized: this._.startCase(this.baseName)
						}
					};
				}
				this.configOptions['packages'] = constants.createPackages(this.packageName);
			},

			configureFiles() {
				this.files = [];
				var categories = this.fileGenerator.createCategories(this.categoryTemplateObject);
				//baseFiles
				this.files = this.fileGenerator.createBaseFiles(
					this.likelihoodTemplateObject,
					this.frequencyTemplateObject,
					categories);

				//scoreFiles
				if (this.scores.tfidf) {
					this.files.push(this.fileGenerator.createTfidfScore(categories));
				}

				if (this.scores.prob) {
					this.files.push(this.fileGenerator.createProbScore(categories));
				}

				this.files = this.files.concat(categories);
			}
		};
	}


	get default() {

		return {
			saveConfig() {
				if (!this.isSubGenerator) {
					this.config.set('inputFormat', this.inputFormat);
					this.config.set('baseName', this.baseName);
				}
				this.config.set('packageName', this.packageName);
				this.config.set('scores', this.scores);
				this.config.save();
			}
		}


	}

	get writing() {
		return {
			writeTemplate() {
				this.writeFileToDisk(this.files);
			}
		}

	}
};