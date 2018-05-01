(ns clj-blockchain.core
  (:refer-clojure :exclude [hash])
  (:require [clj-http.client :as client]
            [clojure.java.io :as io]
            digest))

(defonce chain (atom []))
(defonce current-transactions (atom []))
(defonce nodes (atom #{}))

(defn get-chain
  []
  @chain)

(defn count-chain
  []
  (count @chain))

(defn count-transactions
  []
  (count @current-transactions))

(defn last-block
  []
  (last @chain))

(defn get-nodes
  []
  (vec @nodes))

(defn count-nodes
  []
  (count @nodes))

(defn hash
  "Creates a SHA-256 hash of a Block"
  [block]
  (-> (into (sorted-map) block)
      str
      digest/sha-256))

(defn create-transaction
  "Creates a new transaction to go into the next mined Block"
  [transaction]
  (swap! current-transactions #(conj % transaction))
  (inc (count-chain)))

(defn create-block
  "Create a new Block in the Blockchain" 
  ([proof]
   (create-block proof (hash (last-block))))
  ([proof previous-hash]
   (let [block {:index         (inc (count-chain))
                :timestamp     (System/currentTimeMillis)
                :transactions  @current-transactions
                :proof         proof
                :previous-hash previous-hash}]
     ;; Reset the current list of transactions
     (reset! current-transactions [])

     (swap! chain #(conj % block))
     block)))

(defn valid-proof?
  "Validates the Proof: Does hash(last_proof, proof) contain 4 leading zeroes?"
  [last-proof proof]
  (-> (str last-proof proof)
      digest/sha-256
      (subs 0 4)
      (= "0000")))

(defn proof-of-work
  "Simple Proof of Work algorithm:
     - Find a number p' such that hash(pp') contains leading 4 zeroes, 
       where p is the previous p'
     - p is the previous proof, and p' is the new proof"
  [last-proof]
  (loop [proof 0]
    (if (valid-proof? last-proof proof)
      proof
      (recur (inc proof)))))

(defn register-node
  "Add a new node to the list of nodes"
  [address]
  (let [url (io/as-url address)
        node (str (.getHost url) ":" (.getPort url))]
    (swap! nodes #(conj % node))))

(defn valid-chain?
  "Determine if a given blockchain is valid"
  [chain]
  (loop [last-block (first chain)
         current-index 1]
    (if-let [block (get chain current-index)]
      ;; Check that the hash of the block and the Proof of Work is correct
      (if (and (= (:previous-hash block) (hash last-block))
               (valid-proof? (:proof last-block) (:proof block)))
        (recur block (inc current-index))
        false)
      true)))

(defn fetch-chain
  [node]
  (let [url (str "http://" node "/chain")
        response (client/get url {:as :json})]
    (select-keys response [:status :body])))

(defn fetch-longest-chain
  [neighbours new-chain max-length]
  (if-let [node (first neighbours)]
    (let [response (fetch-chain node)]
      (if (= 200 (:status response))
        (let [length (get-in response [:body :length])
              chain (get-in response [:body :chain])]
          ;; Check if the length is longer and the chain is valid
          (if (and (> length max-length)
                   (valid-chain? chain))
            (recur (rest neighbours) chain length)
            (recur (rest neighbours) new-chain max-length)))
        (recur (rest neighbours) new-chain max-length)))
    new-chain))

(defn resolve-conflicts
  []
  "This is our Consensus Algorithm, it resolves conflicts
   by replacing our chain with the longest one in the network."
  (if-let [new-chain (fetch-longest-chain (get-nodes) nil (count-chain))]
    (do
      ;; Replace our chain if we discovered a new, valid chain longer than ours
      (reset! chain new-chain)
      true)
    false))

(defn init
  []
  (reset! chain [])
  (reset! current-transactions [])
  (reset! nodes #{})

  ;; Create the genesis block
  (let [proof 100
        previsous-hash "1"]
    (create-block proof previsous-hash)))
