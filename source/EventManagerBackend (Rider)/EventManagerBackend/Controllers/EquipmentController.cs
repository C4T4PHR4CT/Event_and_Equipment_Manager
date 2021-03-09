using System;
using System.Linq;
using EventManagerBackend.Data;
using EventManagerBackend.Model;
using EventManagerBackend.Model.Exception;
using Microsoft.AspNetCore.Mvc;

namespace EventManagerBackend.Controllers
{
    [ApiController]
    public class EquipmentController : ControllerBase
    {
        private readonly IPersistenceService _persistence;
        
        public EquipmentController(IPersistenceService persistence)
        {
            _persistence = persistence;
        }

        [HttpGet]
        [Route("equipment")]
        public IActionResult GetEquipments([FromQuery] int? eventId)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                User user = (User) HttpContext.Items["User"];
                if (user.PermissionLevel < 2)
                    throw new UnauthorizedException("You don't have high enough clearance for this operation!");
                Equipment[] temp = _persistence.GetEquipments(eventId, user.OrganizationId);
                return StatusCode(200, temp);
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }
        
        [HttpPost]
        [Route("equipment")]
        public IActionResult PostEquipment([FromBody] Equipment equipment)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                User user = (User) HttpContext.Items["User"];
                if (user.PermissionLevel < 3)
                    throw new UnauthorizedException("You don't have high enough clearance for this operation!");
                int eqId = _persistence.AddEquipment(equipment, (int) user.OrganizationId);
                if (equipment.Events == null) return StatusCode(200);
                string error = "";
                foreach (Event ev in equipment.Events)
                {
                    try
                    {
                        if (ev.Id != null)
                            _persistence.AddEquipmentEventConnection(eqId, (int) ev.Id);
                    }
                    catch (ConflictException e)
                    {
                        error = e.Message + "\n";
                    }
                }
                if (error != "")
                    throw new ConflictException(error.Substring(0, error.Length - 1));
                return StatusCode(200);
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }
        
        [HttpGet]
        [Route("equipment/{equipmentId}")]
        public IActionResult GetEquipment([FromRoute] int equipmentId)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                User user = (User) HttpContext.Items["User"];
                if (user.PermissionLevel < 2)
                    throw new UnauthorizedException("You don't have high enough clearance for this operation!");
                if (_persistence.GetEquipmentOrgId(equipmentId) != user.OrganizationId && user.OrganizationId != null)
                    throw new UnauthorizedException("The requested Equipment is owned by a different organization!");
                Equipment temp = _persistence.GetEquipment(equipmentId);
                temp.Events = _persistence.GetEvents(equipmentId, null, null, null);
                return StatusCode(200, temp);
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }
        
        [HttpDelete]
        [Route("equipment/{equipmentId}")]
        public IActionResult DeleteEquipment([FromRoute] int equipmentId)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                User user = (User) HttpContext.Items["User"];
                if (user.PermissionLevel < 3)
                    throw new UnauthorizedException("You don't have high enough clearance for this operation!");
                if (_persistence.GetEquipmentOrgId(equipmentId) != user.OrganizationId && user.OrganizationId != null)
                    throw new UnauthorizedException("The requested Equipment is owned by a different organization!");
                _persistence.DeleteEquipment(equipmentId);
                return StatusCode(200);
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }
        
        [HttpPut]
        [Route("equipment/{equipmentId}")]
        public IActionResult PutEquipment([FromRoute] int equipmentId, [FromBody] Equipment equipment)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                User user = (User) HttpContext.Items["User"];
                if (user.PermissionLevel < 3)
                    throw new UnauthorizedException("You don't have high enough clearance for this operation!");
                if (_persistence.GetEquipmentOrgId(equipmentId) != user.OrganizationId && user.OrganizationId != null)
                    throw new UnauthorizedException("The requested Equipment is owned by a different organization!");
                equipment.Id = equipmentId;
                _persistence.UpdateEquipment(equipment);
                if (equipment.Events == null) return StatusCode(200);
                Event[] oldEvents = _persistence.GetEvents(equipmentId, null, null, null);
                foreach (Event oldEv in oldEvents)
                {
                    bool contains = equipment.Events.Any(newEv => oldEv.Id == newEv.Id);
                    if (contains) continue;
                    _persistence.DeleteEquipmentEventConnection(equipmentId, (int) oldEv.Id);
                }
                string error = "";
                foreach (Event newEv in equipment.Events)
                {
                    bool contains = oldEvents.Any(oldEv => newEv.Id == oldEv.Id);
                    if (contains) continue;
                    try
                    {
                        if (newEv.Id != null)
                            _persistence.AddEquipmentEventConnection(equipmentId, (int) newEv.Id);
                    }
                    catch (ConflictException e)
                    {
                        error = e.Message + "\n";
                    }
                }
                if (error != "")
                    throw new ConflictException(error.Substring(0, error.Length - 1));
                return StatusCode(200);
            }
            catch (NotFoundException e)
            {
                return StatusCode(404, e.Message);
            }
            catch (ConflictException e)
            {
                return StatusCode(409, e.Message);
            }
            catch (UnauthorizedException e)
            {
                return StatusCode(403, e.Message);
            }
            catch (Exception e)
            {
                return StatusCode(500, e.Message);
            }
        }
    }
}