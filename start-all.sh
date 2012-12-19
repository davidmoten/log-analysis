#!/bin/bash 
(log-server/start.sh) &
(log-ui/start.sh) &
(log-database/start.sh) & 
echo started all
