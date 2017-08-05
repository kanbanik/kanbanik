(ns org.kanbanik.statistics-dirty
  (:use org.kanbanik.statistics)
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress])

  (:gen-class
    :name org.kanbanik.statistics
    :methods [#^{:static true} [mysome [java.util.Map] String]]))

(defn -mysome
  [my-map]

  (let [^MongoOptions opts (mg/mongo-options {:threads-allowed-to-block-for-connection-multiplier 300})
      ^ServerAddress sa  (mg/server-address "127.0.0.1" 27017)
      conn               (mg/connect sa opts)
      db   (mg/get-db conn "kanbanikdb")
      res (mc/find-maps db "events")
        ]   

    (apply str (run-analisis 
     {:reduce-function :merge 
      :result-descriptors [{
                            :filter {:eventType "TaskDeleted"}
                            :function :pass
                            }]}
     nil
     res    
))))
