package com.mhoja.gatling

import io.gatling.core.structure.PopulationBuilder

object TestUtils {

  def toSequential(scenarios: List[PopulationBuilder]): PopulationBuilder = {
    if (scenarios.length == 1) {
      return scenarios.head
    }
    val children = toSequential(scenarios.takeRight(scenarios.length - 1))
    scenarios.head.andThen(children)
  }

}
