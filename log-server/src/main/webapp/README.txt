From orientdb distribution.zip took the web-site and copied to src/main/webapp. 

Added flot directory to js directory and added graph.html and data.txt and README.txt to webapp root.

Sample url:

http://sardevc.amsa.gov.au:2480/graph.html?sql=select%20logTimestamp%20as%20t,%20rateMsgPerSecond%20as%20value%20from%20Entry%20where%20rateMsgPerSecond%20is%20not%20null&limit=2000