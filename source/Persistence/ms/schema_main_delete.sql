USE EventManager
GO

IF OBJECT_ID('dbo._event_equipment_connection') IS NOT NULL
	TRUNCATE TABLE dbo._event_equipment_connection
IF OBJECT_ID('dbo._event_equipment_connection') IS NOT NULL
	DROP TABLE dbo._event_equipment_connection
IF OBJECT_ID('dbo._equipment') IS NOT NULL
	TRUNCATE TABLE dbo._equipment
IF OBJECT_ID('dbo._equipment') IS NOT NULL
	DROP TABLE dbo._equipment
IF OBJECT_ID('dbo._event') IS NOT NULL
	TRUNCATE TABLE dbo._event
IF OBJECT_ID('dbo._event') IS NOT NULL
	DROP TABLE dbo._event
IF OBJECT_ID('dbo._user') IS NOT NULL
	TRUNCATE TABLE dbo._user
IF OBJECT_ID('dbo._user') IS NOT NULL
	DROP TABLE dbo._user
IF OBJECT_ID('dbo._organization') IS NOT NULL
	TRUNCATE TABLE dbo._organization
IF OBJECT_ID('dbo._organization') IS NOT NULL
	DROP TABLE dbo._organization
IF OBJECT_ID('dbo._permission') IS NOT NULL
	TRUNCATE TABLE dbo._permission
IF OBJECT_ID('dbo._permission') IS NOT NULL
	DROP TABLE dbo._permission
GO