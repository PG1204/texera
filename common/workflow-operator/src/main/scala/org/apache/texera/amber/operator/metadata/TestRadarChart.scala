package org.apache.texera.amber.operator.metadata

import org.apache.texera.amber.operator.visualization.radarChart.RadarChartOpDesc

object TestRadarChart {
  def main(args: Array[String]): Unit = {
    println("All operator types:")
    OperatorMetadataGenerator.operatorTypeMap.foreach { case (clazz, name) =>
      println(s"$name -> ${clazz.getSimpleName}")
    }

    println("\nChecking if RadarChart is registered:")
    val radarChartClass = classOf[RadarChartOpDesc]
    val isRegistered = OperatorMetadataGenerator.operatorTypeMap.contains(radarChartClass)
    println(s"RadarChartOpDesc registered: $isRegistered")

    if (isRegistered) {
      val operatorType = OperatorMetadataGenerator.operatorTypeMap(radarChartClass)
      println(s"RadarChart operator type: $operatorType")

      // Try to generate metadata
      try {
        val metadata = OperatorMetadataGenerator.generateOperatorMetadata(radarChartClass)
        println(s"Metadata generated successfully: ${metadata.operatorType}")
      } catch {
        case e: Exception =>
          println(s"Error generating metadata: ${e.getMessage}")
          e.printStackTrace()
      }
    }
  }
}