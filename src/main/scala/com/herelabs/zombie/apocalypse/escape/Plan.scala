package com.herelabs.zombie.apocalypse.escape

import org.joda.time.{Minutes,Seconds}
import scala.actors._
import Actor._

abstract class Location
case object SanFrancisco extends Location {
  override def toString = "<- "
}
case object Sausalito extends Location {
  override def toString = "-> "
}

case object GetBestPath

case class Survivor(name: String, crossingTime: Minutes)

class Plan(survivors: List[Survivor], tirednessFactor: Int, restBoost: Seconds) {

  def this(times: Seq[Minutes], tirednessFactor: Int = 20, restBoost: Seconds = Seconds.seconds(5)) = {
    this(times.zipWithIndex.map{ s => Survivor(s._2.toString, s._1) }.toList, tirednessFactor, restBoost)
  }
 
  val initial = new ChainsawState(SanFrancisco, survivors)

  val timer = new Timer

  lazy val quickestPath : Path = {
    timer.start
    solve(Nil, initial)
    timer !? GetBestPath match { case path: Path => path }
  }

  def solve(crossings: List[Crossing], state: ChainsawState) {
    if (state.done) {
      timer ! Path(crossings)
    } else {
      state crossBridge { (crossing, state) => solve(crossing :: crossings, state) }
    }
  }

  def quickestTime : Minutes = {
    quickestPath.time
  }

  override def toString : String = {
    quickestPath.toString
  }

  //this class describes where is the chainsaw and who has it 
  class ChainsawState(location: Location, group: List[Survivor]) {

    //we are done if everyone and the chainsaw have crossed the bridge to Sausalito
    def done = location==Sausalito && group.size==survivors.size

    def crossBridge(f: (Crossing, ChainsawState) => Unit) = location match {
      case SanFrancisco => 
        //send two guys to safety carrying the chainsaw
        group.combinations(2) foreach { toSafetyPair =>
          f(new Crossing(Sausalito, toSafetyPair), new ChainsawState(Sausalito, survivors -- group ++ toSafetyPair))
        }
      case Sausalito => 
        //send back someone to danger carrying with the chainsaw
        //TODO: sending back 2 people seems unlikely to makes things faster and could result in infinity loops
        group.foreach { toDangerSingle =>
          f(new Crossing(SanFrancisco, List(toDangerSingle)), new ChainsawState(SanFrancisco, toDangerSingle :: (survivors -- group) ))
        }
    }
  }

  class Crossing(direction: Location, val group: List[Survivor]) {
    def time(tired: Set[Survivor]): Minutes = group.map{ s => 
      tired contains s match {
        case true => s.crossingTime.multipliedBy(100+tirednessFactor).dividedBy(100) 
        case false => s.crossingTime
      } 
    }.reduceLeft{ (a,b) => if (a.isGreaterThan(b)) a else b }
    override def toString : String = {
      direction.toString+group.map{_.name}.mkString(" ")
    }
  }

  case class Path(crossings: List[Crossing]) {
    private lazy val times: List[Minutes] = crossings.foldLeft((List[Minutes](),Set[Survivor]())){ (res, crossing) => (crossing.time(res._2) :: res._1 , res._2 ++ crossing.group) }._1.reverse
    lazy val time: Minutes = times.reduceLeft(_.plus(_))
    override def toString : String = {
      crossings.zip(times).map{ t => t._1+" ("+t._2.getMinutes+")" }.mkString(" ")
    }
  }

  class Timer extends Actor {
    private var best: Option[Path]  = None
    def act = loop {
      react {
        case path: Path => 
          best match {
            case Some(bestPath) if bestPath.time.isLessThan(path.time) => Unit
            case _ => best = Some(path)
          }
        case GetBestPath => 
          reply(best.get)
          exit
      }
    }
  }
}

object Plan extends App {

  val file = try {
    args(0)
  } catch {
    case ex: java.lang.ArrayIndexOutOfBoundsException =>
      println("no arg")
      sys.exit
  }
  val survivors = try {
    scala.io.Source.fromFile(file).getLines.map{ s => Minutes.minutes(s.toInt) }.toList
  } catch {
    case ex: java.io.FileNotFoundException =>
      println("bad file "+file)
      sys.exit
  }
  println(new Plan(survivors))
}

