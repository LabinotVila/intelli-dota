package utilities

import org.apache.spark.ml.{Pipeline, PipelineModel}
import org.apache.spark.ml.feature.{MinMaxScaler, StandardScaler, VectorAssembler}
import org.apache.spark.ml.linalg.Vectors
import play.api.libs.json.{JsValue, Json}
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{DoubleType, IntegerType, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions._

import org.apache.spark.sql.functions.col
import org.apache.spark.sql.functions.explode


object Dataset {
	val rand = scala.util.Random

	def getSteamColumns(dataframe: DataFrame): JsValue = {
		val firstRow = dataframe.rdd.first

		Json.toJson(firstRow.getValuesMap[Int](firstRow.schema.fieldNames.filter(a => !a.equals("radiant_win"))))
	}
	def getKaggleColumns(dataframe: DataFrame): JsValue = {
		val firstRow = dataframe.rdd.first

		Json.toJson(firstRow.getValuesMap[Double](firstRow.schema.fieldNames))
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

		getStats("Kaggle [R]", dataset)
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
			.transform(df).select(newDF.map(col): _*)
			.withColumn("result",
				when(col("prediction").equalTo(1), Constants.WON_STRING)
					.otherwise(Constants.LOST_STRING))
			.toJSON.collectAsList().toString
	}

	def cluster(spark: SparkSession, dataframe: DataFrame, s: Seq[Double]): String = {
		val RDD = spark.sparkContext.makeRDD(List(Row.fromSeq(s)))

		val columns = dataframe.schema
		val columnsWName = columns
            .add("kills_out", IntegerType)
			.add("prediction", IntegerType)

		val df = spark.createDataFrame(RDD, StructType(columns.fields))

		getPredictedModel(Constants.ROOT + Constants.CLUSTERED_MODEL)
			.transform(df).select(columnsWName.names.map(col): _*)
			.toJSON.collectAsList().toString
	}
	def getClusterStats(dataframe: DataFrame) = {
		var df = dataframe.groupBy("prediction").mean().drop("prediction")
			.orderBy("prediction")

		val columns = Array("gold", "gold_per_min", "xp_per_min", "kills", "deaths", "assists", "denies",
			"last_hits", "hero_damage", "hero_healing", "tower_damage", "level")
		val assembler = new VectorAssembler()
			.setInputCols(columns)
			.setOutputCol("pre-features")
		val scaler = new StandardScaler()
			.setInputCol("pre-features")
			.setOutputCol("features")

		val pipe = new Pipeline().setStages(Array(assembler, scaler))

		df = RenameBadNaming(df).drop("hero_damage_out", "kills_out")
		df = pipe.fit(df).transform(df).select("prediction", "features")

		var list: List[Map[String, Double]] = List()

		df.collect.foreach(row => {
			var map: Map[String, Double] = Map()
			val arrays = stringToArray(row.toSeq(1).toString)

			for (x <- 0 to columns.size - 1) {
				map = map + (columns(x) -> arrays(x))
			}

			list = list :+ map
		})

		Json.toJson(list)
	}
	def stringToArray(string: String): Array[Double] = {
		string.replace("[", "").replace("]", "").split(",").map(v => v.toDouble)
	}
}

