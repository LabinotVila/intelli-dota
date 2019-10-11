package models

case class Match
(
	gold_per_min: Int,

	level: Int,

	leaver_status: Int,

	xp_per_min: Int,

	radiant_score: Int,

	gold_spent: Int,

	deaths: Int,

	denies: Int,

	hero_damage: Int,

	tower_damage: Int,

	last_hits: Int,

	hero_healing: Int,

	duration: Int,

	radiant_win: Int
)