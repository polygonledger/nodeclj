(ns nodeclj.txl
  (:refer-clojure :exclude [pop!]))

(defn pop! 
  "pop stack"
  [stack]  
  (let [first (first @stack)]
    (swap! stack pop)
    first))

(defn push! 
  "push stack"
  [stack item]  
  (swap! stack conj item))

(defn OP_DUP 
  "duplicate stack"
  [stack]
  (push! stack (first @stack)))

(defn OP_ADD 
  "add two top items of the stack"
  [stack]
  (push! stack (+ (pop! stack) (pop! stack))))

(defn OP_EQUAL 
  "two top items are equal"
  [stack]
  (push! stack (= (pop! stack) (pop! stack))))
  

(defn printstack [stack]
  (doseq [;i (count @stack)
          x (seq @stack)]
    (println "[" x "]")))


;(defn init-env []
; (let [stack (atom '())
;       dict (atom {})
;       prim (fn [id f] (swap! dict assoc id f))]
;   (prim :s #(do (println "---")
;                   (doseq [s @stack] (println s))
;                   (println "---")))
;   (prim :cr #(println))
;   (prim :add #(push! stack (+ (pop! stack) (pop! stack))))
;   (prim :mul #(push! stack (* (pop! stack) (pop! stack))))
;   (prim :div #(let [a (pop! stack)
;                    b (pop! stack)]
;                (push! stack (/ b a))))
;   (prim :min #(let [a (pop! stack)
;                    b (pop! stack)]
;                (push! stack (- b a))))
;   (prim :dup #(push! stack (first @stack)))
;   (prim :pop #(println (pop! stack)))
;   {:ops dict :stack stack}))