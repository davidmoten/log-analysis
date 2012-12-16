#!/bin/bash
set -e
./stop-all.sh 
mvn clean install
./start-all.sh
