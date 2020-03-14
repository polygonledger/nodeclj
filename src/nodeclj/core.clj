(ns nodeclj.core  
  (:require [nodeclj.ntcl :as ntcl])
  (:require [nodeclj.tcp :as t])
  (:import (java.net Socket)
           (java.io PrintWriter InputStreamReader BufferedReader)))

(defonce nodeport 8888)

(def polygon {:name "localhost" :port nodeport})

(defn set-interval [ms callback args]
  (future (while true (do (Thread/sleep ms) (callback args)))))

(defn make-job [f]
  (set-interval (f) 1000))
  
(defn make-ping [conn]
  (ntcl/write conn (str {:type :REQ :cmd :PING})))

; (let [c (ntcl/connect polygon)]
;   (println c)
;   (make-ping c)
;     ;(make-job (make-ping c))
;   (set-interval 1000 make-ping c))

(defn -main
  [& args]
  (println "run main")

  ;(ntcl/serve nodeport)
  ;;(def a (serve-persistent 8888 #(.toUpperCase %)))
  (t/serve-persistent 8888 #(.toUpperCase %))
  )
