package helper

object Globals {
	val startAt = 5000000000L
	val endAt = 5999999999L
	val numberOfFeeds = 100
	val key = "&key=0D1D08A26851831B5C107E9DDB80EC0E"
	val api = "https://api.steampowered.com/IDOTA2Match_570/GetMatchDetails/V001/?match_id="
	val attributes = Array(
		"gold_per_min", "level", "leaver_status", "xp_per_min", "gold_spent",
		"deaths", "denies", "hero_damage", "tower_damage", "last_hits", "hero_healing"
	)

	val datasetsRoute = "C:/Users/Labinot/Desktop/tema_datas/"

	val classificationModel = "classification_model"

	val kaggleDataset = "kaggle_dataset"
}
