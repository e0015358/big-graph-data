package team4.bikeshare.project

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf

object BikeShareApp {
  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName("BikeShare App").setMaster("local[2]").set("spark.executor.memory","1g");
    val sc = new SparkContext(conf)
    // Create an RDD for the vertices
    val users: RDD[(VertexId, (String, String))] =
      sc.parallelize(Array((3L, ("rxin", "student")), (7L, ("jgonzal", "postdoc")),
                           (5L, ("franklin", "prof")), (2L, ("istoica", "prof"))))
    // Create an RDD for edges
    val relationships: RDD[Edge[String]] =
      sc.parallelize(Array(Edge(3L, 7L, "collab"),    Edge(5L, 3L, "advisor"),
                           Edge(2L, 5L, "colleague"), Edge(5L, 7L, "pi")))
    // Define a default user in case there are relationship with missing user
    val defaultUser = ("John Doe", "Missing")
    // Build the initial Graph
    val graph = Graph(users, relationships, defaultUser)
    println("Total Number of Postdocs: " + graph.vertices.filter { case (id, (name, pos)) => pos == "postdoc" }.count)
    println("Total Number of Stations: " + graph.numVertices)
    sc.stop()
  }
}
