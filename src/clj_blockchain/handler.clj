(ns clj-blockchain.handler
  (:require [clj-blockchain.core :as core]
            [clojure.string :as cstr]
            [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.json :refer [wrap-json-params wrap-json-response]]
            [ring.util.response :refer [response status]]))

;; Generate a globally unique address for this node
(defonce node-identifire (-> (str (java.util.UUID/randomUUID))
                             (cstr/replace "-" "")))

(defn- chain
  [req]
  (response {:chain (core/get-chain)
             :length (core/count-chain)}))

(defn- mine
  [req]
  (let [;; We run the proof of work algorithm to get the next proof...
        last-block (core/last-block)
        last-proof (:proof last-block)
        proof (core/proof-of-work last-proof)

        ;; We must receive a reward for finding the proof.
        ;; The sender is "0" to signify that this node has mined a new coin.
        transaction {:sender "0"
                     :recipient node-identifire
                     :amount 1}
        _ (core/create-transaction transaction)

        ;; Forge the new Block by adding it to the chain
        block (core/create-block proof)]
    (response {:message "New Block Forged"
               :index (:index block)
               :transactions (:transactions block)
               :proof (:proof block)
               :previous-hash (:previous-hash block)})))

(defn- create-transaction
  [{:keys [params] :as req}]
  ;; Check that the required fields are in the POST'ed data
  (let [{:keys [sender recipient amount]} params]
    (if (some nil? [sender recipient amount])
      (-> (response "Missing values")
          (status 404))
      (let [transaction {:sender sender
                         :recipient recipient
                         :amount (Integer. amount)}
            ;; Create a new Transaction
            index (core/create-transaction transaction)]
        (response {:message (str "Transaction will be added to Block " index)})))))

(defn- register-node
  [{:keys [params] :as req}]
  (if-let [nodes (:nodes params)]
    (do
      (if (string? nodes)
        (core/register-node nodes)
        (doseq [node nodes]
          (core/register-node node)))
      (response {:message "New nodes have been added"
                 :total-nodes (core/get-nodes)}))
    (-> (response "Error: Please supply a valid list of nodes")
        (status 400))))

(defn- consensus
  [req]
  (if-let [replaced (core/resolve-conflicts)]
    (response {:message "Our chain was replaced"
                 :new-chain (core/get-chain)})
    (response {:message "Our chain is authoritative"
                 :new-chain (core/get-chain)})))

(defroutes app-routes
  (GET  "/chain"             req (chain req))
  (GET  "/mine"              req (mine req))
  (POST "/transactions/new"  req (create-transaction req))
  (POST "/nodes/register"    req (register-node req))
  (GET  "/nodes/resolve"     req (consensus req))
  
  (route/not-found "Not Found"))

(def app (-> app-routes
             wrap-json-response
             (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))
             wrap-json-params))

(defn init
  []
  (core/init))
