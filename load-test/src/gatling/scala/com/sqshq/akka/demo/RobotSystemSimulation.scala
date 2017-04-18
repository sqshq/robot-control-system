package com.sqshq.akka.demo

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.{http, status, ws}

import scala.concurrent.duration._

class RobotSystemSimulation extends Simulation {

   val robotScenario: ScenarioBuilder = scenario("Robot behavior scenario")
      .exec(ws("Open WS").open("ws://localhost:20000/robots/socket"))
      .pause(30 seconds)
      .exec(ws("Close WS").close)

   val sensorDataScenario: ScenarioBuilder = scenario("Offer message scenario")
      .during(30 seconds) {
         exec(http("Sensor data request")
            .post("http://localhost:10000/sensors/data")
            .body(StringBody("data"))
            .check(status.is(200)))
      }

   setUp(
      robotScenario
         .inject(rampUsers(100).over(5 seconds))
         .protocols(http),
      sensorDataScenario
         .inject(atOnceUsers(100))
         .throttle(reachRps(150).in(5 seconds), holdFor(30 seconds))
         .protocols(http)
   )
}
