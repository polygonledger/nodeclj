(ns nodeclj.tcp
(:require [clojure.core.async
           :as a
           :refer [>! <! >!! <!! go chan buffer close! thread
                   alts! alts!! timeout]])
(:require [clojure.string :as str])
(:require [clojure.java.io :as io])
(:import (java.net Socket ServerSocket)
         (java.io PrintWriter InputStreamReader BufferedReader)))



(defn receive
  "Read a line of textual data from the given socket"
  [socket]
  (.readLine (io/reader socket)))

(defn send
  "Send the given string message out over the given socket"
  [socket msg]
  (let [writer (io/writer socket)]
      (.write writer msg)
      (.flush writer)))

(defn serve [port handler]
  (with-open [server-sock (ServerSocket. port)
              sock (.accept server-sock)]
    (let [msg-in (receive sock)
          msg-out (handler msg-in)]
      (send sock msg-out))))

(defn handle-req [sock handler]
  (let [msg-in (receive sock)
        msg-out (handler msg-in)]
    (send sock msg-out)))

(defn serve-persistent [port handler]
  (println "serve-persistent")
  (let [running (atom true)]    
      (with-open [server-sock (ServerSocket. port)]
        (println "open server " server-sock)
        (while @running
          (with-open [sock (.accept server-sock)]
            (println "open sock " sock)
            (while true
              (println "loop")
              (handle-req sock handler))
            )))
    running))

;(def a (serve-persistent 8888 #(.toUpperCase %)))
