Case study: Java SE 7 , SQL DBs
Inception: [2014-01-15] 

1. Folders:
  - commons: Common classes, utils etc.
  - api: API interfaces.
  - approaches: Frmk specific ways to be used can be found there.
  - apistudy: API impl test module. It's configured to use one of approaches submodules. 
  - conf: sample configs e.g. for logging.
  - tmp: temp folder; can have e.g. log files.
  
NOTE: Dirs commons and api expect to have minimal dependencies.

2. API study:
Module apistudy by design depends on one of 'approaches' submodules. 
To configure which one to use in apistudy, amend of maven pom dependencies is needed. 
For instance, if you plan to test jdbc, set this:
    <dependencies>
        <dependency>
            <groupId>com.meriosol</groupId>
            <artifactId>etr-jdbc</artifactId>
			<!-- TIP: Uncomment particular persistence approach you'd like to test:
            <artifactId>etr-jdbc</artifactId>
            <artifactId>etr-mocked</artifactId>
			-->
            <version>0.0.2-SNAPSHOT</version>
        </dependency>
    </dependencies>

NOTE: each impl should have ....dao.EventDaoFactory class, which should return alive and kicking EventDao impl.

  