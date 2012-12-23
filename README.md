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
<img src="https://raw.github.com/davidmoten/log-analysis/master/docs/log-analysis.png"/>

Comprises the following components:

* log-database
* log-persister
* log-server
* log-ui

log-database
----------------
An instance of an orientdb server. Listens for binary connections on port 2424, REST api and html client on port 2480.

The main (currently only) table in the *logs* database is *Entry* with fields:

* logTimestamp - a mandatory long value being the epoch time in ms
* logId - UUID for the log line
* logKey - field key
* logValue - field value

One log line corresponds in the *Entry* table to one row for each key value pair extracted from the log message. Each row will have the same logTimestamp and logId which is a unique String key generated using UUID.randomUUID().

log-persister
----------------
An agent that parses logs and reports their content to *log-database* using binary connections.

log-server
----------------
A jetty web server running on port 9191 that makes binary connections to *log-database* and offers specially formatted json result sets for specific queries (on the http://host:port/data url).

log-ui
----------------
A jetty web server running on port 9292 that presents graphs using json sourced from *log-server*. Graphs produced using javascript, flot and jQuery.

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
Not much to do, the core is in and working. 

* handle split line java.util.logging style logs 



