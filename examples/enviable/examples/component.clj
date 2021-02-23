(ns enviable.examples.component
  (:require [com.stuartsierra.component :as component]
            [enviable.component :as ec]
            [enviable.cli :as cli]
            [enviable.core :as env]))


(defrecord DB [config]
  component/Lifecycle
  (start [this]
    (println "Starting DB with config: " config))
  (stop [this])
  ec/Configurable
  (configuration [this]
    {:host (env/var "DB_HOST")}))

(defrecord Server [config]
  component/Lifecycle
  (start [this]
    (println "Starting server with config: " config))
  (stop [this])
  ec/Configurable
  (configuration [this]
    {:port (env/var "PORT")}))

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
