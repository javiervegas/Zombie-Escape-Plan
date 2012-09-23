package com.herelabs.zombie.apocalypse.escape

import org.specs2._
import specification._

class PlanAcceptanceSpec extends Specification { def is =

  "Group escaping in 60 minutes with no tiredness complications"                                       ^ br^
    "Given the group crossing times are 5, 10, 20, 25 minutes"                                         ^ group^
    "And the tiredness factor is 0%"                                                                   ^ aNumber^
    "And each minute of rest gives a 0 seconds boost"                                                  ^ aNumber^
    "When they plan their escape"                                                                      ^ aPlan^
    "Then the quickest time should be 60 minutes"                                                      ^ time^
    "And the output should be '-> 0 1 (10) <- 0 (5) -> 2 3 (25) <- 1 (10) -> 0 1 (10)'"                ^ output^
                                                                                                       endp^
  "Group escaping in 17 minutes with no tiredness complications"                                       ^ br^
    "Given the group crossing times are 1, 2, 5, 10 minutes"                                           ^ group^
    "And the tiredness factor is 0%"                                                                   ^ aNumber^
    "And each minute of rest gives a 0 seconds boost"                                                  ^ aNumber^
    "When they plan their escape"                                                                      ^ aPlan^
    "Then the quickest time should be 17 minutes"                                                      ^ time^
                                                                                                       endp^
  "Group escaping in 99 minutes with no rest boost complications"                                      ^ br^
    "Given the group crossing times are 15, 15, 30, 30 minutes"                                        ^ group^
    "And the tiredness factor is 20%"                                                                  ^ aNumber^
    "And each minute of rest gives a 0 seconds boost"                                                  ^ aNumber^
    "When they plan their escape"                                                                      ^ aPlan^
    "Then the quickest time should be 99 minutes"                                                      ^ time^
                                                                                                       endp^
  "Group escaping in 65 minutes with tiredness but no rest"                                            ^ br^
    "Given the group crossing times are 5, 10, 20, 25 minutes"                                         ^ group^
    "And the tiredness factor is 20%"                                                                   ^ aNumber^
    "And each minute of rest gives a 0 seconds boost"                                                  ^ aNumber^
    "When they plan their escape"                                                                      ^ aPlan^
    "Then the quickest time should be 65 minutes"                                                      ^ time^
    "And the output should be '-> 0 1 (10) <- 0 (6) -> 2 3 (25) <- 1 (12) -> 0 1 (12)'"                ^ output^
                                                                                                       endp
}
