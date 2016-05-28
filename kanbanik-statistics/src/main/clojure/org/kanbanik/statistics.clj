(ns org.kanbanik.statistics
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  )

(defn f->t [] 12)

(defn my-first-function [x] (+ x 1))

(defn load-something-from-mongo []
  (let [conn (mg/connect {:host "localhost" :port 27017})
        db (mg/get-db conn "kanbanikdb")
        coll "users"
        ]

    (mc/find db coll)
    )
  )

