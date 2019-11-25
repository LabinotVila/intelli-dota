package utilities

import org.apache.spark.ml.{PipelineModel, Transformer}
import play.api.libs.json.Json
import utilities.Dataset.rand

import scala.collection.immutable.ListMap

object Stages {
	def getStages(path: String) = {
		val model = PipelineModel.load(path)

		var mainMap: ListMap[String, List[Map[String, String]]] = ListMap()

		model.stages.foreach(stage => {
			var stageName = stage.getClass.toString.split("\\.").last.replace("Model", "")

			if (mainMap.contains(stageName)) stageName = stageName + rand.nextInt(9)

			mainMap = mainMap + (stageName -> stageDetailsOf(stage))
		})

		Json.toJson(ListMap(mainMap.toSeq.sortBy(_._1): _*))
	}

	def stageDetailsOf(x: Transformer): List[Map[String, String]] = {
		var listOfMaps: List[Map[String, String]] = List()

		x.params
			.filter(param => x.get(param) != None)
			.foreach(param => {
				var nameValuePair: Map[String, String] = Map()

				val paramClass = param.getClass.toString.split("\\.").last.capitalize
				val paramName = param.name.capitalize

				val name = paramClass + " [" + paramName + "]"
				var value = x.get(param).get.toString.replaceAll("[\\-_]", " ").capitalize

				if (name.contains("ArrayParam"))
					value = "Array of " + name.split("ArrayParam")(0).toLowerCase + "s"

				nameValuePair = nameValuePair + ("name" -> name) + ("value" -> value)

				listOfMaps = listOfMaps :+ nameValuePair
			})

		listOfMaps

	}
}
