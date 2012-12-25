#!/bin/bash
cd `dirname $0`
cd log-persister/ && mvn clean compile exec:java -Dlogan.config="$PERSISTER_CONFIG"

