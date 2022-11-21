#! /usr/bin/bash
mvn clean test -Dcucumber=" --tags @scenario1_stage" allure:serve