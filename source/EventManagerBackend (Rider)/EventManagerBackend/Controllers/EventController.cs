using System;
using EventManagerBackend.Data;
using EventManagerBackend.Model;
using EventManagerBackend.Model.Exception;
using Microsoft.AspNetCore.Mvc;

namespace EventManagerBackend.Controllers
{
    [ApiController]
    public class EventController : ControllerBase
    {
        private readonly IPersistenceService _persistence;
        
        public EventController(IPersistenceService persistence)
        {
            _persistence = persistence;
        }

        [HttpGet]
        [Route("event")]
        public IActionResult GetEvents([FromQuery] int? equipmentId, [FromQuery] long? from,[FromQuery] long? until)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                User user = (User) HttpContext.Items["User"];
                if (user.PermissionLevel < 1)
                    throw new UnauthorizedException("You don't have high enough clearance for this operation!");
                Event[] temp = _persistence.GetEvents(equipmentId, from, until, user.OrganizationId);
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
        [Route("event")]
        public IActionResult PostEvent([FromBody] Event @event)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                User user = (User) HttpContext.Items["User"];
                if (user.PermissionLevel < 3)
                    throw new UnauthorizedException("You don't have high enough clearance for this operation!");
                int evId = _persistence.AddEvent(@event, (int) user.OrganizationId);
                if (@event.Equipments == null) return StatusCode(200);
                string error = "";
                foreach (Equipment eq in @event.Equipments)
                {
                    try
                    {
                        if (eq.Id != null)
                            _persistence.AddEquipmentEventConnection((int) eq.Id, evId);
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
        [Route("event/{eventId}")]
        public IActionResult GetEvent([FromRoute] int eventId)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                User user = (User) HttpContext.Items["User"];
                if (user.PermissionLevel < 2)
                    throw new UnauthorizedException("You don't have high enough clearance for this operation!");
                if (_persistence.GetEventOrgId(eventId) != user.OrganizationId && user.OrganizationId != null)
                    throw new UnauthorizedException("The requested Event is owned by a different organization!");
                Event temp = _persistence.GetEvent(eventId);
                temp.Equipments = _persistence.GetEquipments(eventId, null);
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
        [Route("event/{eventId}")]
        public IActionResult DeleteEvent([FromRoute] int eventId)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                User user = (User) HttpContext.Items["User"];
                if (user.PermissionLevel < 3)
                    throw new UnauthorizedException("You don't have high enough clearance for this operation!");
                if (_persistence.GetEventOrgId(eventId) != user.OrganizationId && user.OrganizationId != null)
                    throw new UnauthorizedException("The requested Event is owned by a different organization!");
                _persistence.DeleteEvent(eventId);
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
        [Route("event/{eventId}")]
        public IActionResult PutEvent([FromRoute] int eventId, [FromBody] Event @event)
        {
            try
            {
                if (HttpContext.Items["User"] == null)
                    throw new UnauthorizedException("Authorization failed!");
                User user = (User) HttpContext.Items["User"];
                if (user.PermissionLevel < 3)
                    throw new UnauthorizedException("You don't have high enough clearance for this operation!");
                if (_persistence.GetEventOrgId(eventId) != user.OrganizationId && user.OrganizationId != null)
                    throw new UnauthorizedException("The requested Event is owned by a different organization!");
                @event.Id = eventId;
                _persistence.UpdateEvent(@event);
                if (@event.Equipments == null) return StatusCode(200);
                _persistence.DeleteAllEquipmentEventConnection(eventId);
                string error = "";
                foreach (Equipment eq in @event.Equipments)
                {
                    try
                    {
                        if (eq.Id != null)
                            _persistence.AddEquipmentEventConnection((int) eq.Id, eventId);
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