#!/bin/bash 
echo starting all
(log-server/start.sh) &
(log-ui/start.sh) &
(log-database/start.sh) & 

