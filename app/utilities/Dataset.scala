package utilities

import org.apache.spark.ml.{PipelineModel, Transformer}
import org.apache.spark.ml.feature.VectorAssembler
import play.api.libs.json.{JsValue, Json}
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

import scala.collection.immutable.ListMap

object Dataset {
	val rand = scala.util.Random
	// BOTH DATASETS
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
		PipelineModel.load(path)
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
	def getSchema(dataframe: DataFrame): JsValue = {
		var list: List[Map[String, String]] = List()

		val filteredByDouble = dataframe.schema.fields.filter(field => field.dataType.equals(DoubleType))
		val filteredByString = dataframe.schema.fields.filter(field => field.dataType.equals(StringType))

		if(filteredByDouble.size == 0) return Json.toJson("Data set is made of Double Type values!")
		if(filteredByString.size == 0) return Json.toJson("Data set is made of String Type values!")

		dataframe.schema.fields.foreach(field => {
			var map: Map[String, String] = Map()
			map = map + ("column" -> field.name) + ("type" -> field.dataType.toString)

			list = list :+ map
		})

		Json.toJson(list)
	}
	def getDoubleGroup(dataframe: DataFrame, col1: String, col2: String) = {
		val res = dataframe.groupBy(col1, col2).count()

		res.toJSON.collectAsList.toString
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

		getPredictedModel(Constants.ROOT + Constants.CLASSIFIED_MODEL)
			.transform(df).select(newDF.map(col): _*).toJSON.collectAsList().toString
	}

	// CLUSTERING DATASET
	def cluster(spark: SparkSession, dataframe: DataFrame, s: Seq[Double]): String = {
		val RDD = spark.sparkContext.makeRDD(List(Row.fromSeq(s)))

		val columns = dataframe.schema
		val columnsWName = columns.add("prediction", IntegerType)

		val df = spark.createDataFrame(RDD, StructType(columns.fields))

		getPredictedModel(Constants.ROOT + Constants.CLUSTERED_MODEL)
			.transform(df).select(columnsWName.names.map(col): _*).toJSON.collectAsList().toString
	}
	def getClusterStats(dataframe: DataFrame) = {
		val df = dataframe.groupBy("prediction").mean()

		df.toJSON.collectAsList().toString
	}
}
