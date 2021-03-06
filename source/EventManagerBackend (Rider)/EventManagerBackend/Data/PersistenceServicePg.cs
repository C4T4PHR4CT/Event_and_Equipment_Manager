﻿using System;
using System.Collections.Generic;
using EventManagerBackend.Model;
using EventManagerBackend.Model.Exception;
using Npgsql;

namespace EventManagerBackend.Data
{
    public class PersistenceServicePg : IPersistenceService
    {
        private readonly IConfigService _config;
        
        public PersistenceServicePg(IConfigService config)
        {
            _config = config;
        }

        public bool CheckUserPassword(string user, string password)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "SELECT us_password FROM _user WHERE us_name = @name";
                command.Parameters.AddWithValue("@name", user);
                var reader = command.ExecuteReader();
                if (reader.Read())
                    return reader.GetString(0) == password;
            }
            return false;
        }

        public int GetOrganization(string organization)
        {
            throw new NotImplementedException();
        }

        public int GetPermissionLevel(string permission)
        {
            throw new NotImplementedException();
        }

        public int CreateUser(User user)
        {
            throw new NotImplementedException();
        }

        public User[] GetAllUsers(int? organizationId)
        {
            throw new NotImplementedException();
        }

        public void DeleteUser(int userId)
        {
            throw new NotImplementedException();
        }

        public void UpdateUser(User user)
        {
            throw new NotImplementedException();
        }

        public User GetUserByName(string user)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "SELECT _user.us_id, _user.us_name, _user.us_permission, _permission.pe_name, _user.us_organization, _organization.og_name FROM _user LEFT JOIN _permission ON (_user.us_permission = _permission.pe_id) LEFT JOIN _organization ON (_user.us_organization = _organization.og_id) WHERE _user.us_name = @name";
                command.Parameters.AddWithValue("@name", user);
                var reader = command.ExecuteReader();
                if (reader.Read())
                {
                    User temp = new User
                    {
                        Id = reader.GetInt32(0),
                        Name = reader.GetString(1),
                        PermissionLevel = reader.GetInt32(2),
                        Permission = reader.GetString(3)
                    };
                    try {
                        temp.OrganizationId = reader.GetInt32(4);
                        temp.Organization = reader.GetString(5);
                    } catch (InvalidCastException) {}
                    return temp;
                }
            }
            throw new NotFoundException("User \"" + user + "\" (id) not found!");
        }

        public User GetUserById(int userId)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "SELECT _user.us_id, _user.us_name, _user.us_permission, _permission.pe_name, _user.us_organization, _organization.og_name FROM _user LEFT JOIN _permission ON (_user.us_permission = _permission.pe_id) LEFT JOIN _organization ON (_user.us_organization = _organization.og_id) WHERE _user.us_id = @id";
                command.Parameters.AddWithValue("@id", userId);
                var reader = command.ExecuteReader();
                if (reader.Read())
                {
                    User temp = new User
                    {
                        Id = reader.GetInt32(0),
                        Name = reader.GetString(1),
                        PermissionLevel = reader.GetInt32(2),
                        Permission = reader.GetString(3)
                    };
                    try {
                        temp.OrganizationId = reader.GetInt32(4);
                        temp.Organization = reader.GetString(5);
                    } catch (InvalidCastException) {}
                    return temp;
                }
            }
            throw new NotFoundException("User \"" + userId + "\" (id) not found!");
        }

        public int GetEventOrgId(int eventId)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "SELECT ev_organization FROM _event WHERE ev_id = @id";
                command.Parameters.AddWithValue("@id", eventId);
                var reader = command.ExecuteReader();
                if (reader.Read())
                    return reader.GetInt32(0);
            }
            throw new NotFoundException("Event " + eventId + " (id) not found!");
        }

        public int GetEquipmentOrgId(int equipmentId)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "SELECT eq_organization FROM _equipment WHERE eq_id = @id";
                command.Parameters.AddWithValue("@id", equipmentId);
                var reader = command.ExecuteReader();
                if (reader.Read())
                    return reader.GetInt32(0);
            }
            throw new NotFoundException("Equipment " + equipmentId + " (id) not found!");
        }

        public Event[] GetEvents(int? equipmentId, long? from, long? until, int? organization)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                string kolbi = "SELECT _event.ev_id, _event.ev_name, _event.ev_hidden, extract(epoch from _event.ev_start), extract(epoch from _event.ev_end) FROM _event LEFT JOIN _event_equipment_connection ON (_event.ev_id = _event_equipment_connection.ev_id) ";
                if (equipmentId == null && organization != null || from != null || until != null || equipmentId != null)
                    kolbi += "WHERE ";
                if (equipmentId == null && organization != null)
                    kolbi += "_event.ev_hidden = false AND ";
                else
                    kolbi += "    ";
                if (from != null || until != null || equipmentId != null || organization != null)
                {
                    if (from != null)
                        kolbi += "to_timestamp(@from) < _event.ev_start AND ";
                    if (until != null)
                        kolbi += "to_timestamp(@until) > _event.ev_end AND ";
                    if (equipmentId != null)
                        kolbi += "_event_equipment_connection.eq_id = @id AND ";
                    if (organization != null)
                        kolbi += "_event.ev_organization = @org AND ";
                }
                kolbi = kolbi.Substring(0, kolbi.Length - 4);
                kolbi += "GROUP BY _event.ev_id ORDER BY _event.ev_start, _event.ev_end, _event.ev_name";
                command.CommandText = kolbi;
                if (from != null)
                    command.Parameters.AddWithValue("@from", from);
                if (until != null)
                    command.Parameters.AddWithValue("@until", until);
                if (equipmentId != null)
                    command.Parameters.AddWithValue("@id", equipmentId);
                if (organization != null)
                    command.Parameters.AddWithValue("@org", organization);
                var reader = command.ExecuteReader();
                List<Event> temp = new List<Event>();
                while (reader.Read())
                {
                    temp.Add(new Event
                    {
                        Id = reader.GetInt32(0),
                        Name = reader.GetString(1),
                        Hidden = reader.GetBoolean(2),
                        Start = (long) reader.GetDouble(3),
                        End = (long) reader.GetDouble(4)
                    });
                }
                return temp.ToArray();
            }
        }

        public int AddEvent(Event @event, int organization)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "INSERT INTO _event (ev_name, ev_hidden, ev_start, ev_end, ev_organization) VALUES (@name, @hidden, to_timestamp(@start) at time zone 'UTC', to_timestamp(@end) at time zone 'UTC', @org)";
                command.Parameters.AddWithValue("@name", @event.Name);
                command.Parameters.AddWithValue("@hidden", @event.Hidden);
                command.Parameters.AddWithValue("@start", @event.Start);
                command.Parameters.AddWithValue("@end", @event.End);
                command.Parameters.AddWithValue("@org", organization);
                command.ExecuteNonQuery();
                command.CommandText = "SELECT ev_id FROM _event WHERE ev_name = @name AND ev_hidden = @hidden AND ev_start = @start AND ev_end = @end AND ev_organization = @org ORDER BY ev_id DESC LIMIT 1;";
                command.Parameters.AddWithValue("@name", @event.Name);
                command.Parameters.AddWithValue("@hidden", @event.Hidden);
                command.Parameters.AddWithValue("@start", @event.Start);
                command.Parameters.AddWithValue("@end", @event.End);
                command.Parameters.AddWithValue("@org", organization);
                var reader = command.ExecuteReader();
                reader.Read();
                return reader.GetInt32(0);
            }
        }

        public Event GetEvent(int id)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "SELECT ev_id, ev_name, ev_hidden, extract(epoch from ev_start), extract(epoch from ev_end) FROM _event WHERE ev_id = @id";
                command.Parameters.AddWithValue("@id", id);
                var reader = command.ExecuteReader();
                if (reader.Read())
                    return new Event
                    {
                        Id = reader.GetInt32(0),
                        Name = reader.GetString(1),
                        Hidden = reader.GetBoolean(2),
                        Start = (long) reader.GetDouble(3),
                        End = (long) reader.GetDouble(4)
                    };
            }
            throw new NotFoundException("Event " + id + " (id) not found!");
        }

        public void DeleteEvent(int id)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                
                command.CommandText = "SELECT FROM _event WHERE ev_id = @id";
                command.Parameters.AddWithValue("@id", id);
                var reader = command.ExecuteReader();
                if (!reader.Read())
                    throw new NotFoundException("Event " + id + " (id) not found!");
                
                command.CommandText = "DELETE FROM _event WHERE ev_id = @id";
                command.Parameters.AddWithValue("@id", id);
                command.ExecuteNonQuery();
            }
        }

        public void UpdateEvent(Event @event)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                
                command.CommandText = "SELECT FROM _event WHERE ev_id = @id";
                command.Parameters.AddWithValue("@id", @event.Id);
                var reader = command.ExecuteReader();
                if (!reader.Read())
                    throw new NotFoundException("Event " + @event.Id + " (id) not found!");
                
                command.CommandText = "UPDATE _event SET ev_name = @name, ev_hidden = @hidden, ev_start = to_timestamp(@start) at time zone 'UTC', ev_end = to_timestamp(@end) at time zone 'UTC' WHERE ev_id = @id";
                command.Parameters.AddWithValue("@id", @event.Id);
                command.Parameters.AddWithValue("@name", @event.Name);
                command.Parameters.AddWithValue("@hidden", @event.Hidden);
                command.Parameters.AddWithValue("@start", @event.Start);
                command.Parameters.AddWithValue("@end", @event.End);
                command.ExecuteNonQuery();
            }
        }

        public Equipment[] GetEquipments(int? eventId, int? organization)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                string kolbi = "SELECT _equipment.eq_id, _equipment.eq_name, _equipment.eq_category FROM _equipment LEFT JOIN _event_equipment_connection ON (_equipment.eq_id = _event_equipment_connection.eq_id) ";
                if (eventId != null || organization != null)
                {
                    kolbi += "WHERE ";
                    if (eventId != null)
                        kolbi += "_event_equipment_connection.ev_id = @id AND ";
                    if (organization != null)
                        kolbi += "_equipment.eq_organization = @org AND ";
                    kolbi = kolbi.Substring(0, kolbi.Length - 4);
                }
                kolbi += "GROUP BY _equipment.eq_id ORDER BY _equipment.eq_category, SUBSTR(_equipment.eq_name, 1, 8)";
                command.CommandText = kolbi;
                if (eventId != null)
                    command.Parameters.AddWithValue("@id", eventId);
                if (organization != null)
                    command.Parameters.AddWithValue("@org", organization);
                var reader = command.ExecuteReader();
                List<Equipment> temp = new List<Equipment>();
                while (reader.Read())
                {
                    temp.Add(new Equipment
                    {
                        Id = reader.GetInt32(0),
                        Name = reader.GetString(1),
                        Category = reader.GetString(2)
                    });
                }
                return temp.ToArray();
            }
        }

        public int AddEquipment(Equipment equipment, int organization)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "INSERT INTO _equipment (eq_name, eq_category, eq_organization) VALUES (@name, @category, @org)";
                command.Parameters.AddWithValue("@name", equipment.Name);
                command.Parameters.AddWithValue("@category", equipment.Category);
                command.Parameters.AddWithValue("@org", organization);
                command.ExecuteNonQuery();
                command.CommandText = "SELECT eq_id FROM _equipment WHERE eq_name = @name AND eq_category = @category AND eq_organization = @org ORDER BY ev_id DESC LIMIT 1;";
                command.Parameters.AddWithValue("@name", equipment.Name);
                command.Parameters.AddWithValue("@category", equipment.Category);
                command.Parameters.AddWithValue("@org", organization);
                var reader = command.ExecuteReader();
                reader.Read();
                return reader.GetInt32(0);
            }
        }

        public Equipment GetEquipment(int id)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "SELECT eq_id, eq_name, eq_category FROM _equipment WHERE eq_id = @id";
                command.Parameters.AddWithValue("@id", id);
                var reader = command.ExecuteReader();
                if (reader.Read())
                    return new Equipment
                    {
                        Id = reader.GetInt32(0),
                        Name = reader.GetString(1),
                        Category = reader.GetString(2)
                    };
            }
            throw new NotFoundException("Equipment " + id + " (id) not found!");
        }

        public void DeleteEquipment(int id)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                
                command.CommandText = "SELECT FROM _equipment WHERE eq_id = @id";
                command.Parameters.AddWithValue("@id", id);
                var reader = command.ExecuteReader();
                if (!reader.Read())
                    throw new NotFoundException("Equipment " + id + " (id) not found!");
                
                command.CommandText = "DELETE FROM _equipment WHERE eq_id = @id";
                command.Parameters.AddWithValue("@id", id);
                command.ExecuteNonQuery();
            }
        }

        public void UpdateEquipment(Equipment equipment)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                
                command.CommandText = "SELECT FROM _equipment WHERE eq_id = @id";
                command.Parameters.AddWithValue("@id", equipment.Id);
                var reader = command.ExecuteReader();
                if (!reader.Read())
                    throw new NotFoundException("Equipment " + equipment.Id + " (id) not found!");
                
                command.CommandText = "UPDATE _equipment SET eq_name = @name, eq_category = @category WHERE eq_id = @id";
                command.Parameters.AddWithValue("@id", equipment.Id);
                command.Parameters.AddWithValue("@name", equipment.Name);
                command.Parameters.AddWithValue("@category", equipment.Category);
                command.ExecuteNonQuery();
            }
        }

        public void AddEquipmentEventConnection(int equipmentId, int eventId)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                
                string error = "";
                Event[] events = GetEvents(equipmentId, null, null, null);
                Event newEv = GetEvent(eventId);
                command.CommandText = "SELECT eq_name FROM _equipment WHERE eq_id = @id";
                command.Parameters.AddWithValue("@id", equipmentId);
                var reader = command.ExecuteReader();
                string eqName = "";
                if (reader.Read())
                    eqName = reader.GetString(0);
                if (GetEquipmentOrgId(equipmentId) != GetEventOrgId(eventId))
                    error += "Event " + newEv.Id + " (id) \"" + newEv.Name + "\" is not owned by the same organization, as Equipment " + equipmentId + " (id) \"" + eqName + "\"\n";
                foreach (Event ev in events)
                    if (ev.Start < newEv.Start && ev.End > newEv.Start || ev.Start < newEv.End && ev.End > newEv.End || newEv.Start < ev.Start && newEv.End > ev.Start)
                        error += "Event " + ev.Id + " (id) \"" + ev.Name + "\" conflicts with Event " + newEv.Id + " (id) \"" + newEv.Name + "\" on Equipment " + equipmentId + " (id) \"" + eqName + "\"\n";
                if (error != "")
                    throw new ConflictException(error.Substring(0, error.Length - 1));

                command.CommandText = "INSERT INTO _event_equipment_connection (eq_id, ev_id) VALUES (@eq, @ev)";
                command.Parameters.AddWithValue("@eq", equipmentId);
                command.Parameters.AddWithValue("@ev", eventId);
                try {
                    command.ExecuteNonQuery();
                } catch (InvalidOperationException) {}
            }
        }
        
        public void DeleteAllEquipmentEventConnection(int eventId)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "DELETE FROM _event_equipment_connection WHERE ev_id = @ev";
                command.Parameters.AddWithValue("@ev", eventId);
                command.ExecuteNonQuery();
            }
        }
        
        public void DeleteEquipmentEventConnection(int equipmentId, int eventId)
        {
            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "DELETE FROM _event_equipment_connection WHERE eq_id = @eq AND ev_id = @ev";
                command.Parameters.AddWithValue("@eq", equipmentId);
                command.Parameters.AddWithValue("@ev", eventId);
                command.ExecuteNonQuery();
            }
        }

        public void CheckIntegrity()
        {
            Console.WriteLine("[PersistenceService] Checking Database Integrity...");
            string error = "";
            int orgViolation = 0;
            int eqTimelineViolation = 0;

            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText =
                    "SELECT _event.ev_organization, _equipment.eq_organization, _equipment.eq_id, _event.ev_id FROM _event_equipment_connection LEFT JOIN _event ON (_event_equipment_connection.ev_id = _event.ev_id) LEFT JOIN _equipment ON (_event_equipment_connection.eq_id = _equipment.eq_id)";
                var reader = command.ExecuteReader();
                while (reader.Read())
                    if (reader.GetInt32(0) != reader.GetInt32(1))
                    {
                        orgViolation++;
                        DeleteEquipmentEventConnection(reader.GetInt32(2), reader.GetInt32(3));
                    }

                if (orgViolation != 0)
                    error += "                      - " + orgViolation + " event-equipment organization violation\n";
            }

            using (var connection = new NpgsqlConnection("User ID=" + _config.DbUser + ";Password=" + _config.DbPassword + ";Host=" + _config.DbHost + ";Database=" + _config.DbName + ";Port=" + _config.DbPort))
            {
                connection.Open();
                var command = connection.CreateCommand();
                command.CommandText = "SET search_path TO " + _config.DbSchema;
                command.ExecuteNonQuery();
                command.CommandText = "SELECT _organization.og_id FROM _organization";
                var reader = command.ExecuteReader();
                while (reader.Read())
                {
                    int organization = reader.GetInt32(0);
                    Equipment[] equipments = GetEquipments(null, organization);
                    foreach (Equipment equipment in equipments)
                    {
                        Event[] events = GetEvents(equipment.Id, null, null, organization);
                        for (int i = 0; i < events.Length; i++)
                        for (int j = i + 1; j < events.Length; j++)
                            if (events[i].Start < events[j].Start && events[i].End > events[j].Start || events[i].Start < events[j].End && events[i].End > events[j].End || events[j].Start < events[i].Start && events[j].End > events[i].Start)
                            {
                                eqTimelineViolation++;
                                DeleteEquipmentEventConnection((int) equipment.Id, (int) events[j].Id);
                            }
                    }
                }
                if (eqTimelineViolation != 0)
                    error += "                      - " + eqTimelineViolation + " equipment timeline violation\n";
            }
            
            if (error == "")
                Console.WriteLine("[PersistenceService] Database Integrity Check completed, no errors has been found");
            else
                Console.WriteLine("[PersistenceService] Database Integrity Check completed, the following errors has been found and corrected:\n" + error);
        }

        public void DropSchema()
        {
            throw new NotImplementedException();
        }

        public void InitSchema()
        {
            throw new NotImplementedException();
        }
    }
}