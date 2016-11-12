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
    (def one-somthing [[1 [{:timestamp 10 :id 1}]]])
    (let [
          id1t10 {:timestamp 10 :id 1}
          id1t20 {:timestamp 20 :id 1}
          id2t30 {:timestamp 30 :id 2}
         ]
      (is (= 9 (:timestamp (first (reduce-tasks [[1 [id1t10]]] 1)))))
      (is (= 19 (:timestamp (first (reduce-tasks [[1 [id1t10 id1t20]]] 1)))))
      (let [two-in-chunk (reduce-tasks [[1 [id1t10 id1t20 id2t30]]] 1)]
        (is (and
             (= 19 (:timestamp (first two-in-chunk)))
             (= 29 (:timestapm (second two-in-chunk)))
             ))
        )
      )
  )
)
