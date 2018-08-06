# Medical Decision Support System Generator

This project is a generator for generic medical decision support systems using yeoman as base. 

## Dependencies

node 	version 	^9.10.1
npm 	version 	^5.8.0
yeoman 	version 	^2.0.1

## Local deployment

To install this generator locally us the command ```npm link``` in the directory of the generator.

Run the generator in a directory you want to initialize a decision support system with ```yo mdss```.

The generator then will guide you through the setup process.
Use the data from test-data/generator during the generation process.

## Input

Non-self-explanatory input are the input prompts for categories, frequencies and likelihoods.

+ categories: The categories of features a disease should have.
+ frequencies: Overall Frequencies a disease might have.
+ likelihoods: Likelihoods single features of categories might have with a disease.

Currently only the input of these can only be done by JSON. They have to comply with the JSON-Schemas 
found in ```schemas/json```.

## Example

Exemplary input for the generator, as well as for the resulting system (disease base) can be found in ```example-input```

## TODO

Distill a manual from thesis