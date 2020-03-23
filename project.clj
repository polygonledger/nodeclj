(defproject nodeclj "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "1.0.567"]]
  :main ^:skip-aot nodeclj.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
