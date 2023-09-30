(ns unit.minimal-clojure-project.core-test
  (:require [clojure.test :refer :all]
            [midje.sweet :refer :all]
            [minimal-clojure-project.core :refer :all]))

(deftest a-test
  (testing "FIXME, I fail."
    (is (= 1 1))))

;; Midje coexists with clojure.test, so you can use both at the same time, even in the same file.
(fact "testing"
  (= 1 1) => true)


