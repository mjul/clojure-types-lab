(ns clojure-types-lab.typed.core-test
  (:require
   [clojure-types-lab.typed.core :as sut]
   [typed.clojure :as t] ; the typed type-checker
   [clojure.test :refer [deftest is are testing]]))


(deftest welcome-string-test
  (testing "Says welcome"
    (let [actual (sut/welcome-string "foo")]
      (is (= "Welcome, foo" actual)))))


(deftest party-test
  (testing "Constructor"
    (let [actual (sut/party 1 "Emptor")]
      (is (= {:id 1 :name "Emptor"} actual)))))


(deftest party-id?-test
  (testing "Predicate"
    (are [expected x] (= expected (sut/party-id? x))
      false :a
      false nil
      false 1M
      false (short 1)
      false (byte 1)
      true 1
      true (long 1))))

(deftest sum-test
  (are [expected xs] (= expected (apply sut/sum xs))
    0 []
    1 [1]
    3 [1 2]
    6 [1 2 3]
    10 [1 2 3 4]
    15 [1 2 3 4 5]))


(deftest type-check-test
  (testing "Type checks pass"
    (is (t/check-ns-clj 'clojure-types-lab.typed))))