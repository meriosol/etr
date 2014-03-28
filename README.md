ETR - Event Tracker PoC
===
Project short names: <tt>ETR, etr.</tt>
Inception year: <tt>2014</tt>

## Goal
Based on sample of event logging entity show how different combinations of industry popular
frameworks/approaches can be used to handle this basic entity CRUD in different architectures, ranging from container-less Java SE to web full blown JEE stacks; web services(both SOAP and RESTful/Multimedia APIs), async messaging(through JMS / message beans), even non Java event producers and consumers.

## Overview
Through use basic domain model for events and their categories this set of projects shows how they can be stored, transferred and viewed.
Event model was selected as mmore or less generic and abstract. Events are eveywhere. Modern UIs are full of event handling. Interaction with backend is shifting from request/response paradigm towards async notificatioon/push(WebSocket is the one of the hottest topics among teams who develops close to realtime web/mobile apps).

Main emphasize of this project is to check how combination of view/store approaches works from non-functional prospective, 
how system needs to be configured for optimal use, what is more productive for development etc. It should allow to start quickly more practical projects where event processing is significant part of system.

Subprojects can be considered as autonomous bundles of *configuration* + *code* + *tests*.

## Persistence subprojects
Combinations of ORM and persistence frameworks embraced so far:
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

### Some notable features touched there:
 - Transaction support.
 - Auto-incremented PKs.
 - Java enum handling.
 - Date fields.
 - Many-to-one entity relationships (event->category). 
 - Different databases (only runtime dependencies on them).
 - Configuration (e.g. DB connection settings).
 - DDL and DML scripts for sample tables/cores and basic data load.
 - Versions quick pic: Java SE7, JDBC 4, JPA 2, Spring 4.0.+, Mybatis 3.2.+, Hibernate 4.3.+, MySQL 5+, Postgres 9+, Cassandra 2+, Solr 4.7+.

> NOTE: For SQL DBs persistence combination **hibernate-spring-jpa-annotations** looks like contain the least configuration and boilerplate code as it can be seen from that subproject.

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
If to gather these reports in some DB (MongoDB?), interesting stats can be revealed for combinations of approaches and underlying backend systems.

### MuTasker
Another performance meter is actually external reusable library [MuTasker](https://github.com/meriosol/mutasker) - multithreaded "tasker". This lib utilizes Java Concurrency package and provides a few abstracts to deal with generic tasks. Gathering of execution time performance stats and load testing itself are main features of this lib.
> NOTE: In order to build ETR, because of dependency on **mutasker** in **apistudy** module you need to clone and install this lib on your machine first.


## More docs
 - Look for readme.txt in different folders. They provide context specifi guidelines.
 - See also [PDF](docs/pdf/etr.pdf)(generated from HTML docs) or [HTML docs themselves](docs/html/index.html) (in web browser once you clone git repo).