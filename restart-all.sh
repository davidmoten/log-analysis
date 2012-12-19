#!/bin/bash
./stop-all.sh 
set -e
mvn clean install
./start-all.sh
