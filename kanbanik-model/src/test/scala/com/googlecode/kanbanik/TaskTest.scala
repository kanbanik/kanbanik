package com.googlecode.kanbanik;

import org.scalatest.GivenWhenThen
import org.scalatest.FeatureSpec
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.Spec
import org.scalatest.BeforeAndAfter


class TaskTest extends BaseIntegrationTest {

  val exp: ScalaExperiment = new ScalaExperiment

  describe("The ScalaExperiment should teach give me an introduction to Scala+mongoDB+ScalaTest") {

    it("should find all names with name 'name' from exp collection") {
      assert(exp.findAllNames("name").size === 4)
    }

    it("should fail when wrong argument is passed") {
      intercept[NoSuchElementException] {
        exp.findAllNames("something else")
      }
    }

  }
}