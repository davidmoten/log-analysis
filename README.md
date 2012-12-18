log-analysis
============

Analyzes log files for the purposes of time series analysis.

Uses [orientdb](https://github.com/nuvolabase/orientdb) for high speed persistence and querying and [flot](http://www.flotcharts.org/) for javascript graphs.

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

To test go to [http://localhost:9292/](http://localhost:9292/).

To stop, ctrl-c the process started above and run 
    ./stop-all.sh

Todo
===========
A fair bit but the core is in and working. 
* detailed configuration including regex patterns for extraction of info from log lines
* non-aggregated queries
* dashboard
* auto scan for new log files
