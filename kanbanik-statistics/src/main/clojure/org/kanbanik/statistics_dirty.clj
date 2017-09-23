(ns org.kanbanik.statistics-dirty
  (:use org.kanbanik.statistics)
  (:require [clojure.data.json :as json])
  (:require [clojure.string :as str])
  (:refer-clojure :exclude [sort find])
  (:require [monger.core :as mg]
            [monger.collection :as mc]
            [monger.query :refer :all])
  (:import [com.mongodb MongoOptions ServerAddress])

  (:gen-class
    :name org.kanbanik.statistics
    :methods [#^{:static true} [execute [java.util.Map java.lang.Integer] java.lang.String]]))

(defn update-map [m f] 
  (reduce-kv (fn [m k v] 
    (assoc m k (f v))) {} m))

(defn objectid-to-str [v]
  (if (instance? org.bson.types.ObjectId v)
    (str v)
    v)
)

(defn to-clojure-coll [c]
  (if (instance? java.util.Map c)
    (into {} c)
    (if (instance? java.util.List c)
      (into [] c)
      c
)))

(defn keywordify [coll]
  (defn keywordify-element [e]
    (if (str/starts-with? e ":")
      (keyword (subs e 1))
      e)
  )
  (let [m (to-clojure-coll coll)]
  (if (not (map? m))
    (keywordify-element m)
    (into {} (for [[ko vo] m]
             (let [
                   k (to-clojure-coll ko)
                   v (to-clojure-coll vo)
                   ]
             (if (map? v)
             [(keywordify-element k) (keywordify v)]
             (if (sequential? v)
               [(keywordify-element k) (map keywordify v)]
               [(keywordify-element k) (keywordify-element v)]
             )
           ))))))
)

(defn -execute [descriptor timeframe]
  (let [^MongoOptions opts (mg/mongo-options {:threads-allowed-to-block-for-connection-multiplier 300})
        ^ServerAddress sa  (mg/server-address "127.0.0.1" 27017)
        conn               (mg/connect sa opts)
        db   (mg/get-db conn "kanbanikdb")
        event-stream (with-collection db "events"
                       (find {})
                       (sort (array-map :timestamp 1)))
        clean-event-stream (map #(update-map % objectid-to-str) event-stream)
        ]
    (json/write-str (run-analisis 
                (keywordify (into {} descriptor))
                timeframe
                clean-event-stream
                ))))
