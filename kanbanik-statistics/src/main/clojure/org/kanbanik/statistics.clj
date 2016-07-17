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

"Defines the map of functions which can be used"
(def functions 
  {:cnt (fn [tasks] (count tasks))}
)

(defn apply-filter [filter-conditions tasks]
"
Takes an example of the task which should be matched and 
returns the list of tasks which are matched by it.

Currently supports only the workflowitem-id.
"
(filter (fn [task]
  (= (:workflowitem-id filter-conditions) (:workflowitem-id task)))
  tasks)
)

(defn apply-function [function tasks]
  ((function functions) tasks)
)

"maybe delete because the above two should do the trick"
(defn calculate-one [function filter tasks]
1
)


(defn generate-report [descriptor tasks]
"
The tasks are one timeframe output from the reduce-tasks

Example input (aka descriptor)
(def x [{:function :function1 :filter :filter1 :children [{:function :cf1 :filter :cf1}]}])

Example output
[1 [103]]
"
  (loop [res [] desc descriptor]
    (if (= (count desc) 0)
      res
      (if (:children (first desc))
        (recur (conj res [(calculate-one (:function (first desc)) (:filter (first desc)) tasks) (generate-report (:children (first desc)) tasks)]) (rest desc))
        (recur (conj res 103) (rest desc))
      )
    )
  )

)

