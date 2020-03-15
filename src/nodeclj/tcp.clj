(ns nodeclj.tcp
(:require [clojure.core.async
           :as a
           :refer [>! <! >!! <!! go chan buffer close! thread
                   alts! alts!! timeout]])
(:require [clojure.string :as str])
(:require [clojure.java.io :as io])
(:import (java.net Socket ServerSocket)
         (java.io PrintWriter InputStreamReader BufferedReader)))



(defn ntwk-read
  "Read a line of text from the given socket"
  [conn]
  (.readLine (:ntwkin @conn)))
  ;(.readLine (io/reader socket)))

(defn ntwk-write
  "Send the given string message out over the given socket"
  [conn msg]
  (let [writer (:ntwkout @conn)]
      (.write writer msg)
      (.flush writer)))

(defn serve [port handler]
  (with-open [server-sock (ServerSocket. port)
              sock (.accept server-sock)]
    (let [msg-in (ntwk-read sock)
          msg-out (handler msg-in)]
      (ntwk-write sock msg-out))))

(defn handle-req [conn handler]
  (let [msg-in (ntwk-read conn)
        msg-out (handler msg-in)]
    (ntwk-write conn msg-out)))

(defn handle-req-loop [conn handler]
  (println "handle-req-loop")
  (while true
    (println "loop")
    (handle-req conn handler)))

(defn wrap-socket [socket]
  (let [in (BufferedReader. (InputStreamReader. (.getInputStream socket)))
        out (PrintWriter. (.getOutputStream socket))
        conn (ref {:ntwkin in :ntwkout out :read_queue (chan) :write_queue (chan)})]
    conn))

(defn serve [port]
  (println "serve-persistent " port)
  (let [handler #(.toUpperCase %)
        running (atom true)]    
      (let [server-sock (ServerSocket. port)]
        (println "server " server-sock)
        (while @running
          (let [sock (.accept server-sock)
                conn (wrap-socket sock)]
            (println "open sock " sock)
            (doto (Thread. #(handle-req-loop conn handler)) (.start))
            (println "next")
            )))
    running))

