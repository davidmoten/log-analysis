#!/bin/bash
cd `dirname $0`
mvn clean
scp -pr log-server/ log-persister/ log-database/ log-ui/ log-analysis-util/ pom.xml *.sh aussar@sardevc:/ausdev/log-analysis/
