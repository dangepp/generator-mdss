module.exports = {
    askForBasicOptionsFormat: askForBasicOptionsFormat,
    askForUserInput: askForUserInput,
    askForBaseName: askForBaseName
}

/**
 * Ask for application name
 */
function askForBaseName() {
    if (this.isSubGenerator || this.baseName)
        return;
    const done = this.async();
    this.prompt({
        type: 'input',
        name: 'baseName',
        validate: (input) => {
            if (!(/^([a-zA-Z0-9_]*)$/.test(input))) {
                return 'Your application name cannot contain special characters or a blank space';
            } else if (input === 'application') {
                return 'Your application name cannot be named \'application\' as this is a reserved name for Spring Boot';
            }
            return true;
        },
        message: 'What is the base name of your application?',
        default: 'ExpertSystem'
    }).then((answer) => {
        this.baseName = answer.baseName;
        done();
    });
}
/**
 * Ask for input format
 */
function askForBasicOptionsFormat() {
    if (this.isSubGenerator || this.existingProject)
        return;
    const done = this.async();
    this.prompt([{
        type: 'list',
        name: 'inputFormat',
        message: 'The format of your Input',
        choices: [{ name: 'JSON', value: '.json' }, { name: 'expertDSL', value: '.expertdsl', disabled: true }],
        store: true
    }]
    ).then((answer) => {
        this.inputFormat = answer.inputFormat;
        done();
    });
}
/**
 * Ask for essential user input: categories, likelihood and frequency
 */
function askForUserInput() {
    if (this.isSubGenerator)
        return;
    const done = this.async();

    this.prompt([
        {
            type: 'editor',
            name: 'frequencyUserInput',
            message: 'Your input to create the Frequency Object',
            validate: this.createValidationFunction('frequency'),
            store: true
        },
        {
            type: 'editor',
            name: 'likelihoodUserInput',
            message: 'Your input to create the Likelihood Object',
            validate: this.createValidationFunction('likelihood'),
            store: true
        },
        {
            type: 'editor',
            name: 'categoryUserInput',
            message: 'Your input to create the Category dependent Objects',
            validate: this.createValidationFunction('category'),
            store: true
        }

    ]).then((answer) => {
        this.frequencyUserInput = answer.frequencyUserInput;
        this.categoryUserInput = answer.categoryUserInput;
        this.likelihoodUserInput = answer.likelihoodUserInput;
        done();
    });


}