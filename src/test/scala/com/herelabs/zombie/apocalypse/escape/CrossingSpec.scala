package com.herelabs.zombie.apocalypse.escape

import org.joda.time.{Minutes,Seconds}
import org.specs2.mutable._
import org.specs2.execute.Success

class CrossingSpec extends Specification {
  
  val survivors = List[Survivor](
    Survivor("Larry",Minutes.minutes(5)),
    Survivor("Moe",Minutes.minutes(25))
  )
  val zp = new Plan(List[Survivor](), 20, Seconds.seconds(5))
  val crossing = new zp.Crossing(Sausalito, survivors)
  val tired: Map[Survivor,Seconds] = Map()

  "The crossing" should {
    "take 25 minutes if Larry and Moe are not tired" in {
      crossing.time(tired).toStandardMinutes must be equalTo(Minutes.minutes(25))
    }
    "take 25 minutes if Larry is tired" in {
      crossing.time(tired + (survivors(0) -> Seconds.seconds(0))).toStandardMinutes must be equalTo(Minutes.minutes(25))
    }
    "take 30 minutes if Moe is tired" in {
      crossing.time(tired + (survivors(1) -> Seconds.seconds(0))).toStandardMinutes must be equalTo(Minutes.minutes(30))
    }
    "take 29 minutes if Moe is tired but rested for 12 minutes" in {
      crossing.time(tired + (survivors(1) -> Seconds.seconds(12*60))).toStandardMinutes must be equalTo(Minutes.minutes(29))
    }
    "take 28 minutes if Moe is tired but rested for 24 minutes" in {
      crossing.time(tired + (survivors(1) -> Seconds.seconds(24*60))).toStandardMinutes must be equalTo(Minutes.minutes(28))
    }
    "take 25 minutes if Moe is tired but rested for a long time" in {
      crossing.time(tired + (survivors(1) -> Seconds.seconds(120*60))).toStandardMinutes must be equalTo(Minutes.minutes(25))
    }
  }

}
