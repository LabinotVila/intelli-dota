package algorithms

import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

abstract class Classification {
	def importDataframe(path: String): Unit = {

	}
}
