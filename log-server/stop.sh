#!/bin/bash
set -e
cd `dirname $0`
mvn compile jetty:stop $*
