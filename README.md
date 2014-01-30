ETR - Event Tracker PoC
===
Project short names: <tt>ETR, etr.</tt>
Inception year: <tt>2014</tt>

=== 
## Goal
Based on sample of event logging entity show how different combinations of industry popular
frameworks/approaches can be used to handle this basic entity CRUD in different DBs.

=== 
## Overview
Long term goal is to use basic domain model for events and their categories to show how they can be stored, transferred or shown.
Main emphasize of project is to check how combination of view/store approaches works from non-functional prospective, 
how system needs to be configured for optimal use, what is more productive for development etc.

Subprojects can be considered as autonmous bundles of *configuration* + *code* + *tests*.

=== 
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
 - jdbc

> Combination hibernate-spring-jpa-annotations requires minimal configuration and boilerplate code as it can be seen from that subproject.

### Performance metrics
If you perform tests ffrom e.g. apistudy module, you notice in log file JSON constrcust like this one:

> {name: "EventDaoTest__MockedImpl", created: "2014-01-30 04:14:05.063", totalDuration: 11,  milestones:
>     {
>         {name: "BEGIN", durationSinceStart: 0, durationSincePrev: 0, memory:{max: 1804, total: 122, used: 9}}
>         {name: "testEventsRetrieveForPeriodWithEndDateNotNull.BEGIN", durationSinceStart: 1, durationSincePrev: 1, memory:{max: 1804, total: 122, used: 9}}
>         {name: "testEventsRetrieveForPeriodWithEndDateNotNull.END", durationSinceStart: 11, durationSincePrev: 10, memory:{max: 1804, total: 122, used: 9}}
>         {name: "END", durationSinceStart: 11, durationSincePrev: 0, memory:{max: 1804, total: 122, used: 9}}
>     }
> } 

These are basically duration and (heap) memory consumption metrics. Idea was to report rough metrics fpr potential resource consumption estimates of different approaches.
If to gather these reports in some DB (MongoDB?), interesting stats can be revealed for combinations of approaches and underlying DBs.
 
### Some notable features touched there:
 - Transaction support.
 - Auto-incremented PKs.
 - Enum handling.
 - Date fields.
 - Many-to-one entity relationships (event->category). 
 - Different databases (only runtime dependencies on them).
 - Configuration of "unstable" artifacts (from env view e.g. DB connection settings).
 - DDL and DML scripts for sample tables and basic data load.
 - Versions quick pic: Java SE7, JDBC 4, JPA 2, Spring 4.0.+, Mybatis 3.2.+, Hibernate 4.3.+, MySQL 5+, Postgres 9+.

===
## More docs
 - Look for readme.txt in different folders.
 - See (in web browser once clone repo) [HTML docs](docs/html/index.html).