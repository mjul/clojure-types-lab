(ns clojure-types-lab.typed.holes-test
  (:require
   [clojure-types-lab.typed.holes :as sut]
   [typed.clojure :as t] ; the typed type-checker
   [clojure.test :refer [deftest is are testing]]))


(deftest type-check-test
  (testing "Type checks fail for noisy hole"
    (is (thrown? clojure.lang.ExceptionInfo (t/check-ns-clj 'clojure-types-lab.typed.holes)))))
