package models

case class Match
(
	barracks_status_dire: Int,
	barracks_status_radiant: Int,

	tower_status_dire: Int,
	tower_status_radiant: Int,

	radiant_score: Int,
	dire_score: Int,

	d_rad_gpm: Int,
	d_dire_gpm: Int,

	d_rad_levels: Int,
	d_dire_levels: Int,

	d_rad_goldSpent: Int,
	d_dire_goldSpent: Int,

	d_rad_leaverStatus: Int,
	d_dire_leaverStatus: Int,

	radiant_win: Int
)
