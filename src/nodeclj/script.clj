(ns nodeclj.script
  (:require [clojure.string :as str])
  (:require [clojure.java.io :as io]))

;OP_DUP OP_HASH160 <pubKeyHash> OP_EQUALVERIFY OP_CHECKSIG
    
(def state {:stack []})

(defn pops! [stack]
  (let [first (first @stack)]
    (swap! stack pop)
    first))

(defn push! [stack item]
  (swap! stack conj item))

;;ops

(defn dup [stack] 
  (push! stack (first @stack)))

(defn op_equal [stack]
  (let [v1 (pops! stack)
        v2 (pops! stack)]
    (= v1 v2)))

(defn op_add [stack]
  (let [v1 (pops! stack)
        v2 (pops! stack)]
    (push! stack (+ v1 v2))))
  
(defn equalverify [stack]
  (let [v1 (pops! stack)
        v2 (pops! stack)]
    (= v1 v2)))

(defn evalop [op stack]
  (case op
    :EQUAL (op_equal stack)
    :ADD (op_add stack)))

(defn pushresult [stack result]
  (println "=> " result)
  (push! @stack result))

(defn evalx [op stack]
  (println "eval " op " " @stack)
  (let [result (evalop op stack)
        ;lastop op
        n (count @stack)]
    (println "result " result)
    (cond (= n 0)
          (pushresult stack result))))

(defn evalScript [stack tx]
  (let [pc (atom 0)]
    (doseq [token (seq tx)]
      (swap! pc + 1)
      (println @pc " "  token " " @stack " " (count @stack))
      (if (keyword? token)
        (evalx token stack)
        (push! stack token)))))

; ;(def sometx [2 3 :ADD 5 :EQUAL])
; (def sometx [2 2 :EQUAL])

; ;true => 2 3 OP_ADD 5 OP_EQUAL

; ;;;;;; REPL ;;;;;;
; (in-ns 'nodeclj.script)

; (defonce stack (atom '()))

; (reset! stack (atom '()))

 
; (push! @stack 2)
; (push! @stack true)
; (op_equal @stack)
; (count @stack)

; (def s [2 2 :EQUAL])
; (evalScript @stack s)
; (println @@stack)

; (doseq [x (seq s)]
;   (println "[" x "]"))


; (push! stack 2)
; (println (pops! stack))


; (defonce dict (atom {}))
; (defonce prim (fn [id f] (swap! dict assoc id f)))
; (evalScript stack sometx)
; (println @stack)

; ; ;;;;
; ; OP_CHECKSIG
; ; EvalScript
