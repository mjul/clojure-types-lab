(ns clojure-types-lab.typed-test
  (:require
   [clojure-types-lab.typed :as sut]
   [clojure.core.typed :as t] ; the typed type-checker
   [clojure.test :refer [deftest is testing]]))


(deftest welcome-string-test
  (testing "Says welcome"
    (let [actual (sut/welcome-string "foo")]
      (is (= "Welcome, foo" actual)))))


(deftest party-test
  (testing "Constructor"
    (let [actual (sut/party 1 "Emptor")]
      (is (= {:id 1 :name "Emptor"} actual)))))


(deftest type-check-test
  (testing "Type checks pass"
    (is (t/check-ns 'clojure-types-lab.typed))))