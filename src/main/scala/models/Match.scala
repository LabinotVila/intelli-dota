package models

case class Match
(
	barracks_status_dire: Int,
	barracks_status_radiant: Int,

	tower_status_dire: Int,
	tower_status_radiant: Int,

	radiant_score: Int,
	dire_score: Int,

	derived_radiant_stacked_camps: Int,
	derived_dire_stacked_camps: Int,

	derived_radiant_gpm: Int,
	derived_dire_gpm: Int,

	first_blood_time: Int,
	derived_radiant_first_blood: Int,

	derived_radiant_levels: Int,
	derived_dire_levels: Int,

	derived_radiant_obs: Int,
	derived_dire_obs: Int,

	derived_radiant_roshans: Int,
	derived_dire_roshans: Int,

	radiant_win: Int
)
