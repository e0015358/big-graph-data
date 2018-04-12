package team3.bikeshare.project
import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.log4j.Logger
import org.apache.log4j.Level
import org.apache.spark.SparkContext._
import java.io._


object BigBikeGraphDegreesRatio {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val sparkSession = SparkSession.builder.master("local").appName("Bike Share Vertex Degrees").getOrCreate()
    sparkSession.conf.set("spark.executor.memory", "2g")
    val sc = sparkSession.sparkContext
    val newDf = sparkSession.read.option("header","true").csv("src/main/resources/201801_fordgobike_tripdata.csv")
    // val newDf = df.sample(false, 0.05)
    println("Processing "+ newDf.count + " datapoints")
    // newDf.printSchema()
    // newDf.show()
    // println("="*70)
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
    println("Total Number of Stations: " + station_graph.numVertices)
    println("Total Number of Trips: " + station_graph.numEdges)
    // sanity check
    println("Total Number of Trips in Original Data: " + trips_rdd.count)
    println("="*70)

    // val inDegrees: VertexRDD[Int] = station_graph.inDegrees
    // // println("In Degrees : " + inDegrees)
    // // println("="*70)
    // val maxInDegree: (VertexId, Int) = station_graph.inDegrees.reduce(max)
    // println("Max In Degrees : " + maxInDegree)
    // println("="*70)
    //
    // station_graph
    //     .groupEdges((edge1, edge2) => edge1 + edge2)
    //     .triplets
    //     .sortBy(_.attr, ascending=false)
    //     .map(triplet =>
    //       "There were " + triplet.attr.toString + " trips from " + triplet.srcAttr + " to " + triplet.dstAttr + ".")
    //     .take(10)
    //     .foreach(println)
    // println("="*70)

    // Here we discover which stations have the highest ratio of in degrees but least out degrees
    val top_sinks = station_graph
        .inDegrees
        .join(station_graph.outDegrees) // join with out Degrees
        .join(station_vertices) // join with our other stations
        .map(x => (x._2._1._1.toDouble/x._2._1._2.toDouble, x._2._2)) // ratio of in to out
        .sortBy(_._1, ascending=false) // sort by high to low
    top_sinks.take(10).foreach(x => println(x._2 + " has a in/out degree ratio of " + x._1))
    val top_sinks_file = "top_sinks"
    top_sinks.coalesce(1).map(tuple => "%s,%s".format(tuple._2, (tuple._1*tuple._1*tuple._1).toInt)).saveAsTextFile(top_sinks_file)
    println("="*70)

    // Here we discover which stations have the highest ratio of out degrees vs in degrees
    val top_sources = station_graph
        .inDegrees
        .join(station_graph.outDegrees) // join with out Degrees
        .join(station_vertices) // join with our other stations
        .map(x => (x._2._1._2.toDouble/x._2._1._1.toDouble, x._2._2)) // ratio of out to in
        .sortBy(_._1, ascending=false) // sort by high to low
    top_sources.take(10).foreach(x => println(x._2 + " has a out/in degree ratio of " + x._1))
    val top_sources_file = "top_sources"
    top_sources.coalesce(1).map(tuple => "%s,%s".format(tuple._2, (tuple._1*tuple._1*tuple._1).toInt)).saveAsTextFile(top_sources_file)
    sparkSession.stop()
  }

  // Define a reduce operation to compute the highest degree vertex
  def max(a: (VertexId, Int), b: (VertexId, Int)): (VertexId, Int) = {
    if (a._2 > b._2) a else b
  }

}
