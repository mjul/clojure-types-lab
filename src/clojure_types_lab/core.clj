(ns clojure-types-lab.core)

;; deftype defines a type without toString and hashCode/equals
;; so we add that
;; this gives us value type semantics
(deftype FooValue [^int value]
  Object
  (toString [_] (str value))
  (^boolean equals [_ other]
    (and (instance? FooValue other)
         (= value (.value other))))
  (hashCode [_] value))


