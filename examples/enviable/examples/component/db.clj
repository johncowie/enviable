(ns enviable.examples.component.db
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
