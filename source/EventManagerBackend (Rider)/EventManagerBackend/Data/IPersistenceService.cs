using EventManagerBackend.Model;

namespace EventManagerBackend.Data
{
    public interface IPersistenceService
    {
        public bool CheckUserPassword(int userId, string password);
        public User GetUser(int userId);
        public int GetEventOrgId(int eventId);
        public int GetEquipmentOrgId(int equipmentId);
        public Event[] GetEvents(int? equipmentId, long? from, long? until, int? organization);
        public int AddEvent(Event @event, int organization);
        public Event GetEvent(int id);
        public void DeleteEvent(int id);
        public void UpdateEvent(Event @event);
        public Equipment[] GetEquipments(int? eventId, int? organization);
        public int AddEquipment(Equipment equipment, int organization);
        public Equipment GetEquipment(int id);
        public void DeleteEquipment(int id);
        public void UpdateEquipment(Equipment equipment);
        public void AddEquipmentEventConnection(int equipmentId, int eventId);
        public void DeleteAllEquipmentEventConnection(int eventId);
        public void DeleteEquipmentEventConnection(int equipmentId, int eventId);
        public void CheckIntegrity();
    }
}