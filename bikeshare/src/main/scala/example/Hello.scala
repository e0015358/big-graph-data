package pl.japila.spark

import org.apache.spark.{SparkContext, SparkConf}

object SparkMeApp {
  def main(args: Array[String]) {
    val sc = new SparkConf().setAppName("SparkMe Application").setMaster("local[2]").set("spark.executor.memory","1g")


    val fileName = args(0)
    println(fileName)
    val lines = sc.textFile(fileName).cache

    val c = lines.count
    println(s"There are $c lines in $fileName")
  }
}
