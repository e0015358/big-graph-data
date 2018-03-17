package team4.bikeshare.project

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession


object BikeShareApp {
  def main(args: Array[String]) {
    val sparkSession = SparkSession.builder.master("local").appName("spark session example").getOrCreate()
    val df = sparkSession.read.option("header","true").csv("src/main/resources/20180102-capitalbikeshare-tripdata.csv")
    // df.printSchema()
    // df.show()
    // val start_stations = df.select("start_station_number", "Start station").distinct
    val start_stations = df.selectExpr("cast(start_station_number as int) start_station_number", "start_station").distinct
    val start_stations_rdd = start_stations.rdd
    val end_stations = df.selectExpr("cast(end_station_number as int) end_station_number", "end_station").distinct
    val end_stations_rdd = end_stations.rdd
    val all_stations_rdd = start_stations_rdd.union(end_stations_rdd).distinct
    // all_stations_rdd.take(10).foreach(println)
    // println(">> Total number of stations : " + all_stations_rdd.count())
    val trips = df.selectExpr("cast(start_station_number as int) start_station_number", "cast(end_station_number as int) end_station_number").distinct
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
    // println("Total Number of Postdocs: " + graph.vertices.filter { case (id, (name, pos)) => pos == "postdoc" }.count)
    // println("Total Number of Stations: " + graph.numVertices)
    sparkSession.stop()
  }
}
