package team3.bikeshare.project

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.log4j.Logger
import org.apache.log4j.Level

object BikeShareAppHDFS {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val sparkSession = SparkSession.builder.master("local").appName("Bike Share Big Graph").getOrCreate()
    sparkSession.conf.set("spark.executor.memory", "3g")
    val newDf = sparkSession.read.option("header","true").csv("hdfs://localhost/fordgobike/")
    // var newDf = df.sample(false, 0.1)
    newDf.printSchema()
    newDf.show()
    val start_stations = newDf.selectExpr("cast(start_station_id as int) start_station_id", "start_station_name").distinct
    start_stations.show()
    val start_stations_rdd = start_stations.rdd
    val end_stations = newDf.selectExpr("cast(end_station_id as int) end_station_id", "end_station_name").distinct
    val end_stations_rdd = end_stations.rdd
    val all_stations_rdd = start_stations_rdd.union(end_stations_rdd).distinct
    // all_stations_rdd.take(10).foreach(println)
    println(">> Total number of stations : " + all_stations_rdd.count())
    val trips = newDf.selectExpr("cast(start_station_id as int) start_station_id", "cast(end_station_id as int) end_station_id").distinct
    val trips_rdd = trips.rdd
    // Create an RDD for the vertices
    val station_vertices: RDD[(VertexId, String)] = all_stations_rdd.map(row => (row(0).asInstanceOf[Number].longValue, row(1).asInstanceOf[String]))
    // Create an RDD for edges
    val station_edges: RDD[Edge[Long]] = trips_rdd.map(row => Edge(row(0).asInstanceOf[Number].longValue, row(1).asInstanceOf[Number].longValue, 1))
    // Define a default user in case there are relationship with missing user
    val default_station = ("Missing Station")
    // Build the Graph
    val station_graph = Graph(station_vertices, station_edges, default_station)
    println("Total Number of Stations: " + station_graph.numVertices)
    println("Total Number of Trips: " + station_graph.numEdges)
    // Sanity check
    println("Total Number of Trips in Original Data: " + trips_rdd.count)
    sparkSession.stop()
  }
}
