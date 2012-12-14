#!/bin/bash 
rm log-database/target/db-started
set -e
(log-database/start.sh ) &
while [ ! -f log-database/target/db-started ]
do
  sleep 2
done
echo database started
(log-server/start.sh) &
(log-ui/start.sh) &
echo started all
