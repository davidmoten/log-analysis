#!/bin/bash
cd `dirname $0`
mvn clean
ssh aussar@sardevc.amsa.gov.au "rm -rf /ausdev/log-analysis/*"
scp -pr log-server/ log-persister/ log-database/ log-ui/ log-analysis-core/ pom.xml *.sh aussar@sardevc:/ausdev/log-analysis/
