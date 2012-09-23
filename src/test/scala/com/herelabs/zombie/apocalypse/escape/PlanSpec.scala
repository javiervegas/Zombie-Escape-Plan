package com.herelabs.zombie.apocalypse.escape

import org.joda.convert.FromString
import org.joda.time.{Minutes,Seconds}
import org.specs2._
import org.specs2.execute.Result
import specification._

class PlanSpec extends Specification { def is =

  "Group escaping in 60 minutes with no tiredness complications"              ^ br^
    "Given the group crossing times are 5, 10, 20, 25 minutes"                ^ group^
    "And the tiredness factor is 0%"                                          ^ aNumber^
    "And each minute of rest gives a 0 seconds boost"                         ^ aNumber^
    "When they path through the quickest path"                                ^ path^
    "Then it should take them 60 minutes"                                     ^ time^
                                                                              endp^
  "Group escaping in 17 minutes with no tiredness complications"              ^ br^
    "Given the group crossing times are 1, 2, 5, 10 minutes"                  ^ group^
    "And the tiredness factor is 0%"                                          ^ aNumber^
    "And each minute of rest gives a 0 seconds boost"                         ^ aNumber^
    "When they path through the quickest path"                                ^ path^
    "Then it should take them 17 minutes"                                     ^ time^
                                                                              endp^
  "Group escaping in 17 minutes with no rest boost complications"             ^ br^
    "Given the group crossing times are 10, 10, 10, 10 minutes"               ^ group^
    "And the tiredness factor is 20%"                                          ^ aNumber^
    "And each minute of rest gives a 0 seconds boost"                         ^ aNumber^
    "When they path through the quickest path"                                ^ path^
    "Then it should take them 56 minutes"                                     ^ time^
                                                                              end
  object group extends Given[Seq[Minutes]]("\\D*([\\d\\,\\s]+)\\D*") {            
    def extract(text: String): Seq[Minutes] = extract1(text).split("\\,?\\s").map{ s => Minutes.minutes(s.toInt) }
  } 
  object aNumber extends Given[Int]("\\D*(\\d+)\\D*") {                       
    def extract(text: String): Int = extract1(text).toInt                     
  } 
  object path extends When[(Seq[Minutes], Int, Int), Path] {                      
    def extract(i: (Seq[Minutes], Int, Int), text: String): Path = new Plan(i._1, i._2, Seconds.seconds(i._3)).findQuickestPath
  } 
  object time extends Then[Path]("\\D*(\\d+)\\D*") {                          
    def extract(path: Path, text: String): Result = path.time must_== Minutes.minutes(extract1(text).toInt)
  } 

}
