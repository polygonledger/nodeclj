(ns nodeclj.ntcl
  (:require [clojure.core.async
             :as a
             :refer [>! <! >!! <!! go chan buffer close! thread
                     alts! alts!! timeout]])
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io])
  (:import (java.net Socket ServerSocket)
           (java.io PrintWriter InputStreamReader BufferedReader)))

(declare conn-handler)

;;; network ;;;

(defn write [conn msg]
  (println "> write " msg)
  (doto (:ntwkout @conn)
    (.println (str msg "\r"))
    (.flush)))

(defn send-msg
  "Send the message"
  [conn msg]
  (println "send-msg " msg (type msg))
  (.write (:ntwkout @conn) msg)
  (.flush (:ntwkout @conn)))

  
(defn handle-req [cmd data]
  (println "handle req " cmd data)
  (case cmd
    :PING  (str {:type :REP :CMD :PONG})  
  ))

(defn read-msg-handlertel 
  "telnet like message format"
  [msg conn]
  (println "handle " msg)
  (let [msgv (str/split msg #"#")
        msgtype (get msgv 0)
        cmd (get msgv 1)
        data (get msgv 2)]
    (println msgv)
    (case msgtype
      "REQ" (handle-req cmd data)
      "error"
      )))


(defn read-msg-handler [msg conn]
  (println "handle " msg)
  (let [msgv (read-string msg)
        msgtype (:type msgv)
        cmd (:cmd msgv)
        data (:data msgv)]
    (println msgv)
    (case msgtype
      :REQ (handle-req cmd data)
      "error")))

  
(defn read-processor [conn]
  (while true; (nil? (:exit @conn))
    (let [readmsg (<!! (:read_queue @conn))]   
      (println "[read-processor] " readmsg)
      (let [reply (read-msg-handler readmsg conn)]
        (println "reply" reply)
         (>!! (:write_queue @conn) reply)
        ))))

(defn read-queue [conn]
  (while true ;(nil? (:exit @conn))
    (println "read loop")
    (let [msg (.readLine (:ntwkin @conn))]
      (println "[readhandler] " msg)
      (>!! (:read_queue @conn) msg))))

(defn write-queue [conn]
  (while (nil? (:exit @conn))
    (let [msg (<!! (:write_queue @conn))]
      (println "[write-queue] " msg)
      (send-msg conn msg))))

(defn wrap-socket [socket]
  (let [in (BufferedReader. (InputStreamReader. (.getInputStream socket)))
        out (PrintWriter. (.getOutputStream socket))
        conn (ref {:ntwkin in :ntwkout out :read_queue (chan) :write_queue (chan)})]
    conn))

(defn connect [server]
  "connect to outbound"
  (let [socket (Socket. (:name server) (:port server))        
        conn (wrap-socket socket)
        ]
    ;(read-processor conn)
    (doto (Thread. #(read-queue conn)) (.start))
    (doto (Thread. #(read-processor conn)) (.start))
    (doto (Thread. #(write-queue conn)) (.start))
    conn))


(defn serve1 [port]
  "inbound connections"
  (println "serve " port)  
  ;(with-open [srv-sock (ServerSocket. port)
  (let [srv-sock (ServerSocket. port)
        socket (.accept srv-sock)
        conn (wrap-socket socket)
        ]
    (println "connected " conn)
    (doto (Thread. #(read-queue conn)) (.start))
    (doto (Thread. #(read-processor conn)) (.start))
    (doto (Thread. #(write-queue conn)) (.start))    
    ))

(defn serve [nodeport]
  (println "serve " nodeport)
  (let [running (atom true)]
    (future
      ;(with-open [server-sock (ServerSocket. nodeport)]
      (let [server-sock (ServerSocket. nodeport)]
      (while @running
        ;(with-open [socket (.accept server-sock)]
        (let [socket (.accept server-sock)
              conn (wrap-socket socket)]
            (println "connected " conn)
            (doto (Thread. #(read-queue conn)) (.start))
            (doto (Thread. #(read-processor conn)) (.start))
            (doto (Thread. #(write-queue conn)) (.start))))))
    running))