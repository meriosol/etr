ETR - Event Tracker PoC
===
Project short names: <tt>ETR, etr.</tt>
Inception year: <tt>2014</tt>

=== 
Goal
<div style="color:MediumBlue">
Based on sample of event logging entity show how different combinations of industry popular
frameworks/approaches can be used to handle this basic entity CRUD in different DBs.
</div>

=== 
Overview
Long term goal is to use basic domain model for events and their categories to show how they can be stored, transferred or shown.
Main emphasize of project is to check how combination of view/store approaches works from non-functional prospective, how system needs to be configured for optimal use and what is better productive development etc.

=== 
Persistence subprojects
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

==== 
Persistence subprojects
Some notable features touched there:
 - Transaction support.
 - Auto-incremented PKs.
 - Enum handling.
 - Date fields.
 - Many-to-one entity relationships (event->category). 
 - Different databases (only runtime dependencies on them).
 - Configuration of "unstable" artifacts (from env view e.g. DB connection settings).
 - DDL and DML scripts for sample tables and basic data load.
 - Versions quick pic: Java SE7, JDBC 4, JPA 2, Spring 4.0.+, Mybatis 3.2.+, Hibernate 4.3.+

==== 
More docs
See [HTML docs](docs/html/index.html).