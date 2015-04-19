@REM define location of maven repository, based on the Windows
@REM USERPROFILE environment variable, where for W7, USERPROFILE
@REM will usually resolve to C:/Users/your.os.name.here.
@REM
@REM If the Windows user env variable USERPROFILE is not defined
@REM in your version of windows, then
@REM replace the USERPROFILE entry below with C:/Users/your.os.name.here
set mr=%USERPROFILE%/.m2/repository

@REM define location of log4j jar, relative to the maven repository
set logjar=%mr%/log4j/log4j/1.2.17/log4j-1.2.17.jar

@REM java command, assumes java available in path
java -cp .;%logjar%;./common/target/classes;./io/target/classes com.cobinrox.io.DoMotorCmd