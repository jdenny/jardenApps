set JAVA_HOME=C:\Program Files\Java\jdk1.7.0_51
set DERBY_BIN=%JAVA_HOME%\db\bin
set DERBY_OPTS="-Dderby.system.home=c:\home\john\myderby"

"%DERBY_BIN%\startNetworkServer" -noSecurityManager
