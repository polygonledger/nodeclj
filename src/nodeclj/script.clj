(ns nodeclj.script
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io]))

;OP_DUP OP_HASH160 <pubKeyHash> OP_EQUALVERIFY OP_CHECKSIG
    
(def state {:stack []})

(defn pops! [stack]
  (let [first (first @stack)]
    (swap! stack pop)
    first))()

(defn push! [stack item]
  (swap! stack conj item))

(defonce stack (atom '()))
(defonce dict (atom {}))
(defonce prim (fn [id f] (swap! dict assoc id f)))

;;ops

(defn dup [] 
  (push! stack (first @stack)))

(defn equal []
  (let [v1 (pops! stack)
        v2 (pops! stack)]
    (= v1 v2)))
  
(defn equalverify []
  (let [v1 (pops! stack)
        v2 (pops! stack)]
    (= v1 v2)))


;OP_CHECKSIG

;EvalScript


;(push! stack 2)
;(println (pops! stack))
