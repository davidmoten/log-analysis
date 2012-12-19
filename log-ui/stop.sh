#!/bin/bash
cd `dirname $0`
mvn clean jetty:stop $* 
