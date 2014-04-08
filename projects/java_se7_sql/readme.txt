Case study: Java SE 7 , SQL and NoSQL DBs
Inception: [2014-01-15] 

Goal: Based on sample of event logging entity show how different combinations of industry popular
      frameworks(frmk-s)/approaches can be used to handle this basic entity CRUD in different DBs.
	  
1. High level overview
For 1st iterations some of these combinations will be tried:
  (lang: Java SE v7) (frmk: JDBC v4+ | JPA v2+ | Spring v4+ | Hibernate v4+ | Mybatis v3+) 
  (DB: SQL: (derby v10+ | mysql v5+| pgsql v9+), NoSQL: (Cassandra v2+)) (config: XML | Annotations).

2. Design
Main classes to play around:
  Event(id: long, category: EventCategory, severityLevel: enum(INFO/WARN/ERROR,FATAL)
        , source: string, processId: string, title: short_string, created: date_time).
  EventCategory(code: string, name: string). 

Tables resemble these classes.  
NOTE: large size field will be added in next iterations, be aware..

3. Folders:
  - commons: Common classes, utils etc.
  - api: API interfaces.
  - approaches: Frmk specific ways to be used can be found there.
  - apistudy: API impl test module. It's configured to use one of approaches submodules. 
  - conf: sample configs e.g. for logging.
  - tmp: temp folder; can have e.g. log files.

 
4. Build/test  
Tests to be performed this way:
 1. Select subproject(maven module) dependency towards desired combination.
 2. Select particular DB configuration (and make sure that DB is running and in good shape).
 3. Perform tests.
 
5. Disclaimer
Project is in status "pet, for play around", can be removed from VCS at any time without warning.

6. Version history
----------
0.0.1.RELEASE | 2014-01-22
 - Mocked and JDBC approaches were finished. Performance tracker was added.
1.0.0.RELEASE | 2014-01-28
 - All planned Java7/SQL approaches were added and tested for all DBs.
1.1.0.RELEASE | 2014-02-10
 - Cassandra/CQL approach is added and tested.
2.0.0.RELEASE | 2014-03-05
 - Last planned approach - Solr is added and tested. 
   More polishing is expected to be performed though..and unplanned additions if time allows (e.g. MongoDB, Neo4J).
----------

 