package utilities

import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.feature.VectorAssembler
import play.api.libs.json.Json
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{DoubleType, IntegerType, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object Dataset {
	// BOTH DATASETS
	def getStages(path: String) = {
		val model = PipelineModel.load(Constants.MAIN_ROUTE + path)

		var flicker: Map[String, List[Map[String, String]]] = Map()

		model.stages.foreach(x => {
			var listOfMaps: List[Map[String, String]] = List()

			x.params
				.filter(param => x.get(param) != None)
				.foreach(param => {
					var mapOfStrings: Map[String, String] = Map()

					mapOfStrings = mapOfStrings + ("name" -> param.name) + ("value" -> x.get(param).get.toString)

					listOfMaps = listOfMaps :+ mapOfStrings
				})


			flicker = flicker + (x.uid -> listOfMaps)
		})

		Json.toJson(flicker)
	}
	def getColumns(dataframe: DataFrame) = {
		Json.toJson(dataframe.schema.names.filter(x => !x.equals("radiant_win")))
	}
	def getSample(dataframe: DataFrame, percentage: Double) = {
		dataframe.sample(percentage).toJSON.collectAsList().toString
	}
	def getCorrelationMatrix(dataframe: DataFrame) = {
		val columnNames = dataframe
			.schema.names
			.filter(col => !col.equals("radiant_win") && !col.equals("localized_name"))


		val assembler = new VectorAssembler().setInputCols(columnNames).setOutputCol("features")
		val df = assembler.transform(dataframe)

		Correlation.corr(df, "features").toJSON.collectAsList().toString
	}
	def getPredictedModel(path: String) = {
		PipelineModel.load(Constants.MAIN_ROUTE + path)
	}
	def getStats(dataframe: DataFrame) = {
		val rows = dataframe.count.toString
		val columns = dataframe.schema.names.length.toString

		var map: Map[String, String] = Map[String, String]()

		map = map + ("rows" -> rows) + ("columns" -> columns)

		Json.toJson(map)
	}
	def getRawStats(spark: SparkSession, path: String) = {
		val dataset = spark.read.csv(path)

		getStats(dataset)
	}

	// CLASSIFICATION
	def predict(spark: SparkSession, dataframe: DataFrame, s: Seq[Int]) = {
		val RDD = spark.sparkContext.makeRDD(List(Row.fromSeq(s)))

		val columns = dataframe.schema.fields.filter(x => !x.name.equals("radiant_win"))
		val df = spark.createDataFrame(RDD, StructType(columns))

		val newDF = dataframe.schema
			.add("probability", DoubleType)
			.add("prediction", DoubleType)
			.names.filter(col => !col.equals("radiant_win"))

		getPredictedModel(Constants.CLASSIFIED_MODEL).transform(df).select(newDF.map(col): _*).toJSON.collectAsList().toString
	}

	// CLUSTERING DATASET
	def cluster(spark: SparkSession, dataframe: DataFrame, s: Seq[Double]): String = {
		val RDD = spark.sparkContext.makeRDD(List(Row.fromSeq(s)))

		val columns = dataframe.schema
		val columnsWName = columns.add("prediction", IntegerType)

		val df = spark.createDataFrame(RDD, StructType(columns.fields))

		getPredictedModel(Constants.CLUSTERED_MODEL).transform(df).select(columnsWName.names.map(col): _*).toJSON.collectAsList().toString
	}
	def getClusterStats(dataframe: DataFrame) = {
		val df = dataframe.groupBy("prediction").mean()

		df.toJSON.collectAsList().toString
	}


}
