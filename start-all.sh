#!/bin/bash 
(log-server/start.sh) &
(log-ui/start.sh) &
set -e
log-database/start.sh 
echo started all
