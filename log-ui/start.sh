#!/bin/bash
cd `dirname $0`
mvn clean jetty:run -Djetty.port=9292
