package utilities

import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.feature.VectorAssembler
import play.api.libs.json.{JsValue, Json}
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{DoubleType, IntegerType, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object Dataset {
	val rand = scala.util.Random

	def getColumns(dataframe: DataFrame): JsValue = {
		Json.toJson(dataframe.schema.names.filter(x => !x.equals("radiant_win")))
	}
	def getSample(dataframe: DataFrame, percentage: Double):String = {
		dataframe.sample(percentage).toJSON.collectAsList().toString
	}
	def getCorrelationMatrix(dataframe: DataFrame):String = {
		val columnNames = dataframe
			.schema.names
			.filter(col => !col.equals("radiant_win") && !col.equals("localized_name"))

		val assembler = new VectorAssembler().setInputCols(columnNames).setOutputCol("features")
		val df = assembler.transform(dataframe)

		Correlation.corr(df, "features").toJSON.collectAsList().toString
	}
	def getPredictedModel(path: String): PipelineModel = {
		PipelineModel.load(path)
	}
	def getStats(kind: String, dataframe: DataFrame): JsValue = {
		val rows = dataframe.count.toString
		val columns = dataframe.schema.names.length.toString

		var map: Map[String, String] = Map[String, String]()

		map = map + ("Rows" -> rows) + ("Columns" -> columns) + ("Source" -> kind)

		Json.toJson(map)
	}
	def getRawStats(spark: SparkSession, path: String): JsValue = {
		val dataset = spark.read.csv(path)

		getStats("Kaggle (Raw)", dataset)
	}
	def getSchema(dataframe: DataFrame): JsValue = {
		var list: List[Map[String, String]] = List()

		dataframe.schema.fields.foreach(field => {
			var map: Map[String, String] = Map()

			val column = field.name
				.replace("_", " ")
				.split(" ")
				.map(_.capitalize)
				.mkString(" ")

			map = map + ("column" -> column) + ("type" -> field.dataType.toString) + ("unique" -> dataframe.select(field.name).distinct.count.toString)

			list = list :+ map
		})

		Json.toJson(list)
	}
	def getDoubleGroup(dataframe: DataFrame, col1: String, col2: String): String = {
		val res = dataframe.groupBy(col1, col2).count()

		res.toJSON.collectAsList.toString
	}

	def predict(spark: SparkSession, dataframe: DataFrame, s: Seq[Int]): String = {
		val RDD = spark.sparkContext.makeRDD(List(Row.fromSeq(s)))

		val columns = dataframe.schema.fields.filter(x => !x.name.equals("radiant_win"))
		val df = spark.createDataFrame(RDD, StructType(columns))

		val newDF = dataframe.schema
            .add("gold_spent_to_change", DoubleType)
    		.add("hero_damage_to_change", DoubleType)
			.add("probability", DoubleType)
			.add("prediction", DoubleType)
			.names.filter(col => !col.equals("radiant_win"))

		getPredictedModel(Constants.ROOT + Constants.CLASSIFIED_MODEL)
			.transform(df).select(newDF.map(col): _*).toJSON.collectAsList().toString
	}

	def cluster(spark: SparkSession, dataframe: DataFrame, s: Seq[Double]): String = {
		val RDD = spark.sparkContext.makeRDD(List(Row.fromSeq(s)))

		val columns = dataframe.schema
		val columnsWName = columns
            .add("kills_out", IntegerType)
			.add("prediction", IntegerType)

		val df = spark.createDataFrame(RDD, StructType(columns.fields))

		getPredictedModel(Constants.ROOT + Constants.CLUSTERED_MODEL)
			.transform(df).select(columnsWName.names.map(col): _*).toJSON.collectAsList().toString
	}
	def getClusterStats(dataframe: DataFrame): String = {
		val df = dataframe.groupBy("prediction").mean()

		df.toJSON.collectAsList().toString
	}
}
