# SD2324 project

This repository includes the API and a set of files that should be used in project 1.

* ```test-sd-tp1.bat``` / ```test-sd-tp1.sh``` :  script files for running the project in Windows and Linux/Mac
* ```shorts.props``` : file with information for starting servers
* ```Dockerfile``` : Dockerfile for creating the docker image of the project
* ```hibernate.cfg.xml``` : auxiliary file for using Hibernate
* ```pom.xml``` : maven file for creating the project

* how ro run the project
  * ```mvn clean compile assembly:single docker:build```
  * ```./test-sd-tp1.bat -image sd2324-tp1-api-xxxxx-yyyyy"```