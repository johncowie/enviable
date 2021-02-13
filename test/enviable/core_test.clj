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
             (sut/read-var env (sut/var "VAR_THREE")))))))

(deftest read-env
  (testing "Can read vars from a map"
    (let [env {"VAR_ONE" "Apple"
               "VAR_TWO" "Banana"}]
      (is (= {:one "Apple"
              :two "Banana"
              :three nil}
             (sut/read-env env {:one (sut/var "VAR_ONE")
                                :two (sut/var "VAR_TWO")
                                :three (sut/var "VAR_THREE")}))))))

(deftest with-parser-test
  (testing "can supply parser for var"
    (testing "if parser throws exception, returns error, otherwise parsed value"
      (let [parse-int (fn [s] (Integer/parseInt s))
            var (-> (sut/var "A")
                    (sut/with-parser parse-int))
            ]
        (is (= 10 (sut/read-var {"A" "10"} var)))
        (is (= (sut/error "bob") (sut/read-var {"A" "bob"} var)))
        (is (= nil (sut/read-var {} var)))))

    (testing "if parser returns nil, returns error"
      (let [parse-bool (fn [s] (case s "true" true
                                       "false" false
                                       nil))
            var (-> (sut/var "A")
                    (sut/with-parser parse-bool))
            ]
        (is (= true (sut/read-var {"A" "true"} var)))
        (is (= false (sut/read-var {"A" "false"} var)))
        (is (= (sut/error "bob") (sut/read-var {"A" "bob"} var)))
        (is (= nil (sut/read-var {} var))))
      )

    )
  )
