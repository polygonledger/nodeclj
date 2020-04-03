(ns nodeclj.stackop
  (:require [nodeclj.txl :as txl]
             [clojure.string :as str])
  (:refer-clojure :exclude [pop!]))



(defn show_ops []
  (doseq [[fn-symbol fn-var] (ns-interns 'nodeclj.txl)]
    (cond (str/starts-with? fn-symbol "OP_")
      (do (newline)
          (println "# " fn-symbol " #")
          (println (:doc (meta fn-var)))))))

;(count (ns-interns `clojure.core))
;(take 20 (ns-interns `clojure.core))

(in-ns 'nodeclj.stackop)

(show_ops)

(ns-interns `nodeclj.txl)

; (defn forth-eval [ops stack token]
; (cond (contains? @dict token) ((@dict token))
;       (number? token) (push! stack token)
;       :default (println token "??")))



(def stack (atom '()))

;{:from ABC :to DEF}
;LOCK => ;OP_DUP OP_HASH160 <pubKeyHash> OP_EQUALVERIFY OP_CHECKSIG
;UNLOCK => <sig> <pubKey> | OP_DUP ...

(txl/push! stack 3)
(txl/pop! stack)

(txl/printstack stack)

(txl/OP_DUP stack)
(txl/OP_ADD stack)
(txl/OP_EQUAL stack)

(= (txl/pop! stack) (txl/pop! stack))

(txl/pop! stack)

; (reset! stack (atom '()))
; ;(repl e)
; ;
; (OP_ADD stack)
; 


;2 KeyA KeyB KeyC 3 OP_CHECKMULTISIG
