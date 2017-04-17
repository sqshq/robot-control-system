package com.sqshq.akka.demo

import io.gatling.core.Predef._
import io.gatling.core.scenario.Simulation
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.{http, status}

import scala.concurrent.duration._

class SensorDataSimulation extends Simulation {

   val offerMessageScenario: ScenarioBuilder = scenario("Offer message scenario")
      .during(30 seconds) {
         exec(http("Sensor data request")
            .post("http://localhost:10000/sensors/data")
            .body(StringBody("data"))
            .check(status.is(200)))
      }

   setUp(offerMessageScenario
      .inject(atOnceUsers(100)))
      .throttle(reachRps(100).in(5 seconds), holdFor(30 seconds))
      .protocols(http)
}
