(ns nodeclj.core  
  (:require [nodeclj.ntcl :as ntcl])
  ;(:require [nodeclj.bitcoin :as b])
  ;(:require [nodeclj.tcp :as t])
  (:require [nodeclj.othertcp :as t])
  (:import
   (org.bitcoinj.params MainNetParams)
   (org.bitcoinj.core DumpedPrivateKey)
   (org.bitcoinj.wallet DeterministicSeed)
   (org.bitcoinj.wallet KeyChainGroupStructure)
   (org.bitcoinj.wallet Wallet)
   (org.bitcoinj.script Script)
   (java.net Socket)
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

(defn handler [reader writer]
  (.append writer "Hello World"))

(def server
  (t/tcp-server
   :port    5000
   :handler (t/wrap-io handler)))



(defn -main
  [& args]
  (println "run main")
  (let [p (MainNetParams/get)
        seedCode "yard impulse luxury drive today throw farm pepper survey wreck glass federal"
        passphrase ""
        creationtime 1409478661
        seed (new DeterministicSeed seedCode nil passphrase creationtime)
        p2pkh nil ;Script/ScriptType/P2PKH
        wallet (Wallet/fromSeed p seed)
        ;pk (DumpedPrivateKey/fromBase58 p "xxxx")
        ]
    (println p)
    (println seed wallet)
    ;DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58 (params, args [0]);
    )  

  ;(ntcl/serve nodeport)
  ;;(def a (serve-persistent 8888 #(.toUpperCase %)))
  ;(t/serve 8888)
  ;(b/create-keypair)
  
  )



;(t/start server)

