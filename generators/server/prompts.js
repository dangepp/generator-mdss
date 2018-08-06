module.exports = {
    askForPackageName: askForPackageName,
    askForScores: askForScores
}

/**
 * Ask for the package name
 */
function askForPackageName() {
    if(this.packageName) 
        return;
    const done = this.async();
    this.prompt(
    { //taken from Jhipster generator
        type: 'input',
        name: 'packageName',
        validate: input => (/^([a-z_]{1}[a-z0-9_]*(\.[a-z_]{1}[a-z0-9_]*)*)$/.test(input) ?
            true : 'The package name you have provided is not a valid Java package name.'),
        message: 'What is your default Java package name?',
        default: 'com.mycompany.myexpertsystem',
        store: true
    }
    ).then((answer) => {
        this.packageName = answer.packageName;
        this.packageFolder = this.packageName.replace(/\./g, '/') + '/';
        done();
    });
}

/**
 * Ask for the score that should be used
 */
function askForScores() {
    const done = this.async();
    this.prompt(
    {
        type    : 'list',
        name    : 'score',
        message : 'Which Score Methods your Medical decision support system should use?',
        choices : [{name:'Probabilistic', value:'prob'},{name:'TFIDF', value:'tfidf'}],
        validate: input => input ? true : 'A score must be set',
        store   : true
    }).then((answer) => {
        this.scores = {};
        this.scores[answer.score] = true;
        done();
    });
    
    
}