log-analysis
============

Analyzes log files for the purposes of time series analysis.

Uses [orientdb](https://github.com/nuvolabase/orientdb) for high speed persistence and querying and [flot](http://www.flotcharts.org/) for javascript graphs.

Non-aggregated:
<img src="https://raw.github.com/davidmoten/log-analysis/master/docs/screen1.png"/>
Aggregated:
<img src="https://raw.github.com/davidmoten/log-analysis/master/docs/screen2.png"/>


Architecture
==============
Comprises the following components:

* log-database
* log-persister
* log-server
* log-ui

log-database
----------------
An instance of an orientdb server. Listens for binary connections on port 2424, rest api and html client on port 2480.

log-persister
----------------
An agent that parses logs and reports their content to ''log-database''.

log-server
----------------
A jetty web server running on port 9191 that makes binary connections to ''log-database'' and offers specially formatted json result sets for specific queries (on the http://host:port/data url).

log-ui
----------------
A jetty web server running on port 9292 that presents graphs using json sourced from ''log-server''.

Demo
=============
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
* dashboard
* auto scan for new log files
