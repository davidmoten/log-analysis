#!/usr/bin/bash
#Note the wildcard on the cts.log which will pick up the rolled over log4j logs
cd /ausdev/log-analysis/log-persister/ && mvn clean compile exec:java -Dexec.mainClass=org.moten.david.log.ClientMain -DlogPaths="/ausdev/container/logs/cts/cts.log.*,/ausdev/container/logs/cts/cts-adapter-flight-info-service.log"

