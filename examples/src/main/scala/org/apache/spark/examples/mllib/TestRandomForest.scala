package org.apache.spark.examples.mllib

import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.tree.DecisionTree
import org.apache.spark.mllib.util.MLUtils
import org.apache.spark.mllib.tree.RandomForest


 object TestRandomForest {
    def main(args: Array[String]) {
      // Load and parse the data file.
      val conf = new SparkConf().setAppName("Test Random Forest").set("spark.executor.memory", "4g")
      val spark = new SparkContext(conf)

      val data = MLUtils.loadLibSVMFile(spark, "data/mllib/spam.txt")

      // Split the data into training and test sets (30% held out for testing)
      val splits = data.randomSplit(Array(0.7, 0.3))
      val (trainingData, testData) = (splits(0), splits(1))

      val numClasses = 2
      val categoricalFeaturesInfo = Map[Int, Int]()
      val numTrees = 100 // Use more in practice.
      val featureSubsetStrategy = "auto" // Let the algorithm choose.
      val impurity = "entropy"
      val maxDepth = 30
      val maxBins = 32

      val model = RandomForest.trainClassifier(trainingData, numClasses, categoricalFeaturesInfo,
        numTrees, featureSubsetStrategy, impurity, maxDepth, maxBins)

      // Evaluate model on test instances and compute test error
      val labelAndPreds = testData.map { point =>
        val prediction = model.predict(point.features)
        (point.label, prediction)
      }
      val testErr = labelAndPreds.filter(r => r._1 != r._2).count.toDouble / testData.count()
      println("Test Error = " + testErr)
      //println("Learned classification forest model:\n" + model.toDebugString)
      var num:Double = 0
      for(i <- 0 until numTrees){
        num += model.trees(i).depth
      }
      num /= numTrees
      println("Average Depth: " + num.toString())
      println("NUM NODES: " + model.totalNumNodes.toString())

    }
  }
