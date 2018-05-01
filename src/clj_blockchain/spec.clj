(ns clj-blockchain.spec
  (:require [clj-blockchain.core :as core]
            [clojure.spec.alpha :as s]))

;;; transaction
(s/def ::sender string?)
(s/def ::recipient string?)
(s/def ::amount pos-int?)
(s/def ::transaction (s/keys :req-un [::sender
                                      ::recipient
                                      ::amount]))

;;; block
(s/def ::index pos-int?)
(s/def ::timestamp pos-int?)
(s/def ::transactions (s/coll-of ::transaction))
(s/def ::proof nat-int?)
(s/def ::previous-hash string?)
(s/def ::block (s/keys :req-un [::index
                                ::timestamp
                                ::transactions
                                ::proof
                                ::previous-hash]))

(s/def ::address string?)
(s/def ::node string?)
(s/def ::nodes (s/coll-of ::node))
(s/def ::chain (s/coll-of ::block))
(s/def ::status (s/and pos-int? #(>= % 100) #(<= % 500)))
(s/def ::length pos-int?)
(s/def ::body (s/keys :req-un [::chain
                               ::length]))
(s/def ::response (s/keys :req-un [::status
                                   ::body]))

(s/fdef core/hash
  :args (s/cat :block ::block)
  :ret  string?)


(s/fdef core/create-transaction
  :args (s/cat :transaction ::transaction)
  :ret  ::index)


(s/fdef core/create-block
  :args (s/cat :proof         ::proof
               :previous-hash (s/? ::previous-hash))
  :ret  ::block)


(s/fdef core/valid-proof?
  :args (s/cat :last-proof ::proof
               :proof      ::proof)
  :ret  boolean?)


(s/fdef core/proof-of-work
  :args (s/cat :last-proof ::proof)
  :ret  ::proof)


(s/fdef core/register-node
  :args (s/cat :address ::address))


(s/fdef core/valid-chain?
  :args (s/cat :chain ::chain)
  :ret  boolean?)


(s/fdef core/fetch-chain
  :args (s/cat :node ::node)
  :ret  ::response)


(s/fdef core/fetch-longest-chain
  :args (s/cat :neighbours ::nodes
               :new-chain  (s/nilable ::chain)
               :max-length pos-int?)
  :ret (s/nilable ::chain))


(s/fdef core/resolve-conflicts
  :ret boolean?)
