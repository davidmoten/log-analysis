#!/bin/bash
set -e 
cd log-server 
mvn jetty:stop
cd ../log-ui
mvn jetty:stop
cd ..
