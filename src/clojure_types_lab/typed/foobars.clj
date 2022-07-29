(ns clojure-types-lab.typed.foobars
  {:lang :core.typed}
  (:require [typed.clojure :as t]))


;; Some annotated functions that we can use from the core namespace.

(t/defalias Foo '{:foo t/Num})
(t/defalias Bar '{:bar t/Str})

(t/ann foo [t/Num :-> Foo])
(defn foo
  [x]
  {:foo x})

(t/ann bar [t/Str :-> Bar])
(defn bar
  [x] 
  {:bar x})

;; Also, an annotated record type
(t/ann-record Baz [baz :- t/Keyword])
;;(t/ann ->Baz [t/Keyword :-> Baz])
(defrecord Baz [baz])
