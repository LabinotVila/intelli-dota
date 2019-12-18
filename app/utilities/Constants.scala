package utilities

object Constants {
//	val ROOT = "./main_route/"
	val ROOT = "/usr/src/app/"

	// file paths
	val STEAM_FETCHED_DATA = "fetched_steam_data"
	val KAGGLE_DATA = "kaggle_data"
	val CLUSTERED_MODEL = "clustered_model"
	val CLASSIFIED_MODEL = "classified_model"

	// classification result
	val WON_STRING = "Given the specific inputs, the radiant team has WON."
	val LOST_STRING = "Given the specific inputs, the radiant team has LOST."

	// spark details
	val APP_NAME = "IntelliDota"
	val MASTER = "local[*]"

	// data set kind
	val STEAM = "steam"
	val KAGGLE = "kaggle"

	// attributes
	val LEAVER_STATUS = "leaver_status"
	val RADIANT_WIN = "radiant_win"
	val PREDICTION = "prediction"
	val LOCALIZED_NAME = "localized_name"
	val FEATURES = "features"
}
