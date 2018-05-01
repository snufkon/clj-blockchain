(defproject clj-blockchain "0.1.0-SNAPSHOT"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [digest "1.4.8"]
                 [ring/ring-json "0.4.0"]
                 [clj-http "3.8.0"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler clj-blockchain.handler/app
         :init    clj-blockchain.handler/init}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.2"]
                        [org.clojure/test.check "0.10.0-alpha2"]]}})
