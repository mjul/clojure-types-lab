(ns clojure-types-lab.deftypes
  "deftype is like defrecord but for primitive types with minimal semantics (not even an equality relation).
    Perhaps it is useful to build simple value types."
  (:require [clojure.data.json :as json]))

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

;; this type is not JSON serialisable, so we must help a bit
(defn write-json-FooValue [object output options]
  (json/write {:type (str (namespace `->FooValue) ".FooValue") :value (.value object)} output))

(extend FooValue json/JSONWriter {:-write write-json-FooValue})

;; Unfortunately there is no way to read it back, we have to manually map it after deserialising this

