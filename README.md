ETR - Event Tracker PoC
===
Project short names: <tt>ETR, etr.</tt>
Inception year: <tt>2014</tt>

## Goal
Based on sample of event logging entity show how different combinations of industry popular
frameworks/approaches can be used to handle this basic entity CRUD in different architectures, 
ranging from container-less Java SE to web full blown JEE stacks; web services(both SOAP and RESTful/Multimedia APIs), 
async messaging(through JMS / message beans), even non Java event producers and consumers.

## Overview
Through use basic domain model for events and their categories this set of projects shows how they can be stored, transferred and viewed.
Event model was selected as more or less generic and abstract. Events are everywhere. Modern UIs are full of event handling. 
Interaction with backend is shifting from request/response paradigm towards async notification/push(hence events again).
WebSocket over TCP/IP is the one of the hottest topics among teams who develops close to real-time web/mobile apps. So it would be cool to see how it work out for real-time events.

Main emphasize of this project is to check how combination of view/store approaches works from non-functional prospective, 
how systems need to be configured for optimal use, what is more productive for development etc. 
It should allow to start quickly more practical projects where event processing is significant part of solution.

Subprojects can be considered as autonomous bundles of *configuration* + *code* + *tests*. 
Tests will be designed in way which more leaned towards "set this particular config and launch test" in contrast to automatic unit tests (popular in CI like Jenkins). 

## Persistence subprojects
Combinations of ORM and persistence frameworks embraced so far(root path **projects/java_se7_sql**):
 - hibernate-jpa-annotations
 - hibernate-spring-jpa-annotations
 - hibernate-spring-annotations
 - hibernate-spring-xml
 - hibernate-xml
 - spring-templates
 - mybatis-annotations-spring
 - mybatis-annotations
 - mybatis-xml
 - plain old hardcore jdbc
 - cassandra-cql
 - solr-direct

### Some notable features touched in persistence modules:
 - Transaction support.
 - Auto-incremented PKs.
 - Java enum handling.
 - Date fields.
 - Many-to-one entity relationships (event->category). 
 - Different databases (only runtime dependencies on them as mandatory feature).
 - Configuration (e.g. DB connection settings). The lesser hard-coded params the better(but as this is research project, no ideal "configure all" is expected).
 - DDL and DML scripts for sample tables/cores and basic data load.
 - Versions quick pic: Java SE7, JDBC 4, JPA 2, Spring 4.0.+, Mybatis 3.2.+, Hibernate 4.3.+, MySQL 5+, Postgres 9+, Cassandra 2+, Solr 4.7+.

> NOTE: For SQL DBs persistence combination **etr-hibernate-spring-xml** looks like contains the least configuration and boilerplate code as it can be seen from that subproject.

## XML subprojects
Under path **projects/xmlius**:
 - etr_jaxb (JAXB generation and marshalling shown up there).
 - etr_parsing (XML parsing and storing techniques such as DOM, SAX, StAX, XPath; with XSD validation).
 - etr_xsl (XSLT, XSL-FO to PDF transformation samples).

## Performance metrics
If you perform tests for e.g. **apistudy** module, you notice JSON constructs in log file, like this one:
```JSON
{"name": "EventDaoTest__JdbcImpl", "created": "2014-02-01 20:14:18.582", "totalDuration": 32,  "milestones":
    [
        {"name": "BEGIN", "durationSinceStart": 0, "durationSincePrev": 0, "memory": {"max": 1804, "total": 122, "used": 19}},
        {"name": "testEventCreateAndUpdate.BEGIN", "durationSinceStart": 0, "durationSincePrev": 0, "memory": {"max": 1804, "total": 122, "used": 19}},
        {"name": "testEventCreateAndUpdate.AFTER_createTestingEvent", "durationSinceStart": 18, "durationSincePrev": 18, "memory": {"max": 1804, "total": 122, "used": 19}},
        {"name": "testEventCreateAndUpdate.AFTER_event_update", "durationSinceStart": 32, "durationSincePrev": 14, "memory": {"max": 1804, "total": 122, "used": 19}},
        {"name": "testEventCreateAndUpdate.END", "durationSinceStart": 32, "durationSincePrev": 0, "memory": {"max": 1804, "total": 122, "used": 19}},
        {"name": "END", "durationSinceStart": 32, "durationSincePrev": 0, "memory": {"max": 1804, "total": 122, "used": 19}}
    ]
}
```

These are basically duration and (heap) memory consumption metrics. Idea was to report rough metrics for potential resource consumption estimates of different approaches.
If to gather these reports in some DB (MongoDB?), interesting statistics can be revealed for combinations of approaches and underlying backend systems.

## Dependency highlights
Notice that in order to increase modularity and reusability some former internal projects were moved to external libraries.

### MuTasker
MuTasker is about running tasks using pool of threads. It's external library [MuTasker](https://github.com/meriosol/mutasker) (multi-threaded "tasker").
This lib utilizes Java Concurrency package and provides a few abstracts to deal with generic tasks. 
Gathering of execution time performance statistics and load testing itself are main features of this lib.
> NOTE: In order to build ETR, because of dependency on **mutasker** in **projects/java_se7_sql/apistudy** module you need to clone and install this lib on your local maven repo first.

### JaxbUtil
XML and web service related subprojects can depend on external library [JaxbUtil](https://github.com/meriosol/jaxbutil).
The library provides thin generic wrapper around JAXB framework internals.
For now it's utilized in ETR JAXB subproject **projects/xmlius/etr_jaxb** (means you need to install this lib in local maven repo first).
Sample usage from unit test:
```Java
EtrUnmarshallHelper<Event> eventTrackerMarshallingHelper = new EtrUnmarshallHelper<>(validationErrorTolerant);
Event event = eventTrackerMarshallingHelper.unmarshallFromResourcePath(eventResourcePath, Event.class);
```
Class **Event** is an example of generated (or created by hand) JAXB POJO.
The library is POJO agnostic because of generics binding(it also supports type safety).

## Folders overview
 - **configs**: Backend configs.  
    - **sql_db_samples**: has OS command samples, DDL/DML scripts for managing sample DBs and ETR tables.
    - **no_sql_db_samples**: has OS command samples, DDL/DML scripts for managing sample ETR NoSQL DBs.
 - **docs**: Project docs. Dir docs/html has mini site explaining different aspects of case studies for ETR.
 - **models**: System/DB Design models. UML and ERD are main contenders. For UML 'WhiteStar UML' free app is used for now.
 - **projects**: Case studies grouped by technologies used. They are generally have maven root poms, can be opened in e.g. IDEA IDE.
    They, in turn, have deep dir structures to show usage combinations. Hope you don't get lost :)..  
    - **java_se7_sql**: For SQL and NoSQL DBs, using Java SE 7.
	- **xmlius**: For ETR event XML handling approaches(also it's preparation to SOAP exploration, with WSDLs/XSDs etc.. maybe some BPEL too).
 - **runtimes**: DB runtime launch samples
    - **javadb**: JavaDB(apache derby) runtime folder. Both windows and linux env-s are embraced. 
    - **cassandra**: Cassandra runtime folder. Both windows and linux env-s are embraced.
    - **solr**: Solr runtime folder. Both windows and linux env-s are embraced.
	   
Env specific folders can be copied somewhere outside this code tree and used as foundation for sample database (eventium).

## Further reading
 - Look for readme.txt in different folders. They provide context specific guidelines.
 - See also [PDF](docs/pdf/etr.pdf)(generated from HTML docs) or [HTML docs themselves](docs/html/index.html) (in web browser once you clone git repo).

 
 
 
 