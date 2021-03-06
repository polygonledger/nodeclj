(ns nodeclj.neco
(:require [clojure.core.async
           :as a
           :refer [>! <! >!! <!! go go-loop chan put! take! buffer close! thread
                   alts! alts!! timeout]])
(:require [clojure.string :as str])
(:require [clojure.java.io :as io])
;(:require [nodeclj :as io])
(:import (java.net Socket ServerSocket)
         (java.io PrintWriter InputStreamReader BufferedReader)))

;;; neco i.e. network control -
;;; channel abstraction over network

(defn append-vec! 
  [v x]
  "append element to vector"
  (reset! v (conj @v x)))

(defn append-vmap! 
  [m v]
  "append inside map"
  (reset! m (assoc @m :log (append-vec! (:log m) v))))

(defn swap-append! 
  [m k v]
  "append to vector inside map"
  (swap! m assoc k (conj (get @m k) v)))


(defn new-nconn []
  (atom {:log [] :write_queue (chan) :read_queue (chan) :REQin (chan) :REQ_out (chan) :REPin (chan) :REPout (chan)}))


(defn addlog [p s]
  ;(println "addlog " p s)
  (swap-append! p :log s))

(defmacro defprocess [name chanm & body]
  "define a running process which operates on map of channels
   add helper functions for a process for logging etc"
  `(let [~'*fn-name* ~(str name)]
     (defn ~name ~chanm
       ;(do 
           ;(println ">> setup " ~'*fn-name*) 
           ;(println ~chanm)
           ;cannot be cast to future?
           ;(addlog ~chanm "test") ; (str "setup " ~'*fn-name*))
           ~@body)))

;more general macro
;(defmacro processor
;go-loop
;body
;recur 


(defprocess write-queue [c]
  (addlog c [*fn-name* (str "setup")])
  ;(println (str "setup " *fn-name*))
  (go-loop []
    (println "[" *fn-name* "]")
    (let [msg (<! (get @c :write_queue))]
      ;(println "[" *fn-name* "] write to net " msg)
      (addlog c [*fn-name* (str " write to net" msg)])
    (recur))))


(defprocess read-process [c]
  "" " read from queue and process " ""
  (println "setup " *fn-name*)
  (addlog c [*fn-name* (str "setup")])
  (go-loop [] ;counter
    (println "[" *fn-name* "] loop")
    (let [msg (<! (get @c :read_queue))
          t (:type msg)]
      ;(addlog c (str "[" *fn-name* "] " msg " " t))
      (addlog c [*fn-name* (str msg " " t)])
      (case t
        :REQ (>! (get @c :REQin) msg))
      (recur))))


(defprocess req-process [c]
  "" " process requests " ""
  (addlog c [*fn-name* (str "setup")])
  (let [inc :REQin
        outc :REPout]
    (go-loop [] ;counter
      (println "[" *fn-name* "] loop")
      (let [req (<! (get @c inc))
            cmd (:CMD req)] ;test malformed
        (println "req " (str inc) ":" req)
        (case cmd
          :PING (>! (get @c outc) (str {:CMD :PONG})))         
        (recur)))))

(defprocess rep-process [c]
  "" " process replies " ""
  (addlog c [*fn-name* (str "setup")])
  (let [outc :REPout]
    (go-loop [] ;counter
      (println "[" *fn-name* "] loop")
      (let [rep (<! (get @c outc))]
        (println (str outc) ":" rep " >> write queue")
        (>! (get @c :write_queue) rep)
       ;put on write queue
        (recur)))))


(defn setup-processor [c]
  ;""" setup a network """
  (addlog c ["general" "setup processors"])
  (write-queue c)
  (read-process c)
  (req-process c)
  (rep-process c))

(defprocess connect [c1 c2]
  """ connect nconns """
  (go-loop []
    (let [msg1 (<! (get @c1 :write_queue))]
      (println "from c1 write to c2")
      (>! (get @c2 :read_queue) msg1)
    (recur))))

(defprocess simnet
  "simulate network. everything on writer will go to reader"
  [c1 c2]
  (connect c1 c2)
  (connect c2 c1))

(defn print-log [c]
  (doseq [[x y] (:log @c)] 
    (println "[" x "] => " y)))

;;;;; REPL

;; (in-ns 'nodeclj.neco)

;; (def c1 (new-nconn))
;; (setup-processor c1)

;; (def c2 (new-nconn))
;; (setup-processor c2)

;; (simnet c1 c2)

;; (def t {:type :REQ :CMD :PING})

;; (put! (:read_queue @c1) t)

;; (print-log @c1)


;; (put! (:write_queue @c1) t)

;; (take! (:read_queue @c2) (fn [x] (println x)))

;; (write-queue c1)


;(go-loop [] 
;  (println ">>> " (<! (:read_queue @c2)))
;(recur))

; (go-loop [] (println ">>"  (<! (:write_queue @c))))

;(go (println (<! (:read_queue @c2))))

;(go-loop []  
;  (println "## " (<! (:read_queue @c2)))
;(recur))


;(go (>! (:read_queue @c2) "test"))

;(go (>! (:read_queue @c2) t))

;(def m (atom {:log [] :b "B"}))
;(swap-append! m :log "tezz")
