(ns enviable.core-test
  (:require [clojure.test :refer :all]
            [enviable.core :as sut]))

(deftest read-var-test
  (testing "Can read a var from an env"
    (let [env {"VAR_ONE" "Apple"
               "VAR_TWO" "Banana"}]
      (is (= "Apple"
             (sut/read-env env (sut/var "VAR_ONE"))))
      (is (= "Banana"
             (sut/read-env env (sut/var "VAR_TWO"))))
      (let [var (sut/var "VAR_THREE")]
        (is (= (sut/error (sut/read-result var nil))
               (sut/read-env env var)))))))

(deftest default-to-test
  (testing "Can specify default value"
    (let [config (-> (sut/var "ONE")
                     (sut/default-to "Hawk"))]
      (is (= "Kite"
             (sut/read-env {"ONE" "Kite"} config)))
      (is (= "Hawk"
             (sut/read-env {} config)))))
  (testing "Can specify default for parsed value"
    (let [config (-> (sut/var "NUMBER")
                     (sut/parse-with #(Integer/parseInt %))
                     (sut/default-to 10))]
      (is (= 7
             (sut/read-env {"NUMBER" "7"} config)))
      (is (= 10
             (sut/read-env {} config)))))

  (testing "Can specify default value as nil"
    (let [config (-> (sut/var "ONE")
                     (sut/default-to nil))]
      (is (= "Bob"
             (sut/read-env {"ONE" "Bob"} config)))
      (is (= nil
             (sut/read-env {} config))))))

(deftest parser-with-test
  (testing "can supply parser for var"
    (testing "if parser throws exception, returns error, otherwise parsed value"
      (let [parse-int (fn [s] (Integer/parseInt s))
            var (-> (sut/var "A")
                    (sut/parse-with parse-int))]
        (is (= 10
               (sut/read-env {"A" "10"} var)))
        (is (= (sut/error (sut/read-result var "bob"))
               (sut/read-env {"A" "bob"} var)))
        (is (= (sut/error (sut/read-result var nil))
               (sut/read-env {} var)))))

    (testing "if parser returns nil, returns error"
      (let [parse-bool (fn [s] (case s "true" true
                                       "false" false
                                       nil))
            var (-> (sut/var "A")
                    (sut/parse-with parse-bool))
            ]
        (is (= true (sut/read-env {"A" "true"} var)))
        (is (= false (sut/read-env {"A" "false"} var)))
        (is (= (sut/error (sut/read-result var "bob"))
               (sut/read-env {"A" "bob"} var)))
        (is (= (sut/error (sut/read-result var nil))
               (sut/read-env {} var)))))))

(deftest read-env-test
  (testing "Can read vars from a map"
    (let [env {"VAR_ONE" "Apple"
               "VAR_TWO" "Banana"
               "VAR_THREE" "Peach"}]
      (is (= {:one   "Apple"
              :two   "Banana"
              :three "Peach"}
             (sut/read-env env {:one   (sut/var "VAR_ONE")
                                :two   (sut/var "VAR_TWO")
                                :three (sut/var "VAR_THREE")})))))
  (testing "Errors are collated"
    (let [parse-int (fn [s] (Integer/parseInt s))
          vars {:one (-> (sut/var "ONE")
                         (sut/parse-with parse-int))
                :two (-> (sut/var "TWO")
                         (sut/parse-with parse-int))}]
      (is (= {:one 10
              :two 20}
             (sut/read-env {"ONE" "10"
                            "TWO" "20"} vars)))
      (is (= {::sut/error [(sut/read-result (:one vars) "bill")
                           (sut/read-result (:two vars) "ben")]
              ::sut/ok    []}
             (sut/read-env {"ONE" "bill"
                            "TWO" "ben"} vars)))
      (is (= {::sut/error [(sut/read-result (:one vars) "bill")]
              ::sut/ok    [(sut/read-result (:two vars) "6" 6)]}
             (sut/read-env {"ONE" "bill"
                            "TWO" "6"} vars))))))

(deftest read-env:nested-test
  (testing "Can specify vars in nested configuration, as well as other values"
    (let [parse-fruit #{"banana" "orange" "apple"}
          parse-veg #{"courgette" "pepper"}

          FRUIT_1 (-> (sut/var "FRUIT_1")
                      (sut/parse-with parse-fruit))
          FRUIT_2 (-> (sut/var "FRUIT_2")
                      (sut/parse-with parse-fruit))
          VEG_1 (-> (sut/var "VEG_1")
                    (sut/parse-with parse-veg))

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
      (is (= {::sut/error [(sut/read-result FRUIT_1 "peanut")]
              ::sut/ok    [(sut/read-result FRUIT_2 "orange" "orange")
                           (sut/read-result VEG_1 "pepper" "pepper")]}
             (-> {"FRUIT_1" "peanut"
                  "FRUIT_2" "orange"
                  "VEG_1"   "pepper"}
                 (sut/read-env config))))
      (is (= {::sut/error [(sut/read-result FRUIT_1 "peanut")
                           (sut/read-result FRUIT_2 "almond")
                           (sut/read-result VEG_1 "cashew")]
              ::sut/ok []}
             (-> {"FRUIT_1" "peanut"
                  "FRUIT_2" "almond"
                  "VEG_1"   "cashew"}
                 (sut/read-env config)))))))
