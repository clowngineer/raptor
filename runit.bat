@REM define location of maven repository, if USERPROFILE
@REM is not defined, then replace with C:/Users/your.os.name.here
set mr=%USERPROFILE%/.m2/repository

@REM define location of log4j jar
set logjar=%mr%/log4j/log4j/1.2.17/log4j-1.2.17.jar

@REM java command
java -cp .;%logjar%;./common/target/classes;./io/target/classes com.cobinrox.io.DoMotorCmd