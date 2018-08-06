var _ = require('lodash');

/**
 * Class for the generation of Server file configurations
 */
module.exports = class Files {

	/**
	 * Constructs a new File object
	 * @param {Object} directories - The directory object containing all preconfigured paths
	 */
	constructor(directories) {
		this.directories = directories;
	}

	/**
	 * Creates the category files that can also be used as 
	 * configuration property for other files as well
	 * @param {Array} input - The user input for categories
	 */
	createCategories(input) {
		var categories = [];
		for (var key in input) {
			createCategory(input[key], categories, '', '', this.directories);
		}
		categories.reverse();
		return categories;
	}

	/**
	 * Creates all base file configurations for the server
	 * @param {Object} likelihoodInput - The user input for likelihoods
	 * @param {Object} frequenciesInput - The user input for frequencies
	 * @param {Array} categories - The file configurations for categories
	 */
	createBaseFiles(likelihoodInput, frequenciesInput, categories) {
		return [
			createLikelihood(likelihoodInput, this.directories),
			createFrequencies(frequenciesInput, this.directories),
			{
				folder: this.directories.mainSrc + 'core/profile/',
				copyTo: this.directories.profile,
				files: ['_DiseaseProfile.java', '_PatientProfile.java'],
				parameters: { categories: categories }
			}, {
				folder: this.directories.mainSrc + 'core/featurecategory/',
				copyTo: this.directories.category,
				files: ['_HasParts.java'],
				parameters: { categories: categories }
			}, {
				folder: this.directories.mainSrc + 'core/enum/',
				copyTo: this.directories.enum,
				files: ['_Category.java'],
				parameters: { categories: categories }
			},{
				folder: this.directories.mainSrc + 'core/profile/repository/',
				copyTo: this.directories.profileRepo,
				files: ['_DiseaseRepository.java']
			}, {
				folder: this.directories.mainSrc + 'core/util/',
				copyTo: this.directories.util,
				files: ['_DoubleRegister.java', '_EnumCounter.java', '_EnumMapCounter.java', '_EnumSetCounter.java']
			}, {
				folder: this.directories.mainSrc + 'core/score/',
				copyTo: this.directories.score,
				files: ['_Score.java', '_ScoreException.java', '_ScoreFactory.java', '_ScoreType.java', '_AbstractScoreFactory.java',
					'_ConflictUtil.java'],
				parameters: { categories: categories }
			}, {
				folder: this.directories.rootDir,
				copyTo: this.directories.rootDir,
				files: ['_pom.xml', '_README.md']
			}, {
				folder: this.directories.mainRsr,
				copyTo: this.directories.mainRsr,
				files: ['_application.yml']
			}, {
				folder: this.directories.mainSrc + 'rest/',
				copyTo: this.directories.rest,
				files: ['_DiseaseController.java', '_PatientController.java']
			}, {
				folder: this.directories.mainSrc + 'rest/model/',
				copyTo: this.directories.restModel,
				files: ['_DiseaseModel.java', '_PatientModel.java', '_DiseaseMetaModel.java',
					'_BaseModel.java', '_ScoreModel.java'],
				parameters: { categories: categories }
			}, {
				folder: this.directories.mainSrc + 'rest/file/',
				copyTo: this.directories.restFile,
				files: ['_AttributeMissmatchException.java', '_Attributes.java',
					'_DiseaseBaseMapper.java', '_DiseaseMapper.java', '_HeaderException.java',
					'_MapperException.java', '_MapperExecutionException.java',
					'_MappingHelper.java', '_SheetException.java',
					'_StringMappingException.java'],
				parameters: { categories: categories }
			}, {
				folder: this.directories.mainSrc + 'rest/validation/',
				copyTo: this.directories.restValidation,
				files: ['_AtLeast.java', '_DependentOn.java',
					'_ModelConstraint.java', '_DiseaseModelValidator.java', '_PatientModelValidator.java',
					'_ValidationHelper.java']
			}, {
				folder: this.directories.mainSrc + 'rest/exception/',
				copyTo: this.directories.restException,
				files: ['_UnprocessableEntityException.java', '_GlobalControllerExceptionHandler.java']
			},
			{
				folder: this.directories.mainSrc,
				copyTo: this.directories.base,
				files: ['_Application.java']
			}, {
				folder: this.directories.mainSrc + 'configuration/',
				copyTo: this.directories.config,
				files: ['_SwaggerConfig.java']
			}, {
				folder: this.directories.mainSrc + 'service/',
				copyTo: this.directories.service,
				files: ['_DiseaseService.java', '_PatientService.java', '_ServiceException.java']
			}, {
				folder: this.directories.mainSrc + 'service/mapper/',
				copyTo: this.directories.serviceMapper,
				files: ['_DiseaseMapper.java', '_PatientMapper.java', '_ScoreMapper.java']
			}];
	}

	/**
	 * Creates all base file configurations for the TFIDF based score server files
	 * @param {Array} categories 
	 */
	createTfidfScore(categories) {
		return {
			folder: this.directories.mainSrc + 'core/score/tfidf/',
			copyTo: this.directories.tfidf,
			files: ['_IDFMethodFactory.java', '_IDFStatistics.java', '_IDFOnlyYesScoreComputations.java', '_TFOnlyScoreComputations.java'],
			parameters: { categories: categories }
		};
	}

		/**
	 * Creates all base file configurations for the probabilistic score server files
	 * @param {Array} categories 
	 */
	createProbScore(categories) {
		return {
			folder: this.directories.mainSrc + 'core/score/prob/',
			copyTo: this.directories.prob,
			files: ['_ProbMethodFactory.java', '_ScoreComputations.java'],
			parameters: { categories: categories }
		};
	}

};

//private helper functions
function createCategory(input, arr, prefix, prevVarName, directories) {
	var name = prefix + _.upperFirst(_.camelCase(prevVarName)) + _.upperFirst(_.camelCase(input.name));
	var cat = {
		name: name,
		varName: _.lowerFirst(name),
		enumName: _.toUpper(_.snakeCase(name)),
		values: [],
		type: input.type,
		noHardConflict: input.noHardConflict,
		exclusive: input.patient ? input.patient.exclusive : false,
		dependentOn: createDependentOn(prefix, prevVarName),
		weight: input.weight,
		hasParts: false,
		validationAnnotation: createCategoryValidationAnnotation(prefix, prevVarName, input.atLeast),
		file: directories.mainSrc + 'core/featurecategory/_FeatureCategory.java',
		renameTo: directories.category + name + '.java',
		parameters: input => input
	};

	for (var key in input.values) {
		if (typeof input.values[key] === 'string') {
			cat.values.push(_.toUpper(_.snakeCase(input.values[key])));
		} else if (input.values[key].dependent) {
			cat.values.push(_.toUpper(_.snakeCase(input.values[key].name)));

			for (var dep_key in input.values[key].dependent) {
				createCategory(input.values[key].dependent[dep_key], arr, name, input.values[key].name, directories);
			}
		} else if (input.values[key].parts) {
			cat.hasParts = true;
			cat.values = cat.values.concat(createParts('', input.values[key]));
		}
	}
	arr.push(cat);
}

function createCategoryValidationAnnotation(prefix, prevVarName, atLeast) {
	let annotation = {};
	let dependentOn = null;
	if (prefix && prevVarName) {
		dependentOn = _.lowerFirst(prefix) + '.' + _.toUpper(_.snakeCase(prevVarName));
	}
	return {
		disease: createDiseaseValidationAnnotation(dependentOn, atLeast),
		patient: createPatientValidationAnnotation(dependentOn)
	};
}

function createDependentOn(prefix, prevVarName) {
	if (prefix && prevVarName) {
		return {
			varName: prefix,
			item: _.toUpper(_.snakeCase(prevVarName))
		};
	}
	return false;
}

function createDiseaseValidationAnnotation(dependentOn, atLeast) {
	if (dependentOn && atLeast) {
		return '@DependentOn(value = "' + dependentOn + '", atLeast = ' + atLeast + ')';
	} else if (dependentOn) {
		return '@DependentOn("' + dependentOn + '")';
	} else if (atLeast) {
		return '@AtLeast(' + atLeast + ')';
	}
	return null;
}

function createPatientValidationAnnotation(dependentOn) {
	if (dependentOn) {
		return '@DependentOn("' + dependentOn + '")';
	}
	return null;
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

function createLikelihood(input, directories) {
	var likelihood = {
		parameters: {
			yesValues: [],
			otherLikelihood: [],
			yesLikelihood: 0.0,
			noLikelihood: 0.0
		},
		file: directories.mainSrc + 'core/enum/_Likelihood.java',
		renameTo: directories.enum + 'Likelihood.java'
	}
	for (var key in input) {
		switch (key) {
			case 'yes':
				likelihood.parameters.yesLikelihood = input[key];
				break;
			case 'no':
				likelihood.parameters.noLikelihood = input[key];
				break;
			default:
				if (typeof input[key] === 'object') {
					if (input[key].yesGroup) {
						likelihood.parameters.yesValues.push(key.toUpperCase());
					}
					likelihood.parameters.otherLikelihood.push({ name: key.toUpperCase(), percentage: input[key].value });
				} else {
					likelihood.parameters.otherLikelihood.push({ name: key.toUpperCase(), percentage: input[key] });
				}

		}
	}
	return likelihood;
}

function createFrequencies(input, directories) {
	var frequency = {
		parameters: {
			frequencies: []
		},
		file: directories.mainSrc + 'core/enum/_Frequency.java',
		renameTo: directories.enum + 'Frequency.java'
	};
	for (var key in input) {
		frequency.parameters.frequencies.push({ name: key.toUpperCase(), percentage: input[key] });
	}
	return frequency;
}

