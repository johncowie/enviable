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

(deftest with-parser-test
  (testing "can supply parser for var"
    (testing "if parser throws exception, returns error, otherwise parsed value"
      (let [parse-int (fn [s] (Integer/parseInt s))
            var (-> (sut/var "A")
                    (sut/with-parser parse-int))
            ]
        (is (= 10 (sut/read-var {"A" "10"} var)))
        (is (= (sut/error "A" "bob") (sut/read-var {"A" "bob"} var)))
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
        (is (= (sut/error "A" "bob") (sut/read-var {"A" "bob"} var)))
        (is (= nil (sut/read-var {} var)))))))

(deftest read-env-test
  (testing "Can read vars from a map"
    (let [env {"VAR_ONE" "Apple"
               "VAR_TWO" "Banana"}]
      (is (= {:one   "Apple"
              :two   "Banana"
              :three nil}
             (sut/read-env env {:one   (sut/var "VAR_ONE")
                                :two   (sut/var "VAR_TWO")
                                :three (sut/var "VAR_THREE")})))))
  (testing "Errors are collated"
    (let [parse-int (fn [s] (Integer/parseInt s))
          vars {:one (-> (sut/var "ONE")
                         (sut/with-parser parse-int))
                :two (-> (sut/var "TWO")
                         (sut/with-parser parse-int))}]
      (is (= {:one 10
              :two 20}
             (sut/read-env {"ONE" "10"
                            "TWO" "20"} vars)))
      (is (= {::sut/error [["ONE" "bill"]
                           ["TWO" "ben"]]}
             (sut/read-env {"ONE" "bill"
                            "TWO" "ben"} vars)))
      (is (= {::sut/error [["ONE" "bill"]]}
             (sut/read-env {"ONE" "bill"
                            "TWO" "6"} vars))))))

(deftest read-env:nested-test
  (testing "Can specify vars in nested configuration, as well as other values"
    (let [parse-fruit #{"banana" "orange" "apple"}
          parse-veg #{"courgette" "pepper"}

          FRUIT_1 (-> (sut/var "FRUIT_1")
                      (sut/with-parser parse-fruit))
          FRUIT_2 (-> (sut/var "FRUIT_2")
                      (sut/with-parser parse-fruit))
          VEG_1 (-> (sut/var "VEG_1")
                    (sut/with-parser parse-veg))

          config {:food {:fruit     {:one FRUIT_1
                                     :two FRUIT_2}
                         :veg       VEG_1
                         :condiment "ketchup"}}]
      (is (= {:food {:fruit     {:one "banana"
                                 :two "orange"}
                     :veg       "pepper"
                     :condiment "ketchup"}}
             (-> {"FRUIT_1" "banana"
                  "FRUIT_2" "orange"
                  "VEG_1"   "pepper"}
                 (sut/read-env config))))
      (is (= {::sut/error [["FRUIT_1" "peanut"]]}
             (-> {"FRUIT_1" "peanut"
                  "FRUIT-2" "orange"
                  "VEG_1"   "pepper"}
                 (sut/read-env config))))
      (is (= {::sut/error [["FRUIT_1" "peanut"]
                           ["FRUIT_2" "almond"]
                           ["VEG_1" "cashew"]]}
             (-> {"FRUIT_1" "peanut"
                  "FRUIT_2" "almond"
                  "VEG_1"   "cashew"}
                 (sut/read-env config)))))))
