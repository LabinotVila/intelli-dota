package helper

case object Variables {
	val gameSkill = 3
	val startAt = 50120000
	val endAt = 50123669
	val numberOfFeeds = 20
	val allowNullObjects = false

	def getGameSkill(): Int = gameSkill
	def getStartAt(): Int = startAt
	def getEndAt(): Int = endAt
	def getNumberOfFeeds(): Int = numberOfFeeds
	def getAllowNullObjects(): Boolean = false
}
