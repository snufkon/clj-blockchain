(ns clj-blockchain.handler-test
  (:require [clj-blockchain.core :as core]
            [clj-blockchain.handler :as handler]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer :all]
            [ring.mock.request :as mock]))

(use-fixtures :each (fn [f]
                      (handler/init)
                      (f)))

(use-fixtures :once (fn [f]
                      (stest/instrument)
                      (stest/instrument `core/fetch-chain {:stub #{`core/fetch-chain}})
                      (f)
                      (stest/unstrument)))

(deftest test-app
  (testing "/chain"
    (let [response (handler/app (mock/request :get "/chain"))]
      (is (= (:status response) 200))))

  (testing "/mine"
    (let [response (handler/app (mock/request :get "/mine"))]
      (is (= (:status response) 200))))

  (testing "/transactions/new"
    (let [response (handler/app (-> (mock/request :post "/transactions/new")
                                    (mock/json-body {:sender "aaaa"
                                                     :recipient "bbbb"
                                                     :amount 5})))]
      (is (= (:status response) 200))))

  (testing "/nodes/register"
    (let [response (handler/app (-> (mock/request :post "/nodes/register")
                                    (mock/json-body {:nodes ["http://localhost:3001"]})))]
      (is (= (:status response) 200)))

    (let [response (handler/app (-> (mock/request :post "/nodes/register")
                                    (mock/json-body {:nodes ["http://localhost:3001"
                                                             "http://localhost:3002"]})))]
      (is (= (:status response) 200))))

  (testing "/nodes/resolve"
    (let [response (handler/app (mock/request :get "/nodes/resolve"))]
      (is (= (:status response) 200))))

  (testing "not-found route"
    (let [response (handler/app (mock/request :get "/invalid"))]
      (is (= (:status response) 404)))))
