# big-graph-data
Big Graph Data Analysis in Spark

Here we attempt to use GraphX to model the BikeShare data from https://www.capitalbikeshare.com/system-data
<br><br>

Setup instructions:

- Install or update to Java SE Development Kit 8 >> https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html
- Install Scala >> https://www.scala-lang.org/download/
- Install SBT >> https://www.scala-sbt.org/1.0/docs/Setup.html
- Install Apache Spark >> https://spark.apache.org/downloads.html
<br><br>

To execute program:
- Load the csv datasets from https://s3.amazonaws.com/capitalbikeshare-data/index.html into bikeshare/src/main/resources
- Update bikeshare/src/main/scala/team3/bikeshare/project/BigBikeGraph.scala with the filename of the chosen csv dataset e.g. src/main/resources/2016Q4-capitalbikeshare-tripdata.csv
- Run sbt at command prompt
```
$ cd bikeshare
$ sbt
...
> run
```
