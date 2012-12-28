log-analysis
============

Analyzes log files for the purposes of time series analysis.

Uses [orientdb](https://github.com/nuvolabase/orientdb) for high speed persistence and querying and [flot](http://www.flotcharts.org/) for javascript graphs.

Non-aggregated:
<img src="https://raw.github.com/davidmoten/log-analysis/master/docs/screen1.png"/>
Aggregated:
<img src="https://raw.github.com/davidmoten/log-analysis/master/docs/screen2.png"/>

Features
==============
* pattern based log parsing
* fast parsing, persistence and query
* simplified datetime input (last day,last hour etc)
* parses single line and two line logging (e.g. java.util.Logging)
* tails log files, handles rollover, deletion (using [Apache commons-io Tailer](http://commons.apache.org/io/apidocs/org/apache/commons/io/input/Tailer.html))
* aggregated or non-aggregated graphs
* single field queries currently
* zoom in/out and pan
* multiple graphs to a page defined by url parameters
* click on data points shows logs around that time (+/-5min)
* numerous supported aggregation metrics including
  * MAX
  * MIN
  * MEAN
  * STANDARD_DEVIATION
  * COUNT
  * SUM
  * VARIANCE
  * SUM_SQUARES
  * FIRST
  * LAST
  * EARLIEST
  * LATEST

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
* logId - randomly generated UUID for the log line
* logProps - Map<String,ODocument>, each ODocument has one field of name "value". Keys for the map may include logLevel,logLogger,logMethod,threadName in addition to message specific keys 

log-persister
----------------
An agent that parses logs and reports their content to *log-database* using binary connections.

Sample configuration [here](https://raw.github.com/davidmoten/log-analysis/master/log-persister/src/test/resources/persister-configuration-test.xml).

log-server
----------------
A jetty web server running on port 9191 that makes binary connections to *log-database* and offers specially formatted json result sets for specific queries (on the http://host:port/data url).

log-ui
----------------
A jetty web server running on port 9292 that presents graphs using json sourced from *log-server*. Graphs produced using javascript, flot and jQuery.

Demo
=============
To test, run 

    git clone https://github.com/davidmoten/log-analysis.git
    cd log-analysis
    ./restart-all.sh 

which will
* stop log-server, log-ui, database
* mvn clean install
* start log-server
* start log-ui

All components will be started in the background.

Load some dummy data by clicking on [http://localhost:9191/load](http://localhost:9191/load).

To test go to [http://localhost:9292/](http://localhost:9292/).

To stop, run 

    ./stop-all.sh

Getting started
==================
On host that is to run the database server (and log-server and log-ui components as well):
    
    git clone https://github.com/davidmoten/log-analysis.git
    cd log-analysis
	./restart-all.sh

On a host (can be many) that has logs to be sent to the database:

Create a config file as per [here](https://raw.github.com/davidmoten/log-analysis/master/log-persister/src/test/resources/persister-configuration-test.xml) in say ~/.log-analysis/persister-config.xml.

    git clone https://github.com/davidmoten/log-analysis.git
    cd log-analysis
    mvn clean install
    export PERSISTER_CONFIG=~/.log-analysis/persister-config.xml
    ./start-persister.sh

Todo
===========
Not much left to do, all appears to be working.
* complete javadoc
* add more documentation to this site!
* improve unit test coverage 
* multiple field criteria support
* add source field to Entry table
* switch to embedded map with index
