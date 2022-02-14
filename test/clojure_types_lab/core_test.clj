(ns clojure-types-lab.core-test
  (:require [clojure.test :refer :all]
            [clojure-types-lab.core :as sut]))

(deftest FooValue-test
  (testing "FooValue has value semantics"
    (let [a (sut/->FooValue 1)
          b (sut/->FooValue 1)
          otherFooValue (sut/->FooValue 3)
          otherValue "foo"]
      (testing "equal objects have equal hash-code"
        (is (= (.hashCode a) (.hashCode a)))
        (is (= (.hashCode a) (.hashCode b))))
      (testing "equal objects must be equal"
        (is (.equals a  a))
        (is (.equals a  b))
        (is (.equals b  a))
        (is (= a a))
        (is (= a b))
        (is (= b a)))
      (testing "unequal objects must not be equal"
        (is (not (.equals a otherFooValue)))
        (is (not (.equals otherFooValue a)))
        (is (not (.equals a otherValue)))
        (is (not= a otherFooValue))
        (is (not= a otherValue))))))
