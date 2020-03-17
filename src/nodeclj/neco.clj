(ns nodeclj.neco
(:require [clojure.core.async
           :as a
           :refer [>! <! >!! <!! go chan buffer close! thread
                   alts! alts!! timeout]])
(:require [clojure.string :as str])
(:require [clojure.java.io :as io])
(:import (java.net Socket ServerSocket)
         (java.io PrintWriter InputStreamReader BufferedReader)))

(defn processor [conn]
  (let [xout (<! (:REQin @conn))]
      (println "xout " xout)))


(defn process-msg [conn msg]
  (println "process " msg)
  
  (case (:type msg)
    :REQ (go (>! (:REQin @conn) msg))))


; (defn read-processor [conn]
;   (while true; (nil? (:exit @conn))
;     (let [readmsg (<!! (:REQin @conn))]   
;       (println "[read-processor] " readmsg)
;       (let [reply (read-msg-handler readmsg conn)]
;         (println "reply" reply)
;          (>!! (:write_queue @conn) reply)
;         ))))

(defn new-conn []
  (ref {:REQin (chan) :REQ_out (chan) :REPin (chan) :REPout (chan)}))

(let [conn (new-conn)]
  (println conn)
  )