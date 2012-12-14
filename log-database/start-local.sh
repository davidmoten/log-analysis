#!/bin/bash
cd `dirname $0`
(cd src/main/webapp && python -m SimpleHTTPServer)
