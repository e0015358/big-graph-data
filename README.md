# big-graph-data
**Big Graph Data Analysis in Spark**

Here we attempt to use GraphX to model the BikeShare data from https://www.capitalbikeshare.com/system-data
<br><br>

Setup instructions:

- Install or update to Java SE Development Kit 8 >> https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html
- Install Scala >> https://www.scala-lang.org/download/
- Install SBT >> https://www.scala-sbt.org/1.0/docs/Setup.html
- Install Apache Spark >> https://spark.apache.org/downloads.html

**Install Hadoop and Flume**

For the purpose of consuming large amount of data we decided to use Flume to write to HDFS

OS X setup instructions:
 - Install Hadoop >> <https://isaacchanghau.github.io/2017/06/27/Hadoop-Installation-on-Mac-OS-X/> or <https://macmetric.com/how-to-install-hadoop-on-mac/>
 - Install Flume >> <https://brewinstall.org/install-flume-on-mac-with-brew/>

Sample Flume config for Bikeshare is found in >> flume_config/bikeshare.conf

Flume read CSV Instructions >> <https://acadgild.com/blog/loading-files-into-hdfs-using-flumes-spool-directory/>

To run flume: >> bin/flume-ng agent --name agent1 -f conf/bikeshare.conf
<br><br>

To view the HDFS: >> hdfs dfs -cat /flume_sink/FlumeData.*

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
