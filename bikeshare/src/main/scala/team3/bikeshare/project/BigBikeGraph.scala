package team3.bikeshare.project

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.log4j.Logger
import org.apache.log4j.Level


object BikeShareApp {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val sparkSession = SparkSession.builder.master("local").appName("Bike Share Big Graph").getOrCreate()
    sparkSession.conf.set("spark.executor.memory", "2g")
    // val df = sparkSession.read.option("header","true").csv("src/main/resources/2014-q1_trip_history_data.csv.COMPLETED")
    val df = sparkSession.read.option("header","true").csv("hdfs://localhost/flume_sink/")
    var newDf = df
    for(col <- df.columns){
      newDf = newDf.withColumnRenamed(col,col.replaceAll("\\s", "_"))
    }
    newDf.printSchema()
    newDf.show()
    val start_stations = newDf.selectExpr("cast(Start_station_number as int) Start_station_number", "Start_station").distinct
    start_stations.show()
    val start_stations_rdd = start_stations.rdd
    val end_stations = newDf.selectExpr("cast(End_station_number as int) End_station_number", "End_station").distinct
    val end_stations_rdd = end_stations.rdd
    val all_stations_rdd = start_stations_rdd.union(end_stations_rdd).distinct
    all_stations_rdd.take(10).foreach(println)
    println(">> Total number of stations : " + all_stations_rdd.count())
    val trips = newDf.selectExpr("cast(Start_station_number as int) Start_station_number", "cast(End_station_number as int) End_station_number").distinct
    val trips_rdd = trips.rdd
    // // Create an RDD for the vertices
    val station_vertices: RDD[(VertexId, String)] = all_stations_rdd.map(row => (row(0).asInstanceOf[Number].longValue, row(1).asInstanceOf[String]))
    // // Create an RDD for edges
    val station_edges: RDD[Edge[Long]] = trips_rdd.map(row => Edge(row(0).asInstanceOf[Number].longValue, row(1).asInstanceOf[Number].longValue, 1))
    // // Define a default user in case there are relationship with missing user
    val default_station = ("Missing Station")
    // Build the initial Graph
    val station_graph = Graph(station_vertices, station_edges, default_station)
    station_graph.cache()
    println("Total Number of Stations: " + station_graph.numVertices)
    println("Total Number of Trips: " + station_graph.numEdges)
    // sanity check
    println("Total Number of Trips in Original Data: " + trips_rdd.count)
    sparkSession.stop()
  }

  // Define a reduce operation to compute the highest degree vertex
  def max(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
    if (a._2 > b._2) a else b
  }

}
