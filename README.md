log-analysis
============

Analyzes log files for the purposes of time series analysis.

Uses (http://www.orientdb.org orientdb) for high speed persistence and querying and (http://www.flotcharts.org/ flot) for javascript graphs.

Comprises the following components:

* log-database
* log-persister
* log-server
* log-ui

To test, run 
    ./restart-all.sh 
which will
* stop log-server, log-ui
* mvn clean install
* start log-database with some dummy data
* start log-server
* start log-ui

To test go to (http://localhost:9292/graph.html).

To stop, ctrl-c the process started above and run 
    ./stop-all.sh
