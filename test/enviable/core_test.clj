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

(deftest read-env
  (testing "Can read vars from a map"
    (let [env {"VAR_ONE" "Apple"
               "VAR_TWO" "Banana"}]
      (is (= {:one "Apple"
              :two "Banana"
              :three nil}
             (sut/read-env env {:one (sut/var "VAR_ONE")
                                :two (sut/var "VAR_TWO")
                                :three (sut/var "VAR_THREE")})))
      )
    )
  )
