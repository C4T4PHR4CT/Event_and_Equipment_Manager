# Event & Equipment Manager
Levente Nagy 293115

(the following highlights has been copied form docs/Project_Description.pdf and docs/Project_Report.pdf)

## Project Description
### Background Description
When companies in the AV industry sign contracts with customers and agree on to provide technical background for events, extensive planning and resource allocation must be done to satisfy the customer’s needs.

If the company has enough resources to serve multiple customers in sort timespans (or even parallelly, with events overlapping each other), resource allocation can be hard and prone to human errors.

To further detail the problem, these companies have a finite pool of employees and AV equipments. After the customer stated its needs regarding an event, technical planning begins, regarding what equipment is going to be used to transact the event. After technical planning, planning of human resources begin, as these equipments need to be transported to and back from the event, as well as installed and dismantled, and also supervised during the event. Needless to say, these operations require employees to be executed.

To be able to efficiently allocate resources, software assistance can be introduced, making it sure to avoid human errors, such as assigning equipments or employees to multiple events overlapping each other.

### Problem Statement
Companies in the AV industry have problems allocating finite resources, to multiple events and customers, furthermore, keeping track of equipments’ unavailability, for reasons such as being under repair or maintenance.

• How could such company be sure to not assign equipment or personnel to multiple events overlapping each other?

• How could such company keep track of equipments not being available for various reasons, like being under maintenance or repair?

### Definition of purpose
The purpose is to help companies in the AV industry allocating resources for events, like AV equipments and employees.

### Delimitation
The delimitations of the project are:

• Due to the time restrictions and size of the problem, not all parts will be covered of the detailed issues.

## Project Report
### Analysis
#### Functional Requirements
Must have:
- [x] login
- [x] view existing events
- [x] view event details (including assigned equipments)
- [x] view existing equipments
- [x] view equipment details (including assigned events)
- [x] create events
- [x] delete events
- [x] create equipments
- [x] delete equipments

Should have:
- [x] edit event details
- [x] edit equipment details
- [x] assign/deassign event’s associated equipments
- [x] assign/deassign equipment’s associated events
- [x] view profile

Could have:
- [ ] edit owned organization details
- [ ] edit employees and customers
- [ ] register employees and customers
- [ ] delete employees and customers

Would like to have:
- [ ] edit any organization details
- [ ] create organizations
- [ ] delete organizations
- [ ] edit employers
- [ ] register employers
- [ ] delete employers
- [ ] further admin/developer exclusive operations (detailed in use case diagram)
#### Non-Functional Requirements
Must have:
- [x] persist login details
- [x] persist token to keep login session