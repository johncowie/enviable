(ns enviable.core-test
  (:require [clojure.test :refer :all]
            [enviable.core :as sut]
            [enviable.reader :as reader]))

(deftest read-var-test
  (testing "Can read a var from an env"
    (let [env {"VAR_ONE" "Apple"
               "VAR_TWO" "Banana"}]
      (is (= "Apple"
             (sut/read-env (sut/var "VAR_ONE") env)))
      (is (= "Banana"
             (sut/read-env (sut/var "VAR_TWO") env)))
      (let [var (sut/var "VAR_THREE")]
        (is (= (reader/error (reader/read-result var nil))
               (sut/read-env var env)))))))

(deftest default-to-test
  (testing "Can specify default value"
    (let [config (-> (sut/var "ONE")
                     (sut/default-to "Hawk"))]
      (is (= "Kite"
             (sut/read-env config {"ONE" "Kite"})))
      (is (= "Hawk"
             (sut/read-env config {})))))
  (testing "Can specify default for parsed value"
    (let [config (-> (sut/var "NUMBER")
                     (sut/parse-with #(Integer/parseInt %))
                     (sut/default-to 10))]
      (is (= 7
             (sut/read-env config {"NUMBER" "7"})))
      (is (= 10
             (sut/read-env config {})))))

  (testing "Can specify default value as nil"
    (let [config (-> (sut/var "ONE")
                     (sut/default-to nil))]
      (is (= "Bob"
             (sut/read-env config {"ONE" "Bob"})))
      (is (= nil
             (sut/read-env config {}))))))

(deftest parser-with-test
  (testing "can supply parser for var"
    (testing "if parser throws exception, returns error, otherwise parsed value"
      (let [parse-int (fn [s] (Integer/parseInt s))
            var (-> (sut/var "A")
                    (sut/parse-with parse-int))]
        (is (= 10
               (sut/read-env var {"A" "10"})))
        (is (= (reader/error (reader/read-result var "bob"))
               (sut/read-env var {"A" "bob"})))
        (is (= (reader/error (reader/read-result var nil))
               (sut/read-env var {})))))

    (testing "if parser returns nil, returns error"
      (let [parse-bool (fn [s] (case s "true" true
                                       "false" false
                                       nil))
            var (-> (sut/var "A")
                    (sut/parse-with parse-bool))
            ]
        (is (= true (sut/read-env var {"A" "true"})))
        (is (= false (sut/read-env var {"A" "false"})))
        (is (= (reader/error (reader/read-result var "bob"))
               (sut/read-env var {"A" "bob"})))
        (is (= (reader/error (reader/read-result var nil))
               (sut/read-env var {})))))))

(deftest read-env-test
  (testing "Can read vars from a map"
    (let [env {"VAR_ONE"   "Apple"
               "VAR_TWO"   "Banana"
               "VAR_THREE" "Peach"}]
      (is (= {:one   "Apple"
              :two   "Banana"
              :three "Peach"}
             (sut/read-env {:one   (sut/var "VAR_ONE")
                            :two   (sut/var "VAR_TWO")
                            :three (sut/var "VAR_THREE")} env)))))
  (testing "Errors are collated"
    (let [parse-int (fn [s] (Integer/parseInt s))
          vars {:one (-> (sut/var "ONE")
                         (sut/parse-with parse-int))
                :two (-> (sut/var "TWO")
                         (sut/parse-with parse-int))}]
      (is (= {:one 10
              :two 20}
             (sut/read-env vars {"ONE" "10"
                                 "TWO" "20"})))
      (is (= {::reader/error [(reader/read-result (:one vars) "bill")
                              (reader/read-result (:two vars) "ben")]
              ::reader/ok    []}
             (sut/read-env vars {"ONE" "bill"
                                 "TWO" "ben"})))
      (is (= {::reader/error [(reader/read-result (:one vars) "bill")]
              ::reader/ok    [(reader/read-result (:two vars) "6" 6)]}
             (sut/read-env vars {"ONE" "bill"
                                 "TWO" "6"}))))))

(deftest read-env-nested-test
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
             (->> {"FRUIT_1" "banana"
                   "FRUIT_2" "orange"
                   "VEG_1"   "pepper"}
                  (sut/read-env config))))
      (is (= {::reader/error [(reader/read-result FRUIT_1 "peanut")]
              ::reader/ok    [(reader/read-result FRUIT_2 "orange" "orange")
                              (reader/read-result VEG_1 "pepper" "pepper")]}
             (->> {"FRUIT_1" "peanut"
                   "FRUIT_2" "orange"
                   "VEG_1"   "pepper"}
                  (sut/read-env config))))
      (is (= {::reader/error [(reader/read-result FRUIT_1 "peanut")
                              (reader/read-result FRUIT_2 "almond")
                              (reader/read-result VEG_1 "cashew")]
              ::reader/ok    []}
             (->> {"FRUIT_1" "peanut"
                   "FRUIT_2" "almond"
                   "VEG_1"   "cashew"}
                  (sut/read-env config)))))))

(deftest document-env-test
  (testing "Can document all config"
    (let [INT_VAR (-> (sut/int-var "INT_VAR")
                      (sut/default-to 1)
                      (sut/describe "I need an int"))
          BOOL_VAR (-> (sut/int-var "BOOL_VAR")
                       (sut/default-to false)
                       (sut/describe "I need a bool"))
          config {:a INT_VAR
                  :b BOOL_VAR}]
      (is (= {::reader/error [(reader/doc-result INT_VAR)
                              (reader/doc-result BOOL_VAR)]
              ::reader/ok []}
             (sut/document config))))))
