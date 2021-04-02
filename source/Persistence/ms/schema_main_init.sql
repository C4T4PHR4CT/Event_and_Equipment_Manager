USE EventManager
GO

CREATE SCHEMA [main]
GO

CREATE TABLE main._permission(
    pe_id int IDENTITY (1, 1) PRIMARY KEY,
    pe_name nvarchar(32) NOT NULL
);

CREATE TABLE main._organization (
    og_id int IDENTITY (1, 1) PRIMARY KEY,
    og_name nvarchar(32) NOT NULL
);

CREATE TABLE main._user (
	us_id int IDENTITY (1, 1) PRIMARY KEY,
    us_name nvarchar(32) NOT NULL UNIQUE,
    us_password nvarchar(32) NOT NULL,
    us_permission int REFERENCES main._permission(pe_id) NOT NULL,
    us_organization int REFERENCES main._organization(og_id)
);

CREATE TABLE main._event (
	ev_id int IDENTITY (1, 1) PRIMARY KEY,
    ev_name nvarchar(32) NOT NULL,
    ev_hidden bit DEFAULT 0,
    ev_start datetime NOT NULL,
    ev_end datetime,
    ev_organization int REFERENCES main._organization(og_id) NOT NULL,
	CONSTRAINT _event_end_check CHECK (ev_start < ev_end OR ev_end IS NULL)
);

CREATE TABLE main._equipment (
    eq_id int IDENTITY (1, 1) PRIMARY KEY,
    eq_name nvarchar(512) NOT NULL,
    eq_category nvarchar(32) DEFAULT '',
    eq_note nvarchar(512) DEFAULT '',
    eq_organization int REFERENCES main._organization(og_id) NOT NULL
);

CREATE TABLE main._event_equipment_connection (
	ev_id int REFERENCES main._event(ev_id),
    eq_id int REFERENCES main._equipment(eq_id),
    eq_handout bit DEFAULT 0,
	PRIMARY KEY (ev_id,eq_id)
);
GO

CREATE TRIGGER main._organization_delete_cascade ON main._organization
INSTEAD OF DELETE AS 
BEGIN
 SET NOCOUNT ON;
 DELETE FROM main._user WHERE us_organization IN (SELECT og_id FROM DELETED)
 DELETE FROM main._event WHERE ev_organization IN (SELECT og_id FROM DELETED)
 DELETE FROM main._equipment WHERE eq_organization IN (SELECT og_id FROM DELETED)
 DELETE FROM main._organization WHERE og_id IN (SELECT og_id FROM DELETED)
END
GO

CREATE TRIGGER main._permission_delete_cascade ON main._permission
INSTEAD OF DELETE AS 
BEGIN
 SET NOCOUNT ON;
 DELETE FROM main._user WHERE us_permission IN (SELECT pe_id FROM DELETED)
 DELETE FROM main._permission WHERE pe_id IN (SELECT pe_id FROM DELETED)
END
GO

CREATE TRIGGER main._event_delete_cascade ON main._event
INSTEAD OF DELETE AS 
BEGIN
 SET NOCOUNT ON;
 DELETE FROM main._event_equipment_connection WHERE ev_id IN (SELECT ev_id FROM DELETED)
 DELETE FROM main._event WHERE ev_id IN (SELECT ev_id FROM DELETED)
END
GO

CREATE TRIGGER main._equipment_delete_cascade ON main._equipment
INSTEAD OF DELETE AS 
BEGIN
 SET NOCOUNT ON;
 DELETE FROM main._event_equipment_connection WHERE eq_id IN (SELECT eq_id FROM DELETED)
 DELETE FROM main._equipment WHERE eq_id IN (SELECT eq_id FROM DELETED)
END
GO

CREATE TRIGGER main._event_end_default ON main._event
AFTER INSERT AS
  UPDATE _event SET ev_end = DATEADD(day, 3, _event.ev_start)
  FROM inserted WHERE _event.ev_id = inserted.ev_id;