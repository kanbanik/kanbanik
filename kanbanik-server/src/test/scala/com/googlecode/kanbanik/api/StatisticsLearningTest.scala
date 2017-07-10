package com.googlecode.kanbanik.api

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.kanbanik.statistics
import org.scalatest.FlatSpec

@RunWith(classOf[JUnitRunner])
class StatisticsLearningTest extends FlatSpec {
  "statistics" should "be calleable" in {

    val x = statistics.mysome()
    print(x)

  }
}
