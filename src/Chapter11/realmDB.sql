CREATE DATABASE realmDB;
USE realmDB;

CREATE TABLE deptusers (
  apressusername VARCHAR(15) NOT NULL PRIMARY KEY,
  password     VARCHAR(40) NOT NULL
) TYPE=InnoDB;

CREATE TABLE deptroles (
  apressusername VARCHAR(15) NOT NULL,
  apressrole     VARCHAR(15) NOT NULL,
  PRIMARY KEY (apressusername, apressrole),
  FOREIGN KEY (apressusername) REFERENCES deptusers(apressusername)
    ON DELETE CASCADE 
) TYPE=InnoDB;

INSERT INTO deptusers VALUES ('tomcat', 'tomcat');
INSERT INTO deptusers VALUES ('both', 'tomcat');
INSERT INTO deptusers VALUES ('role1', 'tomcat');
INSERT INTO deptusers VALUES ('admin', SHA('admin'));
#INSERT INTO deptusers VALUES ('admin', MD5('admin'));
#INSERT INTO deptusers VALUES ('admin', 'd033e22ae348aeb5660fc2140aec35850c4da997');

INSERT INTO deptroles VALUES ('tomcat', 'tomcat');
INSERT INTO deptroles VALUES ('both', 'tomcat');
INSERT INTO deptroles VALUES ('both', 'role1');
INSERT INTO deptroles VALUES ('role1', 'role1');
INSERT INTO deptroles VALUES ('admin', 'admin');
