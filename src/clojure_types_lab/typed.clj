(ns clojure-types-lab.typed
  {:lang :core.typed}
  (:require [clojure.core.typed :as t]))

;;(t/ann ^:no-check clojure.core.typed/rclass-pred [t/Any t/Any :-> t/Any])
(t/ann clojure.core/int? [t/Any :-> t/Bool])

(t/ann welcome-string [t/Str :-> t/Str])
(defn welcome-string [a-name]
  (str "Welcome, " a-name))

(t/ann party-id? [t/Any :-> t/Bool])
(defn party-id? [x]
  (and (number? x)
       (pos? ^int x)))
 
(defn party [id name]
  {:pre [(party-id? id)]}
  {:id id :name name})



(defn numerical-tests
  [x]

  #_(juxt (partial identical? 1)
        zero?
        pos?
        neg?
        even?
        odd?
        number?
        ;;ratio?
        ;;rational?
        integer?
        int?
        ;;pos-int?
        ;;nat-int?
        decimal?
        ;;float?
        ;;double?
        ) x)


(comment 
  
  (type(int 32))
  (t/install )
  (t/check-ns)
  (t/cf '(clojure.core.typed/rclass-pred :a :b))
  (t/cf '(juxt :a :b {:a 1 :b 2 :c 3}))
  
  )