# Medical Decision Support System Generator

This project is a backend application for the generic medical decision support system <%= config.baseNames.humanized %>

## Dependencies

java 	version		8 or higher
mvn 	version 	^3.3.9

## Build

To build the server run ```mvn clean package```.

## Start

Before you start the server make sure that a mongoDB is running on ```localhost:27017```, because it is needed to store the diseases.
Start the server via ```java -jar ./target/<%= config.baseNames.dasherized %>-1.0-SNAPSHOT.jar```.