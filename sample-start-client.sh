#!/usr/bin/bash
#Note the wildcard on the cts.log which will pick up the rolled over log4j logs
cd `dirname $0`
cd log-persister/ && mvn clean compile exec:java -Dexec.mainClass=org.moten.david.log.ClientMain -DlogPaths="/ausdev/container/logs/cts/cts.log.*,/ausdev/container/logs/cts/cts-adapter-flight-info-service.log" -Durl="remote:jenkins.amsa.gov.au/logs"

