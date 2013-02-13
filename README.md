log-analysis
============
Analyzes log files for the purposes of time series analysis.

Uses [orientdb](https://github.com/nuvolabase/orientdb) for high speed persistence and querying and [flot](http://www.flotcharts.org/) for javascript graphs.

Alternative implementation using [H2  database](http://www.h2database.com/html/main.html) in development.

Non-aggregated:
<img src="https://raw.github.com/davidmoten/log-analysis/master/docs/screen1.png"/>
Aggregated:
<img src="https://raw.github.com/davidmoten/log-analysis/master/docs/screen2.png"/>

Continuous integration with Jenkins for this project is [here](https://xuml-tools.ci.cloudbees.com/). <a href="https://xuml-tools.ci.cloudbees.com/"><img  src="http://web-static-cloudfront.s3.amazonaws.com/images/badges/BuiltOnDEV.png"/></a>

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

    cd <YOUR_WORKSPACE>
    git clone https://github.com/davidmoten/log-analysis.git
    cd log-analysis
    ./restart-all.sh 

which will
* stop log-server, log-ui, database
* mvn clean install
* start log-server
* start log-ui

All components will be started in the background.

Go to [http://localhost:9292/](http://localhost:9292/) and 
* load some dummy data
* click on sample graphs

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
    
File matching
==================
The paths of log files to be persisted is specified in the &lt;log&gt; element of the persister configuration file as below:

    <log source="test">
        <path>PATH/FILENAME_REGEX</path>
	</log>

* PATH is the ANT style wildcard path of the directory. 
* FILENAME_REGEX is a java regular expression for matching the filename part of the log file.

An example using directory wildcards and a regular expression:

    <log source="test">
        <path>/var/log/myapps/**/log/my.*\.log</path>
	</log>

This breaks down into a PATH of <code>/var/log/myapps/**</code> which means <code>/var/log/myapps</code> and all its subdirectories. Note that for a PATH, * denotes any directory and ** denotes any nested sequence of directories including the current.

The FILENAME_REGEX is

    my.*\.log

which matches all filenames starting with *my* and ending in *.log*.

Pattern matching
===================
The *log-persister* configuration file ([here](https://raw.github.com/davidmoten/log-analysis/master/log-persister/src/test/resources/persister-configuration-test.xml)) refers to two patterns. Here's a fragment concerning patterns:

    <pattern>^(\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d\d\d) +(\S+) +(\S+)+(\S+)? ?- (.*)$</pattern>
    <patternGroups>logTimestamp,logLevel,logLogger,threadName,logMsg</patternGroups>
    <messagePattern>(\b[a-zA-Z](?:\w| )*)=([^;|,]*)(;|\||,|$)</messagePattern>
    <timestampFormat>yyyy-MM-dd HH:mm:ss.SSS</timestampFormat>

* line pattern (and its pattern groups)
* message pone)attern

Line pattern
----------------
    <pattern>^(\d\d\d\d-\d\d-\d\d \d\d:\d\d:\d\d\.\d\d\d) +(\S+) +(\S+)+(\S+)? ?- (.*)$</pattern>
    <patternGroups>logTimestamp,logLevel,logLogger,threadName,logMsg</patternGroups>
    
The above pattern is a java regular expression for parsing a typical log4j log line of the form below:

    2012-11-29 04:39:19.846 INFO  au.gov.amsa.er.craft.tracking.CraftpicProviderDirect - number of craft = 7379
    
Looking at the patternGroups the first matching group will be identified as the *logTimestamp*, second matching group as the *logLevel* (INFO in this case), and so on:
* *logTimestamp* = 2012-11-29 04:39:19.846 (actually the epoch ms value)
* *logLevel* = INFO
* *logLogger* = au.gov.amsa.er.craft.tracking.CraftpicProviderDirect
* *threadName* = null (not present)
* *logMsg* = 'number of craft = 7379'

The key-value parts of the logMsg are parsed by the *message pattern* described below.

Multiline
---------------
The default java.util.logging pattern is a multiline (two line) pattern. Here's an example of a two line log:

    23/12/2012 6:58:04 AM org.moten.david.log.core.Database persistDummyRecords
    INFO: persisted random values=1000 from the last hour to table Dummy

Two line patterns are configured as below:

    <pattern>^(\d\d/\d\d/\d\d\d\d \d\d?:\d\d:\d\d (?:(?:AM)|(?:PM))) +(\S+) +(\S+)ZZZ(\S+): (.*)$</pattern>
    <patternGroups>logTimestamp,logLogger,logMethod,logLevel,logMsg</patternGroups>
    <timestampFormat>dd/MM/yyyy hh:mm:ss a</timestampFormat>
    <multiline>true</multiline>

Take special note of the *ZZZ* which delimits line 1 from line 2 in the pattern. The two lines are concatenated using ZZZ between them and then matched against the pattern. In the same way the pattern groups refer to the groups found in the concatenation of the two lines.

Message pattern
----------------
    <messagePattern>(\b[a-zA-Z](?:\w| )*)=([^;|,]*)(;|\||,|$)</messagePattern>

Above is the default message pattern used by log-persister. A matching key-value pair in a log message satisfies these properties:
* key must start with a letter and be preceded by a word boundary
* key can contain whitespace and any legal java identifier character
* key is separated from value by =
* value part is delimited at its termination by semicolon (;), comma (,), vertical bar(|) or end of line

Timestamp format
------------------
    <timestampFormat>dd/MM/yyyy hh:mm:ss a</timestampFormat>

The format is as defined for the [SimpleDateFormat](http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html) class. 

Todo
===========
Not much left to do, all appears to be working.
* complete javadoc
* add more documentation to this site!
* improve unit test coverage 
* multiple field criteria support
* add source field to Entry table (*done*)
* switch to embedded map with index (*done*)
* allow arbitrary pattern groups
* add scripts for running on Windows
