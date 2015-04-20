#!/bin/bash

mr=$HOME
mr+="/.m2/repository"

logjar=$mr
logjar+="/log4j/log4j/1.2.17/log4j-1.2.17.jar"

java -cp .:./io/target:./io/target/classes:./common/target/classes:$logjar com.c
obinrox.io.DoMotorCmd