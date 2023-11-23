(ns enviable.examples.basic-test
  (:require [clojure.test :refer :all]
            [enviable.examples.test-utils :refer [regression-test]]))

;(enviable.examples.test-utils/get-output "example.basic" "--config.doc")
(def expected-output
  "Error reading config:
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


(deftest example.basic-doc
  (-> "
 | Name        | Status   | Defaulted | Requested by | Description          |
 | ----------- | -------- | --------- | ------------ | -------------------- |
 | NAME        | Required |           | user         | -                    |
 | AGE         | Required |           | user         | Age in years         |
 | FOOD        | Optional | Pizza     | user         | My favourite food    |
 | SPORT       | Optional |           | user         | My favourite sport   |
 | IS_EMPLOYED | Required |           | user         | Am I employed?       |
 | WEIGHT      | Required |           | user         | How much do I weigh? |"
      (regression-test "example.basic" "--config.doc")
      )
  )



