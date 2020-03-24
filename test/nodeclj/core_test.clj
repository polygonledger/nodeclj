(ns nodeclj.core-test
(:require [clojure.core.async
           :as a
           :refer [>! <! >!! <!! go go-loop chan put! take! buffer close! thread
                   alts! alts!! timeout]])
  (:require [clojure.test :refer :all]
            [nodeclj.neco :refer :all]
            [nodeclj.core :refer :all]))



;(let [regmsg {:type :REQ :cmd :BALANCE}
;      ]

(defn test-within
  "Asserts that ch does not close or produce a value within ms. Returns a
  channel from which the value can be taken"
  [ms ch]
  (go (let [t (timeout ms)
            [v ch] (alts! [ch t])]
        (is (not= ch t)
            (str "Test should have finished within " ms "ms."))
        v)))


(deftest simple-async-test
  (let [ch (chan)]
    (go (>! ch "Hello"))
    (<!!
      (test-within 50
        (go (is (= "Hello" (<! ch))))))))

(deftest simple-chan-test
  (let [c1 (setup-processor (new-nconn))]
    (go (>! (:read_queue @c1) "Hello"))
    (<!!
      (test-within 500
        (go (is (= "Hello" (<! (:read_queue @c1)))))))))

;; (deftest contest-test
;;   (testing "connection."
;;     (let [c1 (setup (new-nconn))
;;           t {:type :REQ :CMD :PING}]
;;       (put! (:read_queue @c1) t)
;;     (is (= (take! (:read_queue @c2) 1))))))
