USE EventManager
GO

IF OBJECT_ID('main._event_equipment_connection') IS NOT NULL
	TRUNCATE TABLE main._event_equipment_connection
IF OBJECT_ID('main._event_equipment_connection') IS NOT NULL
	DROP TABLE main._event_equipment_connection
IF OBJECT_ID('main._equipment') IS NOT NULL
	TRUNCATE TABLE main._equipment
IF OBJECT_ID('main._equipment') IS NOT NULL
	DROP TABLE main._equipment
IF OBJECT_ID('main._event') IS NOT NULL
	TRUNCATE TABLE main._event
IF OBJECT_ID('main._event') IS NOT NULL
	DROP TABLE main._event
IF OBJECT_ID('main._user') IS NOT NULL
	TRUNCATE TABLE main._user
IF OBJECT_ID('main._user') IS NOT NULL
	DROP TABLE main._user
IF OBJECT_ID('main._organization') IS NOT NULL
	TRUNCATE TABLE main._organization
IF OBJECT_ID('main._organization') IS NOT NULL
	DROP TABLE main._organization
IF OBJECT_ID('main._permission') IS NOT NULL
	TRUNCATE TABLE main._permission
IF OBJECT_ID('main._permission') IS NOT NULL
	DROP TABLE main._permission
DROP SCHEMA [main]
GO