#! /usr/bin/bash
mvn clean test -Dcucumber=" --tags @scenario3_stage" allure:serve