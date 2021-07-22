package com.mhoja

import io.gatling.core.structure.PopulationBuilder

object TestUtils {

  def toSequential(scenarios: List[PopulationBuilder]): PopulationBuilder = {
    if (scenarios.isEmpty) {
      return null
    }
    if (scenarios.length > 1) {
      val children = toSequential(scenarios.takeRight(scenarios.length - 1))
      if (children != null) {
        return scenarios.head.andThen(children)
      }
    }
    scenarios.head
  }

}
