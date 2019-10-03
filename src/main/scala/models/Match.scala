package models

case class Match
(
	barracks_status_dire: Int,
	barracks_status_radiant: Int,

	tower_status_dire: Int,
	tower_status_radiant: Int,

	radiant_score: Int,
	dire_score: Int,

	d_rad_gold_per_min: Int,
	d_dire_gold_per_min: Int,

	d_rad_level: Int,
	d_dire_level: Int,

	d_rad_gold_spent: Int,
	d_dire_gold_spent: Int,

	d_rad_leaver_status: Int,
	d_dire_leaver_status: Int,

	d_rad_xp_per_min: Int,
	d_dire_xp_per_min: Int,

	d_rad_hero_damage: Int,
	d_dire_hero_damage: Int,

	d_rad_tower_damage: Int,
	d_dire_tower_damage: Int
)
