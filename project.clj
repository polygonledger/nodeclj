(defproject nodeclj "0.1.0-SNAPSHOT"

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/core.async "1.0.567"]
                 [com.github.bitcoinj/bitcoinj "release-0.15-SNAPSHOT"]
                 [bux "0.2.1"]
                 ]
  :main ^:skip-aot nodeclj.core
  :repositories [["jitpack" "https://jitpack.io"]]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
