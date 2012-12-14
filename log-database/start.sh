#!/bin/bash
cd `dirname $0`
mvn clean compile exec:java -Dpersist.dummy=true
