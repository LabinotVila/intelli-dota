package models

case class Match
(
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

	d_rad_hero_damage: Int,
	d_dire_hero_damage: Int,

	d_rad_tower_damage: Int,
	d_dire_tower_damage: Int,

	d_rad_win: Int
)