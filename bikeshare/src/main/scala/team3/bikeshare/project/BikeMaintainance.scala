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
    sparkSession.conf.set("spark.executor.memory", "3g")
    val df = sparkSession.read.option("header","true").csv("hdfs://localhost/fordgobike/")
    val distinctValuesDF = df.select(df("bike_id")).distinct.cache()
    println("Total number of unique bikes in 2018 Jan : " + distinctValuesDF.count())
    println("Top 30 bikes out of " + distinctValuesDF.count() + " that require maintainance in 2018 January : ")
    val bike_maintainance = df.groupBy("bike_id").agg(count("bike_id")).withColumnRenamed("count(bike_id)", "Number of Trips").sort(desc("Number of Trips"))
    bike_maintainance.show(30)
    sparkSession.stop()
  }
}
