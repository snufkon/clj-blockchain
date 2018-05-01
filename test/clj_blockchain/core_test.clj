(ns clj-blockchain.core-test
  (:require [clj-blockchain.core :as core]
            [clj-blockchain.spec :as spec]
            [clj-blockchain.test-data :as td]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.spec.test.alpha :as stest]
            [clojure.test :refer :all]))

;;; for creating test data -----------------------------------------------------

(defn- gen-transaction
  []
  (gen/generate (s/gen ::spec/transaction)))

(defn- gen-transactions
  []
  (gen/generate (s/gen ::spec/transactions)))

(defn- gen-block
  [index proof previous-hash]
  {:index index
   :timestamp (System/currentTimeMillis)
   :transactions (gen-transactions)
   :proof proof
   :previous-hash previous-hash})

(defn- gen-chain
  []
  (let [length (inc (rand-int 5))
        ;; genesis block
        chain [(gen-block 1 100 "1")]]
    (loop [chain chain]
      (if (>= (count chain) length)
        chain
        (let [last-block (last chain)
              index (inc (:index last-block))
              last-proof (:proof last-block)
              proof (core/proof-of-work last-proof)
              previous-hash (core/hash last-block)
              block (gen-block index proof previous-hash)]
          (recur (conj chain block)))))))


;;; tests ----------------------------------------------------------------------

(use-fixtures :once (fn [f]
                      (stest/instrument)
                      (stest/instrument `core/fetch-chain {:stub #{`core/fetch-chain}})
                      (f)
                      (stest/unstrument)))

(use-fixtures :each (fn [f]
                      (core/init)
                      (f)))


(deftest init-test
  (testing "genesis block existence"
    (is (core/get-chain))
    (is (core/last-block))
    (is (= 1 (core/count-chain)))))


(deftest create-transaction-test
  (let [index1 (core/create-transaction td/transaction1)
        index2 (core/create-transaction td/transaction2)]
    
    (testing "increase of current transactions"
      (is (= 2 (core/count-transactions))))

    (testing "return index of block which hold this transaction"
      (is (= 2 index1))
      (is (= 2 index2)))))


(deftest create-block-test
  (testing "increase of block chain"
    (let [current (core/count-chain)]
      (core/create-block 582984774020)
      (is (= (inc current) (core/count-chain)))))

  (testing "reset current transactions"
    (core/create-transaction td/transaction1)
    (is (= 1 (core/count-transactions)))
    (core/create-block 282985774021)
    (is (= 0 (core/count-transactions)))))


(deftest register-node-test
  (testing "increase of nodes"
    (is (= 0 (core/count-nodes)))
    (core/register-node "http://192.168.0.5:5000")
    (is (= 1 (core/count-nodes)))
    (core/register-node "http://192.168.0.6:5000")
    (is (= 2 (core/count-nodes)))
    (core/register-node "http://192.168.0.6:6000")
    (is (= 3 (core/count-nodes)))
    (core/register-node "http://192.168.0.5:5000")
    (is (= 3 (core/count-nodes))))
  
  (is (= ["192.168.0.5:5000"
          "192.168.0.6:5000"
          "192.168.0.6:6000"]) (core/get-nodes)))


(deftest valid-chain-test
  (is (core/valid-chain? td/chain))

  (testing "manipulated chain"
    (let [chain1 (assoc-in td/chain [1 :transactions 0 :amount] 1000000)
          chain2 (assoc-in td/chain [2 :transactions 1 :recipient] "wjgbb7ga3szg")
          chain3 (assoc-in td/chain [3 :transactions 0 :amount] 2000000)]
      (is (not (core/valid-chain? chain1)))
      (is (not (core/valid-chain? chain1)))
      (is (not (core/valid-chain? chain1))))))


(deftest resolve-conflicts-test
  (core/register-node "http://192.168.0.5:5000")
  (core/register-node "http://192.168.0.6:5000")
  (let [current (core/get-chain)]
    (if (core/resolve-conflicts)
      (is (not= current (core/get-chain)))
      (is (= current (core/get-chain))))))
