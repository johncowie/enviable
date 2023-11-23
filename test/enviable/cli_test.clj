(ns enviable.cli-test
  (:require [clojure.test :refer :all]
            [enviable.cli :as sut]))

(deftest cli-args->opts-test
  (testing "converts --config.doc to doc? option"
    (is (= {:doc? true}
           (sut/cli-args->opts ["some" "other" "args" "--config.doc"])))
    (is (= {:doc? false}
           (sut/cli-args->opts [])))
    (is (= {:doc? false}
           (sut/cli-args->opts ["an" "arg" "--config"])))))
