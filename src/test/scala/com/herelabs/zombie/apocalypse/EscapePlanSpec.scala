package com.herelabs.zombie.apocalypse

import org.specs2.mutable._

class EscapePlanSpec extends Specification {

  "The Zombie Apocalypse Escape Plan" should {
    "end with 'Plan'" in {
      "Escape Plan" must endWith("Plan")
    }
  }
}
