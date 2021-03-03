(ns enviable.examples.component
  (:require [com.stuartsierra.component :as component]
            [enviable.component :as ec]
            [enviable.examples.component.db :refer [map->DB]]
            [enviable.examples.component.server :refer [map->Server]]
            [enviable.cli :as cli]
            [enviable.core :as env]))


(def system
  (component/system-map
    :db (map->DB {})
    :server (map->Server {})))

(defn throw-errors [errors]
  (throw (Exception. (str "\nError reading config:\n" (cli/result-str errors)))))

(defn -main [& args]
  (-> system
      (ec/configure-system)
      (env/lmap throw-errors)
      (component/start-system)))
