(ns nodeclj.core  
  (:require [nodeclj.ntcl :as ntcl])
  (:import (java.net Socket)
           (java.io PrintWriter InputStreamReader BufferedReader)))

(def polygon {:name "localhost" :port 8888})

(defn set-interval [ms callback args]
  (future (while true (do (Thread/sleep ms) (callback args)))))

(defn make-job [f]
  (set-interval (f) 1000))
  
(defn make-ping [conn]
  (ntcl/write conn (str "REQ#PING#|")))

(defn -main
  [& args]
  (let [c (ntcl/connect polygon)]
    (println c)
    (make-ping c)
    ;(make-job (make-ping c))
    (set-interval 1000 make-ping c)
    ))
