# raptor
Raspberry Pi Motor Project

To build in Eclipse IDE (works as of Eclipse-Luna on Windows7):
1) Download entire raptor project
2) Go to command line, change to the raptor subdirectory
3) Run mvn eclipse:eclipse (this will create Eclipse .classpath and .project files)
4) Run the Eclipse IDE
5) In Eclipse, choose File, Import General Project (NOT NOT NOT Maven project!!)
6) Navigate to the raptor directory as the root to import from
7) You will only be given the three child projects to choose; choose all three and import
8) You *should* now be able to build and run within Eclipse IDE, though you will not
see the parent "raptor" in the IDE
9) If you update any pom.xml files (adding dependencies, for example), you will have
to go out to the command line and re-run eclipse:eclipse, then restart Eclipse and
within Eclipse do a project refresh
