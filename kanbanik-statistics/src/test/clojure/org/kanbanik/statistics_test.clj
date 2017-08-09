(ns org.kanbanik.statistics-test
  (:use clojure.test)
  (:use org.kanbanik.statistics))

; hack needed to make sure the reduce-function will not be mapped multiple times
(ns-unmap *ns* 'reduce-function)

(testing "Statistics"
  (testing "first-timestamp"
    (is (= -1 (first-timestamp [])))
    (is (= -1 (first-timestamp [{:does-not-have-timestamp 10}])))
    (is (= 9 (first-timestamp [{:timestamp 10}])))
    (is (= 9 (first-timestamp [{:timestamp 10} {:timestamp 12}])))
    )

  (testing "reduce-tasks"
     (let [
          id1t10 {:timestamp 10 :entityId 1}
          id1t20 {:timestamp 20 :entityId 1}
          id2t30 {:timestamp 30 :entityId 2}
         ]
; one chunk, one field in it
      (is (= 9 (:timestamp (first (first (reduce-tasks :last [[1 [id1t10]]] 1))))))
; one chunk, two field with same id in it
      (is (= 19 (:timestamp (first (first (reduce-tasks :last [[1 [id1t10 id1t20]]] 1))))))
; one chunk, 3 fields with 2 differend ids in it
      (let [two-in-chunk (reduce-tasks :last [[1 [id1t10 id1t20 id2t30]]] 1)]
        (is (and
             (= 19 (:timestamp (first (first two-in-chunk))))
             (= 29 (:timestamp (second (first two-in-chunk))))
             ))
        )

; two chunks and 2 differnet ids in both
      (let [two-chunks (reduce-tasks :last [[1 [id1t10 id1t20 id2t30]] [2 [id2t30 id1t20 id1t10]]] 1)]
        (is (and
; test first chunk
             (= 19 (:timestamp (first (first two-chunks))))
             (= 29 (:timestamp (second (first two-chunks))))
; test second chunk
             (= 29 (:timestamp (second (second two-chunks))))
             (= 9 (:timestamp (first (second two-chunks))))
             ))
        )
      )
  )

  (testing "group-by-timeframe"
    (let [
          id1t10 {:timestamp 10 :entityId 1}
          id1t20 {:timestamp 20 :entityId 1}
          id2t30 {:timestamp 30 :entityId 2}
          id2t80 {:timestamp 80 :entityId 2}
         ]
      (is (= 0 (count (group-by-timeframe [] 1))))
      (is (= 3 (count (group-by-timeframe [id1t10 id1t20 id2t30] 10))))
      (is (= 21 (count (group-by-timeframe [id1t10 id1t20 id2t30] 1))))
      (is (= 21 (count (group-by-timeframe [id1t10 id1t20 id2t30] 0))))
      (is (= 1 (count (group-by-timeframe [id1t10 id1t20 id2t30] 100))))
      (is (= 8 (count (group-by-timeframe [id1t10 id1t20 id2t30 id2t80] 10))))

      (testing "apply-filter"
        (let [
              w1 {:workflowitem-id 1}
              w2 {:workflowitem-id 2}
              w3 {:workflowitem-id 3}
              w4 {:workflowitem-id 4}
              ]
          (is (= 0 (count (apply-filter [] []))))
          (is (= 1 (count (apply-filter w1 [w2 w1]))))
          (is (= 2 (count (apply-filter {} [w2 w1]))))
          (is (= 2 (count (apply-filter {} [{} w1]))))
          )
        ))

    (testing "apply-function" 
      (is (= 0 (apply-function :cnt [])))
      (is (= 1 (apply-function :cnt [:x])))
      (is (= nil (apply-function :not-existing [])))
      (is (= nil (apply-function nil [])))
      ))

  (testing "generate-report"
    (let [
          w1 {:workflowitem-id 1}
          w2 {:workflowitem-id 2}

          r-basic-2 {:function :cnt :filter {:workflowitem-id 2}}
          r-nested-1 {:function :cnt :filter {:workflowitem-id 1} :children [{:function :cnt :filter {:workflowitem-id 1}}]}
          ]
      (is (= [1] (generate-report [r-basic-2] [w1 w2])))
      (is (= [1 [1]] (generate-report [r-nested-1] [w1 w2])))
      (is (= [2 [2] 1] (generate-report [r-nested-1 r-basic-2] [w1 w2 w1])))
      )
    )

  (testing "integrated-test"
    (let [
          id1t10 {:timestamp 10 :entityId 1 :workflowitem-id 2}
          id1t20 {:timestamp 20 :entityId 1 :workflowitem-id 2}
          id2t30 {:timestamp 30 :entityId 2 :workflowitem-id 2}
          r-basic-2 {:function :cnt :filter {:workflowitem-id 2}}
          full-descriptor {:reduce-function :last :result-descriptors [r-basic-2]}]
      (let [full-stream [id1t10 id1t20 id2t30]]
        (is (= [[1] [1] [2]] (run-analisis full-descriptor 10 full-stream)))
        (is (= [[2]] (run-analisis full-descriptor 100 full-stream)))
    )))
)
