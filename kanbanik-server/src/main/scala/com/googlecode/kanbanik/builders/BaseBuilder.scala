package com.googlecode.kanbanik.builders

import com.googlecode.kanbanik.dto.IdentifiableDto
import org.bson.types.ObjectId

class BaseBuilder {
  def determineId(entity: IdentifiableDto): Option[ObjectId] = {
    if (entity.getId() == null) {
      None
    } else {
      Some(new ObjectId(entity.getId()))
    }
  }
}