package com.herelabs.zombie.apocalypse.escape

import org.joda.time.{Minutes,Seconds}

case class Survivor(name: String, crossingTime: Minutes) {
}

abstract class Location
case object SanFrancisco extends Location
case object Sausalito extends Location

class Crossing(direction: Location, group: List[Survivor]) {
  def time: Minutes = group.map{ s => s.crossingTime }.reduceLeft{ (a,b) => if (a.isGreaterThan(b)) a else b }
}

case class Path(crossings: Option[List[Crossing]]) {
  def time: Minutes = crossings.get.map{_.time}.reduceLeft{_.plus(_)}
}

class Plan(survivors: List[Survivor], tiredness_factor: Int, rest_boost: Seconds) {

  def this(times: Seq[Minutes], tiredness_factor: Int = 20, rest_boost: Seconds = Seconds.seconds(5)) = {
    this(times.zipWithIndex.map{ s => Survivor(s._2.toString, s._1) }.toList, tiredness_factor, rest_boost)
  }
 
  val initial = new ChainsawState(SanFrancisco, survivors)

  def foreach(f: Path => Unit) {
    def solve(crossings: List[Crossing], state: ChainsawState) {
      if (state.done) {
        f(Path(Some(crossings)))
      } else {
        state crossBridge { (crossing, state) => solve(crossing :: crossings, state) }
      }
    }
    solve(Nil, initial)
  }

  def findQuickestPath : Path = {
    var best = Path(None)
    foreach { path =>
      best = best match {
        case Path(Some(p)) if best.time.isLessThan(path.time) => best
        case _ => path
      }
    }
    best
  }
  
  class ChainsawState(location: Location, endangeredGroup: List[Survivor]) {
    def done = endangeredGroup.isEmpty
    def crossBridge(f: (Crossing, ChainsawState) => Unit) = location match {
      case SanFrancisco => 
        //send two guys to safety carrying the chainsaw
        endangeredGroup.combinations(2) foreach { toSafetyPair =>
          f(new Crossing(Sausalito, toSafetyPair), new ChainsawState(Sausalito, endangeredGroup diff toSafetyPair))
        }
      case Sausalito => 
        //send back someone to danger carrying with the chainsaw
        //TODO: sending back 2 people seems unlikely to makes things faster and could result in infinity loops
        (survivors diff endangeredGroup).foreach { survivor =>
          f(new Crossing(SanFrancisco, List(survivor)), new ChainsawState(SanFrancisco, survivor :: endangeredGroup))
        }
    }
  }
}

