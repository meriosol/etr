Case study: Java SE 7 , SQL and NoSQL DBs
Inception: [2014-01-15] 

#-------------------------------
1. Folders:
  - commons: Common classes, utils etc.
  - api: API interfaces.
  - approaches: Frmk specific ways to be used can be found there.
  - apistudy: API impl test module. It's configured to use one of approaches submodules. 
  - conf: sample configs e.g. for logging.
  - tmp: temp folder; can have e.g. log files.
  
NOTE: Dirs commons and api expect to have minimal dependencies.

#-------------------------------
2. API study:
Module apistudy by design depends on one of 'approaches' submodules. 
To configure which one to use in apistudy, amend of maven pom dependencies is needed. 
For instance, if you plan to test jdbc, set this:
    <dependencies>
        <dependency>
            <groupId>com.meriosol</groupId>
            <artifactId>etr-mocked</artifactId>
			<!-- TIP: Copy particular persistence approach you'd like to test in artifactId tag.
            <artifactId>etr-solr-direct</artifactId>
            <artifactId>etr-cassandra-cql</artifactId>
            <artifactId>etr-hibernate-jpa-annot</artifactId>
            <artifactId>etr-hibernate-spring-jpa-annot</artifactId>
            <artifactId>etr-hibernate-spring-annot</artifactId>
            <artifactId>etr-hibernate-spring-xml</artifactId>
            <artifactId>etr-hibernate-xml</artifactId>
            <artifactId>etr-spring-templates</artifactId>
            <artifactId>etr-mybatis-annot-spring</artifactId>
            <artifactId>etr-mybatis-annot</artifactId>
            <artifactId>etr-mybatis-xml</artifactId>
            <artifactId>etr-jdbc</artifactId>
            <artifactId>etr-mocked</artifactId>
			-->
            <version>2.2-RELEASE</version>
        </dependency>
    </dependencies>

NOTE: each impl should have ....dao.EventDaoFactory class, which should return alive and kicking EventDao impl.

#-------------------------------
3. Quick start guide:

3.1. Prerequisites (as of Jan 2014):
 - Java SE 7 +.
 - Maven 3.1 +.
 - Sample DB schemas got installed. NOTE: the simplest is javadb (no real DB is needed to be installed).
 
3.2. Command line maven test sample:
export JAVA_HOME='/path/to/Java/jdk1.7'
M2_HOME=/path/to/apache-maven-3.1.1
M2=$M2_HOME/bin 
MAVEN_OPTS='-Xmx1024m -XX:MaxPermSize=256m'
PATH=$PATH:$M2

GITHUB_HOME=/path/to/github

cd ${GITHUB_HOME}/etr/projects/java_se7_sql
mvn -e clean test

If all passed fine, you'll see something like that:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Results :

Tests run: 1, Failures: 0, Errors: 0, Skipped: 0

-------------------------------------------------------
 T E S T S
-------------------------------------------------------

Results :

Tests run: 0, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] ETR for Java SE7 .................................. SUCCESS [0.515s]
[INFO] ETR Commons ....................................... SUCCESS [7.265s]
[INFO] ETR API ........................................... SUCCESS [1.179s]
[INFO] ETR hibernate/jpa annotations ..................... SUCCESS [2.074s]
[INFO] ETR API Study ..................................... SUCCESS [21.390s]
[INFO] ETR mocked ........................................ SUCCESS [1.947s]
[INFO] ETR jdbc .......................................... SUCCESS [1.593s]
[INFO] ETR mybatis XML ................................... SUCCESS [1.574s]
[INFO] ETR mybatis Annotations ........................... SUCCESS [19.924s]
[INFO] ETR mybatis Annotations Spring .................... SUCCESS [2.740s]
[INFO] ETR Spring ........................................ SUCCESS [2.855s]
[INFO] ETR hibernate XML ................................. SUCCESS [1.803s]
[INFO] ETR hibernate/spring XML .......................... SUCCESS [2.441s]
[INFO] ETR hibernate/spring annotations .................. SUCCESS [1.907s]
[INFO] ETR hibernate/spring/jpa annotations .............. SUCCESS [2.780s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 1:12.409s
[INFO] Finished at: Thu Jan 30 04:07:56 EST 2014
[INFO] Final Memory: 76M/209M
[INFO] ------------------------------------------------------------------------~~~~~~~~~~~~~~~~~~~~~~~~~~~~

To review code, you can load IDEA(CE is free) and open maven pom there.
#-------------------------------

 