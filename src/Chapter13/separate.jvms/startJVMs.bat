@echo Running three Tomcat workers
set CATALINA_BASE=C:\home\sites\catalinabook.com\catalina
start /B catalina start

set CATALINA_BASE=C:\home\sites\jasperbook.com\catalina
start /B catalina start

set CATALINA_BASE=C:\home\sites\tomcatbook.com\catalina
start /B catalina start
