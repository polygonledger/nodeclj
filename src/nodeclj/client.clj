(ns nodeclj.client
  (:require [nodeclj.ntcl :as ntcl])
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]])
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io]))

(defonce nodeport 8888)

(def polygon {:name "localhost" :port nodeport})

(defn runclient []
   (let [conn (ntcl/connect polygon)
         req (str {:type :REQ :cmd :PING} "\n")
         ]
     (println conn)
     ;(ntcl/write c (str {:type :REQ :cmd :PING}))
     (>!! (:write_queue @conn) req) 
     (time (Thread/sleep 2000))
     (println "got " (<!! (:read_queue @conn)))
     ;(println (.readLine (:in @c)))
     ))