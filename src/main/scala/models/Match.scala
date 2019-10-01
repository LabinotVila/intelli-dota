package models

case class Match
(
  match_id: Int,

  barracks_status_dire: Int,
  barracks_status_radiant: Int,

  cluster: Int,

  tower_status_dire: Int,
  tower_status_radiant: Int,

  radiant_score: Int,
  radiant_team_id: Int,

  dire_score: Int,
  dire_team_id: Int,

  duration: Int,
  engine: Int,
  first_blood_time: Int,
  game_mode: Int,
  human_players: Int,
  lobby_type: Int,
  skill: Int,
  patch: Int,
  region: Int,
  loss: Int
)
