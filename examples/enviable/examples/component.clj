(ns enviable.examples.component
  (:require [com.stuartsierra.component :as component]
            [enviable.cli :as cli]
            [enviable.core :as env]
            [enviable.examples.component.db :refer [map->DB]]
            [enviable.examples.component.server :refer [map->Server]]))


(def system
  (component/system-map
    :db (map->DB {})
    :another-db (-> (map->DB {})
                    (env/with-configuration {:host (env/var "ANOTHER_DB_HOST")}))
    :server (map->Server {})))

(defn -main [& args]
  (try
    (-> system
        (cli/configure-system args)
        (component/start-system))
    (catch Exception e
      (println (.getMessage e)))))
