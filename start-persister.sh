#!/bin/bash
#Note the wildcard on the cts.log which will pick up the rolled over log4j logs
cd `dirname $0`
cd log-persister/ && mvn clean compile exec:java -Dexec.mainClass=org.moten.david.log.ClientMain 

