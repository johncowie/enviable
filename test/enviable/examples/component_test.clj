(ns enviable.examples.component-test
  (:require [clojure.test :refer :all]
            [enviable.examples.test-utils :refer [regression-test]]))

;(enviable.examples.test-utils/get-output "example.component" "--config.doc")

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

(deftest example.component-doc
  (->
    "| Name            | Status   | Defaulted | Requested by                              | Description |
     | --------------- | -------- | --------- | ----------------------------------------- | ----------- |
     | DB_HOST         | Required |           | enviable.examples.component.db.DB         | -           |
     | DB_PASSWORD     | Required |           | enviable.examples.component.db.DB         | -           |
     | ANOTHER_DB_HOST | Required |           | user                                      | -           |
     | PORT            | Required |           | enviable.examples.component.server.Server | -           |"
    (regression-test "example.component" "--config.doc")
    )
  )
