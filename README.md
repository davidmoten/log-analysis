log-analysis
============

Analyzes log files for the purposes of time series analysis.

Uses orientdb for high speed persistence and querying and flot for javascript graphs.

Comprises the following components:

* log-database
* log-persister
* log-server
* log-ui

To test, run ./restart-all.sh which will
* start log-database with some dummy data
* start log-server
* start log-ui
To test go to localhost:9292/graph2.html
