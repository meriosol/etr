ETR JAXB Generator
[2014-04-07]

About opening project in IDE (e.g. in IDEA):
Build project using command line maven once before open project pom in IDE.
Reason is JAXB generated src folders will be added only once generated(chicken-egg problem).


Sample env setup:
#----------------
# Env(init each time new session opens):
# -======
# it can be cygwin(then better gitbatch):
export JAVA_HOME='path_to/Java/jdk1.7'
M2_HOME=/path_to/apache-maven-3.1.1
M2=$M2_HOME/bin
MAVEN_OPTS='-Xmx1024m -XX:MaxPermSize=256m'
PATH=$PATH:$M2

PROJECT_DIR=/path_to/etr-jaxb-gen
# -======
#----------------
# Sample commands:
cd $PROJECT_DIR
mvn clean test
mvn clean package
#----------------

Project History:

0.1-RELEASE - 2014-04-07
 - JAXB code is generated and 1st test passed.




