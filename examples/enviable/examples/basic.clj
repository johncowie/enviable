(ns enviable.examples.basic
  (:gen-class)
  (:require [enviable.core :as env]
            [enviable.cli :as env-cli]))

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
                                         (env/describe "How much do I weigh?"))
                    }})

(defn -main [& args]
  (try
    (let [r (env-cli/configure vars args)]
      (println "Loaded config: " r))
    (catch Exception e
      (println (.getMessage e)))))


