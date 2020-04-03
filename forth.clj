(ns forth
  (:refer-clojure :exclude [pop!]))

(declare forth-eval)

(defn pop! [stack]
  (let [first (first @stack)]
    (swap! stack pop)
    first))

(defn push! [stack item]
  (swap! stack conj item))

(defn next-token [stream]
  (if (. stream hasNextBigInteger)
    (. stream nextBigInteger)
    (. stream next)))

(defn init-env []
  (let [stream (java.util.Scanner. System/in)
        stack (atom '())
        dict (atom {})
        prim (fn [id f] (swap! dict assoc id f))]
    (prim ".s" #(do (println "---")
                    (doseq [s @stack] (println s))
                    (println "---")))
    (prim "cr" #(println))
    (prim "+" #(push! stack (+ (pop! stack) (pop! stack))))
    (prim "*" #(push! stack (* (pop! stack) (pop! stack))))
    (prim "/" #(let [a (pop! stack)
                     b (pop! stack)]
                 (push! stack (/ b a))))
    (prim "-" #(let [a (pop! stack)
                     b (pop! stack)]
                 (push! stack (- b a))))
    (prim "dup" #(push! stack (first @stack)))
    (prim "." #(println (pop! stack)))
    (prim ":" #(let [name (next-token stream)
                     block (loop [b [] n (next-token stream)]
                             (if (= n ";")
                               b
                               (recur (conj b n) (next-token stream))))]
                 (prim name (fn [] (doseq [w block]
                                    (forth-eval dict stack w))))))
    [dict stack stream]))

(defn forth-eval [dict stack token]
  (cond (contains? @dict token) ((@dict token))
        (number? token) (push! stack token)
        :default (println token "??")))

(defn ev [env]
  (let [[dict stack stream] env]
    )
  )

(defn repl [env]
  (let [[dict stack stream] env
        token (next-token stream)]
    (when (not= token "bye")
      (forth-eval dict stack token)
      (repl env))))

(in-ns 'forth)

(def e (init-env))

;(repl e)