package utilities

import org.apache.spark.sql.{Dataset, Row}

object RenameBadNaming {
	def apply(dataset: Dataset[Row]) = {
		var goodDataset = dataset
		val schema = dataset.schema.fields

		for (x <- schema) {
			if (x.name.contains("(")) {
				val name = x.name.split('(')(1).replace(")", "")

				goodDataset = goodDataset.withColumnRenamed(x.name, name)
			}
		}

		goodDataset
	}
}
