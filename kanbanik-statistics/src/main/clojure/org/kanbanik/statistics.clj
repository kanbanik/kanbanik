(ns org.kanbanik.statistics)

(defn first-timestamp [stream]
  "Takes a list of task related events and returns the timestamp - 1 for the first, if the
  timeframe contains at least one element, otherwise -1"
  (if (= (count stream) 0)
    -1
    (- (:timestamp (first stream)) 1)
  )
)

(defn reduce-tasks [grouped base-timestamp]
  (defn reduce-chunk [grouped-chunk base-timestamp]
    (let [last-from-chunk (last (val (first grouped-chunk)))]
      (assoc 
          last-from-chunk
        :timestamp
        (- (:timestamp last-from-chunk) base-timestamp)
        )
      )
    )

  (let [vals (map (fn [[k v]] v) grouped)] ; ignore the timestamps
    ; result is something like:
    ; ({10 [{:id 10, :timestamp 20}]} {20 [{:id 20, :timestamp 30}]}
    (map (fn [timeframe-chunk] (reduce-chunk (group-by #(:id %) timeframe-chunk) base-timestamp))
         vals)
  )
)

(defn group-by-timeframe [stream timeframe]
  "Gets a vector of task related events sorted by time and groups them according to given time frame.
  Timeframe: in seconds
  Input: [event1 event2 event3 event4....]
  Output {timeframe1 [event1 event2...] timeframe2 [event3 event4...]...]}"
  (if (= (count stream) 0)
    []
; the base time starts one millisecond before the first item from the stream
    (let [base-timestamp (first-timestamp stream)]
      (group-by (fn [item]
                  (Math/ceil (/ 
                                (- (:timestamp item) base-timestamp) 
                                timeframe))
                  ) 
                stream)
    )
  )
)



(defn generate-report [descriptor tasks]
"
The tasks are one timeframe output from the reduce-tasks

Example input (aka descriptor)
[
 {
  :type :composite
  :title \"some title\"
  :children [
   {   
    :type :leaf
    :filter {:worfklowitem-id 12}
    :function :avg-cnt/:avg-time
   } 
  ]
 }
]

Example output
[
 {
  :title \"some title\"
  :children [
    {
      :type :leaf
      :value 10
    }
  ]
 }
]
"
  (loop [res [] desc descriptor]
    (if (= (count desc) 0)
      res
      (if (= (:type (first desc)) :leaf)
        (recur (conj res {:type :leaf :value 103}) (rest desc))
        (recur (conj res {:title (:title (first desc)) :children (generate-report (:children (first desc)) tasks)}) (rest desc))
      )
    )
  )
)

