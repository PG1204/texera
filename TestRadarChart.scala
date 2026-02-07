// TestRadarChart.scala intentionally commented out to prevent build errors.
// import org.apache.texera.amber.operator.metadata.OperatorMetadataGenerator
// import org.apache.texera.amber.operator.visualization.radarChart.RadarChartOpDesc
//
// object TestRadarChart extends App {
//     OperatorMetadataGenerator.operatorTypeMap.foreach { case (clazz, name) =>
//       println(s"$name -> ${clazz.getSimpleName}")
//     }
//
//     val radarChartClass = classOf[RadarChartOpDesc]
//     val isRegistered = OperatorMetadataGenerator.operatorTypeMap.contains(radarChartClass)
//     println(s"RadarChartOpDesc registered: $isRegistered")
//     if (isRegistered) {
//       val operatorType = OperatorMetadataGenerator.operatorTypeMap(radarChartClass)
//       println(s"RadarChartOpDesc type: $operatorType")
//       val metadata = OperatorMetadataGenerator.generateOperatorMetadata(radarChartClass)
//       println(s"RadarChartOpDesc metadata: $metadata")
//     }
// }