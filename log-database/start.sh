#!/bin/bash
cd `dirname $0`
export CONFIG_FILE="`pwd`/src/main/resources/orientdb-server-config.xml"
mvn clean install exec:exec $*
