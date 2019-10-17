package runnable

import helper.Globals
import org.apache.spark.ml.PipelineModel
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.stat.Correlation
import org.apache.spark.sql.functions._
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql._
import org.apache.spark.sql.types._

object Dataset {
	def getCorrelationMatrix(spark: SparkSession, dataframe: DataFrame) = {
		val columnNames = dataframe.schema.names.filter(col => !col.equals("radiant_win"))

		val assembler = new VectorAssembler()
			.setInputCols(columnNames)
			.setOutputCol("features")
		val df = assembler.transform(dataframe)

		Correlation.corr(df, "features").toJSON.collectAsList().toString
	}


	def predict(spark: SparkSession, dataframe: DataFrame, s: Seq[Int]) = {
		// create dataframe with the given sequence input from the user
		val RDD = spark.sparkContext.makeRDD(List(Row.fromSeq(s)))
		val columns = dataframe.schema.fields.filter(x => !x.name.equals("radiant_win"))
		val df = spark.createDataFrame(RDD, StructType(columns))

		// make the new schema ready
		val newDF = dataframe.schema
			.add("probability", DoubleType)
			.add("prediction", DoubleType)
    		.names.filter(col => !col.equals("radiant_win"))

		// load model and try the new input, selecting `newDF` columns and return the result
		val model = PipelineModel.load(Globals.MAIN_ROUTE + Globals.CLASSIFIED_MODEL).transform(df)
		model.select(newDF.map(col): _*).toJSON.collectAsList().toString
	}

	def getColumns(spark: SparkSession, dataframe: DataFrame) = {
		dataframe.schema.names.filter(x => !x.equals("radiant_win"))
	}

	def getSample(spark: SparkSession, dataframe: DataFrame, percentage: Double) = {
		dataframe.sample(percentage).toJSON.collectAsList().toString
	}
}
