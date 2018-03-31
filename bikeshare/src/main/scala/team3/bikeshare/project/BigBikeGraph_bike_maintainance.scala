package team3.bikeshare.project

import org.apache.spark.graphx._
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.log4j.Logger
import org.apache.log4j.Level


object BigBikeGraphBikeMaintainance {
  def main(args: Array[String]) {
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)
    val sparkSession = SparkSession.builder.master("local").appName("spark session example").getOrCreate()
    sparkSession.conf.set("spark.executor.memory", "2g")
    val df = sparkSession.read.option("header","true").csv("src/main/resources/2016Q1-capitalbikeshare-tripdata.csv").cache()
    // df.printSchema()
    // df.show()
    // println("="*70)
    val distinctValuesDF = df.select(df("Bike number")).distinct.cache()
    // println("Total number of unique bikes in 2016 Q1 : " + distinctValuesDF.count())
    println("Top 30 bikes out of " + distinctValuesDF.count() + " that require maintainance in 2016 Q1 : ")
    df.groupBy("Bike number").agg(count("Bike number")).withColumnRenamed("count(Bike number)", "Number of Trips").sort(desc("Number of Trips")).show(30)
    sparkSession.stop()
  }
}
