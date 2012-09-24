package com.herelabs.zombie.apocalypse.escape

import org.joda.time.{Minutes,Seconds}
import org.specs2.mutable._
import org.specs2.execute.Success

class PathSpec extends Specification {
  val zp = new Plan(List(20, 15, 18, 27, 12, 30).map{Minutes.minutes(_)}, 20, Seconds.seconds(5))
  val path = zp.Path(List(
    new zp.Crossing(Sausalito, List(zp.survivors(0),zp.survivors(1))),
    new zp.Crossing(SanFrancisco, List(zp.survivors(1))),
    new zp.Crossing(Sausalito, List(zp.survivors(2),zp.survivors(4))),
    new zp.Crossing(SanFrancisco, List(zp.survivors(0),zp.survivors(4)))
  ))  

  "The back and forth example path" should {
    "take 77 minutes" in {
      path.toString must be equalTo("-> 0 1 (20) <- 1 (18) -> 2 4 (18) <- 0 4 (21)")
    }
  }

}
