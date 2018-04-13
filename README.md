# big-graph-data
**Big Graph Data Analysis in Spark**

Here we attempt to use GraphX to model the BikeShare data from ~~https://www.capitalbikeshare.com/system-data~~ <https://www.fordgobike.com/system-data>
<br><br>

Setup instructions:

- Install or update to Java SE Development Kit 8 >> https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html
- Install Scala >> https://www.scala-lang.org/download/
- Install SBT >> https://www.scala-sbt.org/1.0/docs/Setup.html
- Install Apache Spark >> https://spark.apache.org/downloads.html

**Install Hadoop and Flume**

For the purpose of consuming large amount of data we decided to use Flume to write to HDFS

OS X setup instructions:
 - Install Hadoop >> <https://macmetric.com/how-to-install-hadoop-on-mac/>
 - Install Flume >> <https://brewinstall.org/install-flume-on-mac-with-brew/>

To start HDFS on OS X:
 - Enable Remote Login by navigating the following path :“System Preferences” -> “Sharing”. Check “Remote Login”
 - run "/usr/local/Cellar/hadoop/3.0.0/sbin/start-dfs.sh"


Sample Flume config for Bikeshare is found in https://github.com/e0015358/big-graph-data/blob/master/flume_config/bikeshare.conf

Place the sample config in /usr/local/Cellar/flume/1.6.0/libexec/conf/

Flume read CSV Instructions >> <https://acadgild.com/blog/loading-files-into-hdfs-using-flumes-spool-directory/>

To run flume:
>/usr/local/Cellar/flume/1.6.0/bin/flume-ng agent --name agent1 -f /usr/local/Cellar/flume/1.6.0/libexec/conf/bikeshare.conf
<br><br>

To list files in HDFS:
> hdfs dfs -ls /flume_sink | sort -k6,7

To view the HDFS:
> hdfs dfs -cat /flume_sink/FlumeData.*

To execute GraphX program:

~~Load the csv datasets from https://s3.amazonaws.com/capitalbikeshare-data/index.html into bikeshare/src/main/resources~~

~~Update bikeshare/src/main/scala/team3/bikeshare/project/BigBikeGraph.scala with the filename of the chosen csv dataset e.g. src/main/resources/2016Q4-capitalbikeshare-tripdata.csv~~
- Run sbt at command prompt
```
$ cd bikeshare
$ sbt
...
> run
```

GraphX Resources:
- <http://www.sparktutorials.net/Analyzing+Flight+Data%3A+A+Gentle+Introduction+to+GraphX+in+Spark>
- <https://github.com/caroljmcdonald/sparkgraphxexample>
- <https://docs.databricks.com/spark/latest/graph-analysis/graph-analysis-graphx-tutorial.html>
