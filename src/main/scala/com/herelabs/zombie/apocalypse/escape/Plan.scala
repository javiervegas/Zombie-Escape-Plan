package com.herelabs.zombie.apocalypse.escape

class Plan(group: Seq[Int], tiredness_factor: Int = 20, rest_boost: Int = 5) {
  def findQuickestPath: Path = new Path 
}

class Path() {
  def time: Int = 60
}
