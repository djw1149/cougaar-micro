@echo OFF

REM calls setlibpath.bat which sets the path to the required jar files.
REM calls setarguments.bat which sets input parameters for system behavior
CALL %ALP_INSTALL_PATH%\bin\setlibpath.bat
CALL %ALP_INSTALL_PATH%\bin\setarguments.bat

set MYARGUMENTS= -c -n "%1"

set MYPROPERTIES=-Dorg.cougaar.domain.micro=org.cougaar.microedition.domain.Domain %MYPROPERTIES%
#set LIBPATHS=..\..\..\data\cougaarMEDomain.jar;%LIBPATHS%
#set LIBPATHS=.\classes\;%LIBPATHS%
set LIBPATHS=c:\alp\micro\classes\;%LIBPATHS%
#set MYPROPERTIES=%MYPROPERTIES% -Dorg.cougaar.core.society.bootstrapper.loud=shout

@ECHO ON

java.exe %MYPROPERTIES% %MYMEMORY% -classpath %LIBPATHS% %MYCLASSES% %MYARGUMENTS% %2 %3


