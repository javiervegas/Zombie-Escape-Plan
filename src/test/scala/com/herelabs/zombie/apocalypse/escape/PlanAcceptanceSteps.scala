package com.herelabs.zombie.apocalypse.escape

import org.joda.convert.FromString
import org.joda.time.{Minutes,Seconds}
import org.specs2._
import matcher.MustMatchers._
import execute.Result
import specification._

object group extends Given[Seq[Minutes]]("\\D*([\\d\\,\\s]+)\\D*") {            
  def extract(text: String): Seq[Minutes] = extract1(text).split("\\,?\\s").map{ s => Minutes.minutes(s.toInt) }
} 

object aNumber extends Given[Int]("\\D*(\\d+)\\D*") {                       
  def extract(text: String): Int = extract1(text).toInt                     
} 

object aPlan extends When[(Seq[Minutes], Int, Int), Plan] {                      
  def extract(i: (Seq[Minutes], Int, Int), text: String): Plan = new Plan(i._1, i._2, Seconds.seconds(i._3))
} 

object time extends Then[Plan]("\\D*([\\d\\.]+)\\D*") {                          
  def extract(plan: Plan, text: String): Result = plan.quickestTime.getSeconds/60F must be equalTo(extract1(text).toFloat)
} 

object output extends Then[Plan](".*\\'(-.*)\\'") {                                                     
  def extract(plan: Plan, text: String): Result = plan.toString must be equalTo(extract1(text))
}   
