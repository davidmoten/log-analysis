#!/bin/bash
cd `dirname $0`
mvn exec:exec -Dcommand=shutdown
