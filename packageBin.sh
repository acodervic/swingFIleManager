#!/bin/bash
export JAVA_HOME=$JAVA17_HOME
mvn package
cp  target/filemanager-1.0-SNAPSHOT.jar /home/w/mytool/filemanager/

#cd /home/w/mytool/filemanager/ &&$JAVA17_HOME/bin/native-image --report-unsupported-elements-at-runtime -jar  filemanager-1.0-SNAPSHOT.jar