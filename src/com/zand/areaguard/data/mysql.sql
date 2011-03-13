CREATE TABLE IF NOT EXISTS `<tablePrefix>Worlds` ( 
Id int NOT NULL AUTO_INCREMENT, 
Name varchar(32) NOT NULL,
PRIMARY KEY (Id), 
INDEX(Name), 
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `<tablePrefix>Areas` ( 
Id int NOT NULL AUTO_INCREMENT, 
WorldId int NOT NULL, 
Name varchar(32) NOT NULL,
Priority int NOT NULL DEFAULT 0,
ParrentId int NOT NULL  DEFAULT -1, 
x1 int NOT NULL, 
y1 int NOT NULL, 
z1 int NOT NULL, 
x2 int NOT NULL, 
y2 int NOT NULL, 
z2 int NOT NULL, 
PRIMARY KEY (Id), 
INDEX(Name), 
INDEX(Priority), 
INDEX(x1) 
) ENGINE=InnoDB;


CREATE TABLE IF NOT EXISTS `<tablePrefix>Msgs` ( 
AreaId int NOT NULL, 
Name varchar(16) NOT NULL, 
Msg varchar(100), 
INDEX(AreaId), 
INDEX(Name) 
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS `<tablePrefix>Lists` ( 
AreaId int NOT NULL, 
List varchar(16) NOT NULL, 
Value varchar(64) NOT NULL, 
INDEX(AreaId), 
INDEX(List), 
INDEX(Value) 
) ENGINE=InnoDB;
