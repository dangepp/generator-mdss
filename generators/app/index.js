var baseGenerator = require('../baseGenerator');
var Validator = require('jsonschema').Validator;
var chalk = require('chalk');



module.exports = class extends baseGenerator {
    constructor(args, opts) {
        super(args, opts);
    }

    get initializing() {
        return {
            getBaseConfig() {
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

    get prompting() {
        if (!this.existingProject)
            return this.askForBaseOptions();
        else
            return {};
    }

    get configuring() {
        return {

            configureGlobal() {
                this.configOptions = {
                    baseNames: {
                        base: this.baseName,
                        camelized: this._.camelCase(this.baseName),
                        dasherized: this._.kebabCase(this.baseName),
                        lowercase: this.baseName.toLowerCase(),
                        humanized: this._.startCase(this.baseName)
                    }
                };
            },

            startSubGenerators() {
                let baseOptions = {
                    configOptions: this.configOptions,
                    inputFormat: this.inputFormat,
                    baseName: this.baseName,
                    frequencyUserInput: this.frequencyUserInput,
                    categoryUserInput: this.categoryUserInput,
                    likelihoodUserInput: this.likelihoodUserInput
                }
                this.composeWith(require.resolve('../server'), {
                    baseOptions: baseOptions
                });
                this.composeWith(require.resolve('../client'), {
                    baseOptions: baseOptions

                });

            }
        }
    }

    get default() {

        return {
            saveConfig() {
                this.config.set('inputFormat', this.inputFormat);
                this.config.set('baseName', this.baseName);
                this.config.save();
            }
        }


    }

    end() {
        this.log('Finished generating files');
    }

};