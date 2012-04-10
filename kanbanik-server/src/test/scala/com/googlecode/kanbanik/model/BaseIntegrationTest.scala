package com.googlecode.kanbanik.model
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfter
import org.scalatest.Spec
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class BaseIntegrationTest extends Spec with BeforeAndAfter {
  
  before {
    DataLoader.fillDB
  }

  after {
    DataLoader.clearDB
  }
}