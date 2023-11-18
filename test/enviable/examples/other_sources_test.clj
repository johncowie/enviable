(ns enviable.examples.other-sources-test
  (:require [clojure.test :refer :all]
            [enviable.examples.test-utils :refer [regression-test]])
  )

;(get-output "example.other-sources")

(deftest example.component
  (-> "
 | Name   | Status   | Input | Value | Requested by | Description          |
 | ------ | -------- | ----- | ----- | ------------ | -------------------- |
 | atom:c | Required |       |       | user         | -                    |
 | atom:a | Loaded   | 1     | 1     | user         | A little bit of this |
 | atom:b | Loaded   | 2     | 2     | user         | -                    |
 "
      (regression-test "example.other-sources")))
