package runnable

object VizualizationMetrix {
	def calculateFormula(start: Double, end: Double, partitions: Int): Array[Double] = {
		val leftover = (start + end) / partitions

		var array = Array[Double](Double.NegativeInfinity)

		var sum = 0.0
		for (x <- 1 to partitions) {
			sum += leftover

			array = array :+ sum
		}

		array
	}
}
