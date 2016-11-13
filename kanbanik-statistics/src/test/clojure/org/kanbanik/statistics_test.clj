(ns org.kanbanik.statistics-test
  (:use clojure.test)
  (:use org.kanbanik.statistics))

(testing "Statistics"
  (testing "first-timestamp"
    (is (= -1 (first-timestamp [])))
    (is (= -1 (first-timestamp [{:does-not-have-timestamp 10}])))
    (is (= 9 (first-timestamp [{:timestamp 10}])))
    (is (= 9 (first-timestamp [{:timestamp 10} {:timestamp 12}])))
    )

  (testing "reduce-tasks"
     (let [
          id1t10 {:timestamp 10 :id 1}
          id1t20 {:timestamp 20 :id 1}
          id2t30 {:timestamp 30 :id 2}
         ]
; one chunk, one field in it
      (is (= 9 (:timestamp (first (first (reduce-tasks [[1 [id1t10]]] 1))))))
; one chunk, two field with same id in it
      (is (= 19 (:timestamp (first (first (reduce-tasks [[1 [id1t10 id1t20]]] 1))))))
; one chunk, 3 fields with 2 differend ids in it
      (let [two-in-chunk (reduce-tasks [[1 [id1t10 id1t20 id2t30]]] 1)]
        (is (and
             (= 19 (:timestamp (first (first two-in-chunk))))
             (= 29 (:timestamp (second (first two-in-chunk))))
             ))
        )
; two chunks and 2 differnet ids in both
      (let [two-chunks (reduce-tasks [[1 [id1t10 id1t20 id2t30]] [2 [id2t30 id1t20 id1t10]]] 1)]
        (is (and
; test first chunk
             (= 19 (:timestamp (first (first two-chunks))))
             (= 29 (:timestamp (second (first two-chunks))))
; test second chunk
             (= 29 (:timestamp (first (second two-chunks))))
             (= 9 (:timestamp (second (second two-chunks))))
             ))
        )
      )
  )

  (testing "group-by-timeframe"
    (let [
          id1t10 {:timestamp 10 :id 1}
          id1t20 {:timestamp 20 :id 1}
          id2t30 {:timestamp 30 :id 2}
         ]
      (is (= 0 (count (group-by-timeframe [] 1))))
      (is (= 3 (count (group-by-timeframe [id1t10 id1t20 id2t30] 10))))
      (is (= 3 (count (group-by-timeframe [id1t10 id1t20 id2t30] 1))))
      (is (= 3 (count (group-by-timeframe [id1t10 id1t20 id2t30] 0))))
      )
    )
)
