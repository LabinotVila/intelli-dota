package utilities

import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.functions.col
import org.apache.spark.sql.types.{DoubleType, StructType}
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

object Dataset {
	def getCorrelationMatrix(dataframe: DataFrame) = {
		val columnNames = dataframe.schema.names.filter(col => !col.equals("radiant_win"))

		val assembler = new VectorAssembler().setInputCols(columnNames).setOutputCol("features")
		val df = assembler.transform(dataframe)

		Correlation.corr(df, "features").toJSON.collectAsList().toString
	}

	def predict(spark: SparkSession, dataframe: DataFrame, s: Seq[Int]) = {
		val RDD = spark.sparkContext.makeRDD(List(Row.fromSeq(s)))

		val columns = dataframe.schema.fields.filter(x => !x.name.equals("radiant_win"))
		val df = spark.createDataFrame(RDD, StructType(columns))

		val newDF = dataframe.schema
			.add("probability", DoubleType)
			.add("prediction", DoubleType)
    		.names.filter(col => !col.equals("radiant_win"))

		val model = PipelineModel.load(Constants.MAIN_ROUTE + Constants.CLASSIFIED_MODEL).transform(df)
		model.select(newDF.map(col): _*).toJSON.collectAsList().toString
	}

	def getColumns(dataframe: DataFrame) = {
		dataframe.schema.names.filter(x => !x.equals("radiant_win"))
	}

	def getSample(dataframe: DataFrame, percentage: Double) = {
		dataframe.sample(percentage).toJSON.collectAsList().toString
	}
}
