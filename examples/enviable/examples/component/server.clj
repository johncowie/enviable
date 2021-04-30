(ns enviable.examples.component.server
  (:require [com.stuartsierra.component :as component]
            [enviable.core :as env]))

(defrecord Server [config]
  component/Lifecycle
  (start [this]
    (println "Starting server with config: " config))
  (stop [this])
  env/Configurable
  (env/configuration [this]
    {:port (env/var "PORT")}))
