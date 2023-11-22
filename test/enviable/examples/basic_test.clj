(ns enviable.examples.basic-test
  (:require [clojure.test :refer :all]
            [enviable.examples.test-utils :refer [regression-test]]))


(def expected-output
  "
 | Name        | Status    | Input | Value | Requested by | Description          |
 | ----------- | --------- | ----- | ----- | ------------ | -------------------- |
 | NAME        | Required  |       |       | user         | -                    |
 | AGE         | Required  |       |       | user         | Age in years         |
 | IS_EMPLOYED | Required  |       |       | user         | Am I employed?       |
 | WEIGHT      | Required  |       |       | user         | How much do I weigh? |
 | FOOD        | Defaulted |       | Pizza | user         | My favourite food    |
 | SPORT       | Optional  |       |       | user         | My favourite sport   |
 ")

(deftest example.basic
  (-> expected-output
      (regression-test "example.basic")))



