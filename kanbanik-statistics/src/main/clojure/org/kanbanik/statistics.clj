(ns org.kanbanik.statistics)

(defn group-by-timeframe [stream timeframe]
  "Gets a vector of task related events sorted by time and groups them according to given time frame.
  Timeframe: in seconds
  Input: [event1, event2, event3, event4....]
  Output [[event1, event2...], [event3, event4...]...]"
  (if (= (count stream) 0)
    []
; the base time starts one millisecond before the first item from the stream
    (let [base-timestamp (- (:timestamp (first stream)) 1)]
      (group-by (fn [item]
                  (Math/ceil (/ 
                                (- (:timestamp item) base-timestamp) 
                                timeframe))
                  ) 
                stream)
    )
  )
)
