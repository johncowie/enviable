(ns enviable.examples.basic
  (:gen-class)
  (:require [enviable.cli :refer [read-env]]
            [enviable.core :as env]))

;; TODO come up with better example
(def vars {:person {:name           (env/var "NAME")
                    :age            (-> (env/int-var "AGE")
                                        (env/describe "Age in years"))
                    :favourite-food (-> (env/var "FOOD")
                                        (env/default-to "Pizza")
                                        (env/describe "My favourite food"))
                    :favourite-sport (-> (env/var "SPORT")
                                         (env/is-optional)
                                         (env/describe "My favourite sport"))
                    :employed?       (-> (env/bool-var "IS_EMPLOYED")
                                         (env/describe "Am I employed?"))
                    :weight          (-> (env/double-var "WEIGHT")
                                         (env/describe "How much do I weight?"))
                    }})

(defn -main [& args]
  (read-env vars))


