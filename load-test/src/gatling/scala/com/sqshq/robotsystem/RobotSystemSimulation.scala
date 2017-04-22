package com.sqshq.robotsystem

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.{http, status, ws}

import scala.concurrent.duration._
import scala.util.Random

class RobotSystemSimulation extends Simulation {

   val robotScenario: ScenarioBuilder = scenario("Robot behavior scenario")
      .exec(ws("Open WS").open("ws://localhost:20000/robots/socket"))
      .pause(30 seconds)
      .exec(ws("Close WS").close)

   val sensorDataScenario: ScenarioBuilder = scenario("Offer message scenario")
      .during(30 seconds) {
         feed(Feeder.sensorData)
            .exec(http("Sensor data request")
               .post("http://localhost:10000/sensors/data")
               .body(StringBody("${sensorData}"))
               .check(status.is(200)))
      }

   setUp(
      robotScenario
         .inject(rampUsers(100).over(5 seconds))
         .protocols(http),
      sensorDataScenario
         .inject(atOnceUsers(300))
         .throttle(reachRps(300).in(5 seconds), holdFor(30 seconds))
         .protocols(http)
   )
}

object Feeder {
   val sensorData = new Feeder[Int] {
      override def hasNext = true
      override def next: Map[String, Int] = {
         Map("sensorData" -> (Random.nextInt(1000) + 1000))
      }
   }
}