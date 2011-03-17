CREATE TABLE IF NOT EXISTS `<tablePrefix>Worlds` ( 
Id INTEGER, 
Name varchar(32) NOT NULL,
PRIMARY KEY (Id) 
);

CREATE TABLE IF NOT EXISTS `<tablePrefix>Areas` ( 
Id INTEGER, 
WorldId int NOT NULL, 
Name varchar(32) NOT NULL, 
Creator varchar(32) NOT NULL, 
ParrentId int NOT NULL DEFAULT -1, 
PRIMARY KEY (Id) 
);

CREATE TABLE IF NOT EXISTS `<tablePrefix>Cuboids` ( 
Id INTEGER, 
AreaId int NOT NULL, 
WorldId int NOT NULL,  
Creator varchar(32) NOT NULL, 
Priority int NOT NULL DEFAULT 0, 
x1 int NOT NULL, 
y1 int NOT NULL, 
z1 int NOT NULL, 
x2 int NOT NULL, 
y2 int NOT NULL, 
z2 int NOT NULL, 
PRIMARY KEY (Id) 
);

CREATE TABLE IF NOT EXISTS `<tablePrefix>Msgs` ( 
AreaId int NOT NULL, 
Name varchar(16) NOT NULL, 
Creator varchar(32) NOT NULL, 
Msg varchar(100) 
);

CREATE TABLE IF NOT EXISTS `<tablePrefix>Lists` ( 
AreaId int NOT NULL, 
Name varchar(16) NOT NULL, 
Creator varchar(32) NOT NULL, 
Value varchar(64) 
NOT NULL 
);
