#MLCP Bulk Loader
[![Build Status](https://travis-ci.org/rwalpole/mlcp-bulk-loader.svg?branch=master)](https://travis-ci.org/rwalpole/mlcp-bulk-loader)

This Bulk Loader extends MarkLogic Content Pump ([MLCP](https://developer.marklogic.com/products/mlcpMLCP)) to bulk load multiple directories of data into a running instance of [MarkLogic NoSQL Database](http://www.marklogic.com/). It takes either a text input file which contains a list of directory paths or a directory name from which it then attempts to load the sub-directories contained within. Which method to use depends on the location of your data and whether you want to be highly selective or not.

You will need [Apache Maven](https://maven.apache.org/) and the [Java SE 8 JDK](http://www.oracle.com/technetwork/java/javase/downloads/index-jsp-138363.html) to build this tool. 

To build run `mvn clean compile assembly:single` from the root of this project. This will create an executable jar file in the target directory which can then be used from the command line as follows:
 
    java -jar target/mlcp-bulk-loader-1.0-SNAPSHOT-jar-with-dependencies.jar

The required command line parameters are as follows:

- **input file name and path | directory path** - if this value provided is a file name and path then it is assumed that the file is a text file containing a list of folder paths. If the value is a directory path then it is assumed that all of the sub-directories below this contain data to be loaded   
- **host name** - the MarkLogic instance hostname - this can usually be found on the MarkLogic admin console
- **port** - an XDBC port on the MarkLogic instance. If no specific port has been configured then port 8000 usually provides XDBC
- **user name** - must be a user with write access to the MarkLogic database
- **password** - password for the user above
- **data type** - this data type is used to allocate a collection name to the data being loaded 
