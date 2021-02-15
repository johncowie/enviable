(ns enviable.examples.example1
  (:gen-class)
  (:require [enviable.cli :refer [read-env]]
            [enviable.core :as env]))

(def vars {:person {:name           (env/var "NAME")
                    :age            (-> (env/int-var "AGE")
                                        (env/describe "Age in years"))
                    :favourite-food (-> (env/var "FOOD")
                                        (env/default-to "Pizza")
                                        (env/describe "My favourite food"))}})

(defn -main [& args]
  (let [config (read-env vars)]
    (println config)))


