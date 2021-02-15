(ns enviable.examples.example2
  (:require [com.stuartsierra.component :as component]
            [enviable.component :as ec]
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

(defn -main [& args]
  (-> system
      (ec/configure-system)
      (component/start-system)))
