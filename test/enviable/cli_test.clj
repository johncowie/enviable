(ns enviable.cli-test
  (:require [clojure.test :refer :all]
            [enviable.cli :as sut]))

;(deftest pad-columns-test
;  (testing "can pad columns to width of max val in column"
;    (is (= [["name  " "fruit  " "car       "]
;            ["Bob   " "apple  " "          "]
;            ["Gareth" "kumquat" "volkswagen"]]
;           (sut/pad-columns [["name" "fruit" "car"]
;                             ["Bob" "apple" ""]
;                             ["Gareth" "kumquat" "volkswagen"]])))
;    )
;  )
