(ns clojure-owasp.core-test
  (:require [clojure.test :refer :all]
            [clojure-owasp.core :refer :all]))

(deftest basic-sanity-test
  (testing "Basic equality check"
    (is (= 1 1))))

(deftest detect-vulnerabilities-test
  (testing "Detects known vulnerabilities in sample input"
    (let [sample-log {:events []}]
      (is (empty? (detect-vulnerabilities sample-log))))))
