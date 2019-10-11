import org.apache.log4j.{Level, Logger}
import org.apache.spark.ml.clustering.KMeans
import org.apache.spark.ml.evaluation.ClusteringEvaluator
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.sql.SparkSession

object Clustering {
	def main(args: Array[String]) = {
		Logger.getLogger("org").setLevel(Level.OFF)
		Logger.getLogger("akka").setLevel(Level.OFF)

		val spark = SparkSession.builder.appName("T").master("local[*]").getOrCreate

		val heroNames = spark.read
    		.option("header", true)
    		.option("inferSchema", true)
    		.csv("players_dataset/hero_names.csv")
    		.withColumnRenamed("hero_id", "hero__id")

		var players = spark.read
			.option("header", true)
			.option("inferSchema", true)
			.csv("players_dataset/players.csv")

		val elements = Array(
			"avg(gold)", "avg(gold_per_min)", "avg(xp_per_min)", "avg(kills)", "avg(deaths)", "avg(assists)",
			"avg(denies)", "avg(last_hits)", "avg(hero_damage)", "avg(hero_healing)", "avg(level)")
		players = players
			.select("hero_id",
			"gold", "gold_per_min", "xp_per_min", "kills", "deaths", "assists", "denies",
			"last_hits", "hero_damage", "hero_healing", "level")

		var groupedBy = players
			.groupBy("hero_id").mean()
			.join(heroNames, heroNames("hero__id").equalTo(players("hero_id")))
			.drop("hero__id", "name")
			.sort("localized_name")

		val assembler = new VectorAssembler()
			.setInputCols(elements)
			.setOutputCol("features")

		groupedBy = assembler.transform(groupedBy)

		val numClusters = 6
		val numIterations = 250

		val kmeans = new KMeans().setK(numClusters).setSeed(1L).setMaxIter(numIterations)
		val model = kmeans.fit(groupedBy)

		val predictions = model.transform(groupedBy)

		val evaluator = new ClusteringEvaluator()

		val silhouette = evaluator.evaluate(predictions)
		println("Silhouette with squared euclidean distance = $silhouette")

		println("Cluster Centers: ")
		model.clusterCenters.foreach(println)

	}
}
