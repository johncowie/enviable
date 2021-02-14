(ns enviable.examples.example1
  (:gen-class)
  (:require [enviable.cli :refer [read-env]]
            [enviable.core :as env]))

(def vars {:person {:name (env/var "NAME")
                    :age  (env/int-var "AGE")}})

(defn -main [& args]
  (let [config (read-env vars)]
    (println config)))


