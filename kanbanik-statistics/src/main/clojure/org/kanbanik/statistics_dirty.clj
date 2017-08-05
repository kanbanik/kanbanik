(ns org.kanbanik.statistics-dirty
  (:use org.kanbanik.statistics)
  (:require [clojure.string :as str])
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import [com.mongodb MongoOptions ServerAddress])

  (:gen-class
    :name org.kanbanik.statistics
    :methods [#^{:static true} [execute [java.util.Map] java.lang.String]]))

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

(defn -execute
  [descriptor]

  (let [^MongoOptions opts (mg/mongo-options {:threads-allowed-to-block-for-connection-multiplier 300})
      ^ServerAddress sa  (mg/server-address "127.0.0.1" 27017)
      conn               (mg/connect sa opts)
      db   (mg/get-db conn "kanbanikdb")
      res (mc/find-maps db "events")
        ]   
    
    (apply str (run-analisis 
     (keywordify (into {} descriptor))
     nil
     res    
     ))

;(str :result-descriptors (keywordify my-map))

))

;:function :pass
