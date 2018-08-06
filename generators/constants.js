// all constants used throughout all generators

const MAIN_DIR = 'src/main/';
const TEST_DIR = 'src/test/';

const CORE = 'core';
const SCORE = 'score';
const REST = 'rest';
const CONFIG = 'configuration';
const SERVICE = 'service';

const CATEGORY = 'featurecategory';
const PROFILE = 'profile';
const ENUM = 'enumeration';
const UTIL = 'util';
const TFIDF = 'tfidf';
const PROB = 'prob';
const MODEL = 'model';
const MAPPER = 'mapper';
const REPO = 'repository';
const FILE = 'file';
const VALIDATION = 'validation';
const EXCEPTION = 'exception';

const FOLDER_DELIMITER = '/';
const PACKAGE_DELIMITER = '.';

module.exports = {
    createFolders: createFolders,
    createPackages: createPackages
}

/**
 * Creates folder path constants
 * @param {string} userFolder - the base folder for source files
 */
function createFolders (userFolder) {
    var rootDir = '';
    var mainDir = MAIN_DIR;
    var mainSrc = mainDir + 'java/';
    var mainRsr = mainDir + 'resources/';
    var base = mainSrc + userFolder;
    var core = base + CORE + FOLDER_DELIMITER;
    var profile = core + PROFILE + FOLDER_DELIMITER;
    var category = core + CATEGORY + FOLDER_DELIMITER;

    var util = core + UTIL + FOLDER_DELIMITER;
    var score = core + SCORE + FOLDER_DELIMITER;
    var tfidf = score + TFIDF + FOLDER_DELIMITER;
    var prob = score + PROB + FOLDER_DELIMITER;
    var rest = base + REST + FOLDER_DELIMITER;
    var config = base + CONFIG + FOLDER_DELIMITER;
    var restModel = rest + MODEL + FOLDER_DELIMITER;
    var restFile = rest + FILE + FOLDER_DELIMITER;
    var restValidation = rest + VALIDATION + FOLDER_DELIMITER;
    var restException = rest + EXCEPTION + FOLDER_DELIMITER;
    var profileRepo = profile + REPO + FOLDER_DELIMITER;
    var service = base + SERVICE + FOLDER_DELIMITER;
    var serviceMapper = service + MAPPER + FOLDER_DELIMITER;
    return {
        rootDir,
        mainDir,
        mainSrc,
        mainRsr,
        base,
        core,
        profile,
        enum: core + ENUM + FOLDER_DELIMITER, // otherwise reserved word
        category,
        util,
        score,
        tfidf,
        prob,
        rest,
        config,
        restModel,
        restFile,
        restValidation,
        restException,
        profileRepo,
        service,
        serviceMapper
    };
}

/**
 * Creates package constants
 * @param {string} userPackage - the base package given from the user
 */
function createPackages (userPackage) {
    var base = userPackage;
    var core = base + PACKAGE_DELIMITER + CORE;
    var profile = core + PACKAGE_DELIMITER + PROFILE;
    var category = core + PACKAGE_DELIMITER + CATEGORY;

    var util = core + PACKAGE_DELIMITER + UTIL;
    var score = core + PACKAGE_DELIMITER + SCORE;
    var tfidf = score + PACKAGE_DELIMITER + TFIDF;
    var prob = score + PACKAGE_DELIMITER + PROB;
    var rest = base + PACKAGE_DELIMITER + REST;
    var config = base + PACKAGE_DELIMITER + CONFIG;
    var restModel = rest + PACKAGE_DELIMITER + MODEL;
    var restFile = rest + PACKAGE_DELIMITER + FILE;
    var restValidation = rest + PACKAGE_DELIMITER + VALIDATION;
    var restException = rest + PACKAGE_DELIMITER + EXCEPTION;
    var profileRepo = profile + PACKAGE_DELIMITER + REPO;
    var service = base + PACKAGE_DELIMITER + SERVICE;
    var serviceMapper = service + PACKAGE_DELIMITER + MAPPER;
    return {
        base,
        core,
        profile,
        enum: core + PACKAGE_DELIMITER + ENUM,// otherwise reserved word
        category,
        util,
        score,
        tfidf,
        prob,
        rest,
        config,
        restModel,
        restFile,
        restValidation,
        restException,
        profileRepo,
        service,
        serviceMapper
    };
}
