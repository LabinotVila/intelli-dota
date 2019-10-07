package models

case class Match
(
/*
	barracks_status_radiant: Int,
	barracks_status_dire: Int,

	tower_status_radiant: Int,
	tower_status_dire: Int,
*/
	d_rad_kills: Int,
	d_dire_kills: Int,

	d_rad_gold_per_min: Int,
	d_dire_gold_per_min: Int,

	d_rad_level: Int,
	d_dire_level: Int,

	d_rad_leaver_status: Int,
	d_dire_leaver_status: Int,

	d_rad_xp_per_min: Int,
	d_dire_xp_per_min: Int,

	d_rad_denies: Int,
	d_dire_denies: Int,

	d_rad_win: Int
)