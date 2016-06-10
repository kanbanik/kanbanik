(ns org.kanbanik.statistics-tests
  (:use clojure.test
        org.kanbanik.statistics))

(testing "Grouping stream of tasks by timeframes"
  (testing "Empty input returns empty output"
    (is (= [] (group-streams-by-time [], 10)))
    )

  (testing "One element input returns one element output"
    (is (= {1.0 [{:timestamp 1464509670 :name "name 1"}] }
             (group-by-timeframe [{:timestamp 1464509670 :name "name 1"}] 10)))  
    )

  (testing "Two element input in the same chunk"
    (is (= {1.0 [{:timestamp 1464509670 :name "name 1"}
               {:timestamp 1464509671 :name "name 2"}]}
           (group-by-timeframe [{:timestamp 1464509670 :name "name 1"}
                                   {:timestamp 1464509671 :name "name 2"}] 10)))
    )
  
    (testing "Two element input in different chunks"
      (is (= {1.0 [{:timestamp 1464509670 :name "name 1"}]
              2.0 [{:timestamp 1464509681 :name "name 2"}]}              (group-by-timeframe [{:timestamp 1464509670 :name "name 1"}
                                     {:timestamp 1464509681 :name "name 2"}] 10)))
      ) 
  )
