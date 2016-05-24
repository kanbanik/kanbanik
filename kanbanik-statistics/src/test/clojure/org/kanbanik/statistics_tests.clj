(ns org.kanbanik.statistics-tests
  (:use clojure.test
        org.kanbanik.statistics))

(testing "Testing the statistics"
  (testing "my first test"
    (is (= 2 (my-first-function 1)))
    )

  (testing "strange function name"
    (is (= 12 (f->t)))
    )

  (testing "load something from mongo"
    (is (not-empty (load-something-from-mongo)))
    )
  )
