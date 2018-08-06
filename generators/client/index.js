var baseGenerator = require('../baseGenerator');
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
			}
		}
	}

	get prompting() {
		return this.askForBaseOptions();
	}

	get configuring() {
		return {

			constructInputObjectFromInput() {
				this.categoryTemplateObject = this.constructInputObjectFromInput(this.categoryUserInput);
			},

			configureDirectories() {
				this.fileGenerator = new Files();
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
			},

			configureFiles() {
				//baseFiles
				this.files = this.fileGenerator.createBaseFiles(this.categoryTemplateObject);
			}
		};
	}


	get default() {

		return {
			saveConfig() {
				if (!this.isSubGenerator) {
					this.config.set('inputFormat', this.inputFormat);
					this.config.set('baseName', this.baseName);
					this.config.save();
				}
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