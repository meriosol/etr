Case study: Java SE 7 , SQL DBs
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
            <artifactId>etr-jdbc</artifactId>
			<!-- TIP: Uncomment particular persistence approach you'd like to test:
            <artifactId>etr-mybatis-annot-spring</artifactId>
            <artifactId>etr-mybatis-annot</artifactId>
            <artifactId>etr-mybatis-xml</artifactId>
            <artifactId>etr-jdbc</artifactId>
            <artifactId>etr-mocked</artifactId>
			-->
            <version>0.0.3-SNAPSHOT</version>
        </dependency>
    </dependencies>

NOTE: each impl should have ....dao.EventDaoFactory class, which should return alive and kicking EventDao impl.

#-------------------------------
3. Quick start guide:

3.1. Prerequisites (as of Jan 2014):
 - Java SE 7 +.
 - Maven 3.0.5 +. 
 - Sample DB schemas got installed. NOTE: the simplest is javadb (no real DB is needed to be installed).
 
3.2. Command line maven test sample:
export JAVA_HOME='/path/to/Java/jdk1.7'
M2_HOME=/path/to/apache-maven-3.0.5
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

[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
[INFO]
[INFO] ETR for Java SE7 .................................. SUCCESS [0.466s]
[INFO] ETR Commons ....................................... SUCCESS [6.095s]
[INFO] ETR API ........................................... SUCCESS [1.041s]
[INFO] ETR mybatis Annotations Spring .................... SUCCESS [2.128s]
[INFO] ETR API Study ..................................... SUCCESS [6.060s]
[INFO] ETR mocked ........................................ SUCCESS [1.730s]
[INFO] ETR jdbc .......................................... SUCCESS [1.218s]
[INFO] ETR mybatis XML ................................... SUCCESS [1.171s]
[INFO] ETR mybatis Annotations ........................... SUCCESS [3.533s]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time: 23.719s
[INFO] Finished at: Sat Jan 25 03:32:29 EST 2014
[INFO] Final Memory: 48M/135M
[INFO] ------------------------------------------------------------------------
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

#-------------------------------

 