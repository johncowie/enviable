(ns enviable.examples.test-utils
  (:require [clojure.string :as str]
            [clojure.java.shell :refer [sh]]
            [clojure.test :refer [testing is]]))
(def ansi-colour-pattern #"\p{C}\[[0-9]+;[0-9]+m")
(defn strip-ansi-colours [s]
  (str/replace s ansi-colour-pattern ""))

(defn clean-output-for-comparison [s]
  (->> s
       strip-ansi-colours
       str/trim
       str/split-lines
       (remove str/blank?)
       (map str/trim)))

(defn get-output [example-command]
  (->> (sh "lein" example-command)
       :out
       clean-output-for-comparison
       (str/join "\n")))
(defn regression-test [expected-cli-output example-command]
  (testing "regression test of output"
    (is (= (-> expected-cli-output
               clean-output-for-comparison)
           (-> (sh "lein" example-command)
               :out
               clean-output-for-comparison)))))
