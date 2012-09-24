package com.herelabs.zombie.apocalypse.escape

import org.joda.time.{Minutes,Seconds}
import scala.actors._
import Actor._

//the two extremes of the bridge
abstract class Location
case object SanFrancisco extends Location {
  override def toString = "<- "
}
case object Sausalito extends Location {
  override def toString = "-> "
}

//encapsulates data of survivors that are trying to cross the bridge 
case class Survivor(name: String, crossingTime: Minutes)

//case class to send messages to the Timer actor
case object GetBestPath

//escape plan for a group of survivors
class Plan(val survivors: List[Survivor], tirednessFactor: Int, restBoost: Seconds) {

  //overloaded constructor for convenience
  def this(times: Seq[Minutes], tirednessFactor: Int = Plan.TirednessFactor, restBoost: Seconds = Plan.RestBoost) = {
    this(times.zipWithIndex.map{ s => Survivor(s._2.toString, s._1) }.toList, tirednessFactor, restBoost)
  }
 
  val initial = new ChainsawState(SanFrancisco, survivors)

  //this class describes where is the chainsaw and who has it 
  class ChainsawState(location: Location, group: List[Survivor]) {

    //we are done if everyone and the chainsaw have crossed the bridge to Sausalito
    def done = location==Sausalito && group.size==survivors.size

    def crossBridge(f: (Crossing, ChainsawState) => Unit) = location match {
      case SanFrancisco => 
        //send two guys to safety carrying the chainsaw (dealing for corner case of single survivor)
        val toSafetyCombinations = group.combinations(2) match {
          case i if i.isEmpty => group.combinations(1)
          case i => i
        }
        toSafetyCombinations foreach { toSafety =>
          f(new Crossing(Sausalito, toSafety), new ChainsawState(Sausalito, (survivors filterNot (group contains))  ++ toSafety))
        }
      case Sausalito => 
        //send back someone to danger carrying with the chainsaw
        //TODO: we send back just 1 person because sending back 2 people seems unlikely to makes things faster and could result in infinite loops, but
        //in some weird case the best solution could involve 2 people going back together. A way to deal with this would be to calculate first the best
        //solution for only one person going back and then explore solutions allowing 2 persons to go back as long as the full path does not take longer 
        //than the best 1 person going back solution. However, this is most likely going to be very slow and most likely will not find a better solution
        //so it is not implemented to achieve better performance.
        group.foreach { toDangerSingle =>
          f(new Crossing(SanFrancisco, List(toDangerSingle)), new ChainsawState(SanFrancisco, toDangerSingle :: (survivors filterNot (group contains)) ))
        }
    }
  }

  def quickestTime : Seconds = {
    quickestPath.time
  }

  lazy val quickestPath : Path = {
    timer.start
    explorePaths(Nil, initial)
    timer !? GetBestPath match { case path: Path => path }
  }

  def explorePaths(crossings: List[Crossing], state: ChainsawState) {
    if (state.done) {
      timer ! Path(crossings)
    } else {
      //check if there is a completed path faster that the current path in progress
      timer !? PathInProgress(crossings) match {
        //continue exploring
        case true => state crossBridge { (crossing, state) => explorePaths(crossing :: crossings, state) }
        //stop recursing on this path, there are faster solutions
        case false => Unit
      }
    }
  }

  val timer = new Timer

  class Timer extends Actor {
    private var best: Option[Path]  = None
    def act = loop {
      react {
        case path: Path => 
          best match {
            case Some(bestPath) if bestPath.time.isLessThan(path.time) => Unit
            case _ => best = Some(path)
          }
        case path: PathInProgress =>
          best match {
            case Some(bestPath) if bestPath.time.isLessThan(path.time) => reply(false)
            case _ => reply(true)
          }
        case GetBestPath => 
          reply(best.get)
          exit
      }
    }
  }

  case class PathInProgress(crossings: List[Crossing]) {
    def time = Path(crossings).time
  }

  case class Path(crossings: List[Crossing]) {
    //path time is the sum of individual crosing times, we keep resting times data since last crossing in a map that gets updated after each crossing
    private lazy val times: List[Seconds] = {
      crossings.foldLeft((List[Seconds](),Map[Survivor,Seconds]())){ (res, crossing) => 
        val time = crossing.time(res._2)
        (time :: res._1 , crossing.group.foldLeft(res._2.map{ k => (k._1, k._2.plus(time))}){ (map, survivor) => map + (survivor -> Seconds.seconds(0)) } ) 
      }._1.reverse
    }

    lazy val time: Seconds = times.reduceLeft(_.plus(_))

    override def toString : String = {
      crossings.zip(times).map{ t => t._1 + " (" + "\\.0$".r.replaceFirstIn((t._2.getSeconds/60F).toString, "") + ")" }.mkString(" ")
    }

  }

  class Crossing(direction: Location, val group: List[Survivor]) {
    //time depends on the speed of the crossers, if they are tired because they already crossed before and if they have rested after their last crossing
    def time(tired: Map[Survivor,Seconds]): Seconds = {
      group.map{ s => s.crossingTime.toStandardSeconds.plus(
        tired.get(s) match {
          case Some(rest: Seconds) => 
            val tiredness = s.crossingTime.toStandardSeconds.multipliedBy(tirednessFactor).dividedBy(100)
            val restedness = restBoost.multipliedBy(rest.toStandardMinutes.getMinutes)
            //resting compensates for tiredness up to original speed
            tiredness.isGreaterThan(restedness) match {
              case true => tiredness.minus(restedness)
              case false => Seconds.seconds(0)
            }
          case _ => Seconds.seconds(0)
        })
      //group speed is the speed of the slowest person 
      }.reduceLeft{ (a,b) => if (a.isGreaterThan(b)) a else b }
    }

    override def toString : String = {
      direction.toString+group.map{_.name}.sorted.mkString(" ")
    }

  }

  override def toString : String = {
    quickestPath.toString
  }

}

object Plan extends App {

  protected val TirednessFactor: Int = 20
  val RestBoost: Seconds = Seconds.seconds(5)

  //try to read a data file
  val file = try {
    args(0)
  } catch {
    case ex: java.lang.ArrayIndexOutOfBoundsException =>
      println("Please pass a filename so the app can read crossing times data")
      sys.exit
  }
  val survivors = try {
    scala.io.Source.fromFile(file).getLines.map{ s => Minutes.minutes(s.toInt) }.toList
  } catch {
    case ex: java.io.FileNotFoundException =>
      println("Sorry, I can not find that file to read crossing times data")
      sys.exit
    case ex: java.lang.NumberFormatException =>
      println("Sorry, the file can only contain lines with digits representing the number of minutes a person needs to cross the bridge")
      sys.exit
  }

  //print the output in format like "-> 0 1 (20) <- 1 (18) -> 2 5 (18) <- 0 5"
  println(new Plan(survivors))

}

