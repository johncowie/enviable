(ns enviable.core-test
  (:require [clojure.test :refer :all]
            [enviable.core :as sut]))

(deftest read-var-test
  (testing "Can read a var from an env"
    (let [env {"VAR_ONE" "Apple"
               "VAR_TWO" "Banana"}]
      (is (= "Apple"
             (sut/read-var env (sut/var "VAR_ONE"))))
      (is (= "Banana"
             (sut/read-var env (sut/var "VAR_TWO"))))
      (is (= nil
             (sut/read-var env (sut/var "VAR_THREE"))))
      )
    )
  )
