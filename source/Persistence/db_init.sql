CREATE SCHEMA event_and_equipment_manager AUTHORIZATION postgres;

SET search_path TO event_and_equipment_manager;

CREATE TABLE _permission(
    pe_id serial PRIMARY KEY,
    pe_name varchar(32) NOT NULL
);

CREATE TABLE _organization (
    og_id serial PRIMARY KEY,
    og_name varchar(32) NOT NULL
);

CREATE TABLE _user (
	us_id serial PRIMARY KEY,
    us_name varchar(32) NOT NULL,
    us_password varchar(32) NOT NULL,
    us_permission integer REFERENCES _permission(pe_id) ON DELETE CASCADE NOT NULL,
    us_organization integer REFERENCES _organization(og_id) ON DELETE CASCADE
);

CREATE TABLE _event (
	ev_id serial PRIMARY KEY,
    ev_name varchar(32) NOT NULL,
    ev_hidden boolean DEFAULT false,
    ev_start timestamp NOT NULL,
    ev_end timestamp CHECK (ev_start < ev_end OR ev_end IS NULL),
    ev_organization integer REFERENCES _organization(og_id) ON DELETE CASCADE NOT NULL
);
CREATE FUNCTION _event_calculate_end () RETURNS trigger AS $$
BEGIN
  IF (NEW.ev_end IS NULL) THEN
    UPDATE _event SET ev_end = (NEW.ev_start + INTERVAL '3 days') WHERE ev_id = NEW.ev_id;
  END IF;
  RETURN NEW;
END; $$ LANGUAGE plpgsql;
CREATE TRIGGER _event_default_end
  AFTER INSERT OR UPDATE ON _event
  FOR EACH ROW EXECUTE PROCEDURE _event_calculate_end();

CREATE TABLE _equipment (
    eq_id serial PRIMARY KEY,
    eq_name varchar(512) NOT NULL,
    eq_category varchar(32),
    eq_note varchar(512),
    eq_organization integer REFERENCES _organization(og_id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE _event_equipment_connection (
	ev_id integer REFERENCES _event(ev_id) ON DELETE CASCADE,
    eq_id integer REFERENCES _equipment(eq_id) ON DELETE CASCADE,
    eq_handout boolean DEFAULT true
);
ALTER TABLE _event_equipment_connection ADD CONSTRAINT
PK_event_equipment_connection PRIMARY KEY(ev_id,eq_id);