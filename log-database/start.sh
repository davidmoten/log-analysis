#!/bin/bash
cd `dirname $0`
mvn clean install exec:exec $*
