package team3.bikeshare.project

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.log4j.Logger
import org.apache.log4j.Level

object BikeShareAppPageRank {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val sparkSession = SparkSession.builder.master("local").appName("Bike Share PageRank").getOrCreate()
    sparkSession.conf.set("spark.executor.memory", "5g")
    val newDf = sparkSession.read.option("header","true").csv("hdfs://localhost/fordgobike/")
    println("Processing "+ newDf.count + " datapoints")
    val start_stations = newDf.selectExpr("cast(start_station_id as int) start_station_id", "start_station_name", "start_station_latitude", "start_station_longitude").distinct
    val start_stations_rdd = start_stations.rdd
    val end_stations = newDf.selectExpr("cast(end_station_id as int) end_station_id", "end_station_name", "end_station_latitude", "end_station_longitude").distinct
    val end_stations_rdd = end_stations.rdd
    val all_stations_rdd = start_stations_rdd.union(end_stations_rdd).distinct
    // all_stations_rdd.take(10).foreach(println)
    // println(">> Total number of stations : " + all_stations_rdd.count())
    val trips = newDf.selectExpr("cast(start_station_id as int) start_station_id", "cast(end_station_id as int) end_station_id").distinct
    val trips_rdd = trips.rdd
    // // Create an RDD for the vertices
    val station_vertices: RDD[(VertexId, String)] = all_stations_rdd.map(row => (row(0).asInstanceOf[Number].longValue, row(1).asInstanceOf[String]+","+row(2).asInstanceOf[String]+","+row(3).asInstanceOf[String]))
    // // Create an RDD for edges
    val station_edges: RDD[Edge[Long]] = trips_rdd.map(row => Edge(row(0).asInstanceOf[Number].longValue, row(1).asInstanceOf[Number].longValue, 1))
    // // Define a default user in case there are relationship with missing user
    val default_station = ("Missing Station")
    // Build the initial Graph
    val station_graph = Graph(station_vertices, station_edges, default_station)
    station_graph.cache()
    val ranks = station_graph.pageRank(0.0001).vertices
    val pagerank = ranks.join(station_vertices).sortBy(_._2._1, ascending=false) // sort by the rank
    pagerank.take(10).foreach(x => println(x._2._2 + " has a pagerank of " + (x._2._1*100).toInt))
    val pagerank_file = "top_pagerank"
    pagerank.coalesce(1).map(tuple => "%s,%s".format(tuple._2._2, (tuple._2._1*100).toInt)).saveAsTextFile(pagerank_file)
    sparkSession.stop()
  }
}
