(ns enviable.examples.component-test
  (:require [clojure.test :refer :all]
            [enviable.examples.test-utils :refer [regression-test]]))

;(get-output "example.component")

(deftest example.component
  (-> "
 Error reading config:
 | Name            | Status   | Input | Value | Requested by                              | Description |
 | --------------- | -------- | ----- | ----- | ----------------------------------------- | ----------- |
 | DB_HOST         | Required |       |       | enviable.examples.component.db.DB         | -           |
 | DB_PASSWORD     | Required |       |       | enviable.examples.component.db.DB         | -           |
 | ANOTHER_DB_HOST | Required |       |       | user                                      | -           |
 | PORT            | Required |       |       | enviable.examples.component.server.Server | -           |
 "
      (regression-test "example.component")))
