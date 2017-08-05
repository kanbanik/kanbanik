(ns org.kanbanik.statistics)

(defn first-timestamp [stream]
  "Takes a list of task related events and returns the timestamp - 1 for the first, if the
  timeframe contains at least one element, otherwise -1"
  (if (and
       (> (count stream) 0 )
       (contains? (first stream) :timestamp))
    (- (:timestamp (first stream)) 1)
    -1
  )
)

; a nasty hack just for playing around - will be removed
(ns-unmap *ns* 'reduce-function)

(defmulti reduce-function (fn [chunk-with-function] (:function chunk-with-function)))
 (defmethod reduce-function :merge
  [chunk-with-function]
  (apply merge (:chunk chunk-with-function)))
 (defmethod reduce-function :last
  [chunk-with-function]
  (last (:chunk chunk-with-function)))

  (defn reduce-tasks [specific-function grouped base-timestamp]
    "Takes a list of tasks grouped by timestamp and the first timestamp
  which is used as a base.
  From each chunk returns only the last task enriched by the :timestamp
  attribute which contains the time difference between the base timestamp and
  the last timestamp."
    (defn reduce-chunk [specific-function grouped-chunks base-timestamp]
                                        ; {1 [{:timestamp 10, :id 1} {:timestamp 20, :id 1}], 2 [{:timestamp 30, :id 2}]}
      
      (map (fn [grouped-chunk] 
             (let [reduced-chunk (reduce-function {:function specific-function :chunk (val grouped-chunk)})]
               (assoc
                   reduced-chunk
                 :timestamp
                 (- (:timestamp reduced-chunk) base-timestamp)
                 ))
             )
           grouped-chunks
           )
      )

    (let [vals (map (fn [[k v]] v) grouped)] ; ignore the timestamps
      
      (map (fn [timeframe-chunk] 
             (reduce-chunk specific-function (group-by #(:id %) timeframe-chunk) base-timestamp))
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
                                (max timeframe 1)))
                    ) 
                  stream)
        )
      )
    )

(defn apply-filter [filter-conditions tasks]
"
Takes an example of the task which should be matched and 
returns the list of tasks which are matched by it.

Currently supports only the workflowitem-id.
"
  (filter 
   (fn [task] (and 
               (:workflowitem-id filter-conditions)
               (= (:workflowitem-id filter-conditions) (:workflowitem-id task))))
    tasks)
)


"Defines the map of functions which can be used"
(def functions
  {:cnt (fn [tasks] (count tasks))}
)


(defn apply-function [function tasks]
  (if (and (not (nil? function)) (function functions)) 
    ((function functions) tasks)
    nil
  )
)

(defn generate-report [descriptors tasks]
"
The tasks are one timeframe output from the reduce-tasks

Example input (aka descriptor)
(def d [{:function :cnt :filter {:workflowitem-id 10} :children [{:function :cnt :filter {:workflowitem-id 10}}]}])

Example input (aka list of tasks)
(def t [{:workflowitem-id 10} {:workflowitem-id 20}])

Example output
[1 [1]]
"
  (loop [res [] desc descriptors]
    (if (= (count desc) 0)
      res
      (let [filtered-tasks (apply-filter (:filter (first desc)) tasks) function (:function (first desc))]
           (if (:children (first desc))
             (recur (conj res (apply-function function filtered-tasks) (generate-report (:children (first desc)) filtered-tasks)) (rest desc))
             (recur (conj res (apply-function function filtered-tasks)) (rest desc))
             )           
       )
    )
  )
)

(defn run-analisis [descriptor timeframe stream]
  (map #(generate-report (:result-descriptors descriptor) %)
       (reduce-tasks (:reduce-function descriptor)
        (group-by-timeframe stream timeframe) 
        (first-timestamp stream)))
)
