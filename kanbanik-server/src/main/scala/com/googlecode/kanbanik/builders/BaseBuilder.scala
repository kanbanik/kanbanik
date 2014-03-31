package com.googlecode.kanbanik.builders

import org.bson.types.ObjectId

class BaseBuilder {

  def determineId(entity: {def id: String}): Option[ObjectId] = {
    if (entity == null || entity.id == null) {
      None
    } else {
      Some(new ObjectId(entity.id))
    }
  }
}