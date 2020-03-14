(ns nodeclj.ntcl
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]])
  (:require [clojure.string :as str])
  (:import (java.net Socket)
           (java.io PrintWriter InputStreamReader BufferedReader)))


(declare conn-handler)

;;; network ;;;

(defn write [conn msg]
  (println "> write " msg)
  (doto (:out @conn)
    (.println (str msg "\r"))
    (.flush)))

(defn put-write [req conn]
  (println ">> " req)
  (let [reply (str "PONG " (re-find #":.*" req))]
    (println "reply " reply)
    (>!! (:write_queue @conn) reply)))

(defn handle-pong [req conn]
  (println "pong >> " req))
  
(defn handle-req [msg conn]
  (println "handle req " msg)
  )

(defn read-msg-handler [msg conn]
  (println "handle " msg)
  (let [msgv (str/split msg #"#")
        msgtype (get msgv 0)
        cmd (get msgv 1)
        ]
    (println msgv)
    (case msgtype
      "REQ" (handle-req msg conn)
      )))
  
;   (cond
;     ;(re-find #"^ERROR :Closing Link:" msg)
;     ;(dosync (alter conn merge {:exit true}))
;     (re-find #"^PING" msg)
;     (put-write msg conn)
;     (re-find #"^PONG" msg)
;     (handle-pong msg conn)))
    ;(write conn (str "PONG " (re-find #":.*" msg)))))


(defn read-processor [conn]
  (while (nil? (:exit @conn))
    (let [readmsg (<!! (:read_queue @conn))]   
    (println "[read-processor] " readmsg)
    (read-msg-handler readmsg conn))))

(defn read-queue [conn]
  (while (nil? (:exit @conn))
    (println "read loop")
    (let [msg (.readLine (:in @conn))]
      (println "[readhandler] " msg)
      (>!! (:read_queue @conn) msg))))

(defn write-queue [conn]
  (while (nil? (:exit @conn))
    (println "[write-queue] " (<!! (:write_queue @conn)))))


(defn connect [server]
  (let [socket (Socket. (:name server) (:port server))
        in (BufferedReader. (InputStreamReader. (.getInputStream socket)))
        out (PrintWriter. (.getOutputStream socket))
        conn (ref {:in in :out out :read_queue (chan) :write_queue (chan)})
        ]
    ;(read-processor conn)
    (doto (Thread. #(read-queue conn)) (.start))
    (doto (Thread. #(read-processor conn)) (.start))
    (doto (Thread. #(write-queue conn)) (.start))
    conn))
