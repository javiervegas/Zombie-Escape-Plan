package com.herelabs.zombie.apocalypse.escape

import org.joda.time.{Minutes,Seconds}

class Plan(group: Seq[Minutes], tiredness_factor: Int = 20, rest_boost: Seconds = Seconds.seconds(5)) {
  def findQuickestPath: Path = new Path 
}

class Path() {
  def time: Minutes = Minutes.minutes(60)
}
