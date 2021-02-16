(ns enviable.component
  (:require [enviable.cli :as cli]))

(defprotocol Configurable
  (configuration [this]))

(defn- collate-config [system]
  (->> system
       (map (fn [[k v]]
              (if (satisfies? Configurable v)
                [k (configuration v)]
                [k nil])))
       (into {})))

(defn- inject-config [system config]
  (->> system
       (map (fn [[k v]]
              (if(satisfies? Configurable v)
                [k (assoc v :config (get config k))]
                [k v])))
       (into {})))

(defn configure-system
  ([system]
   (configure-system system (System/getenv)))
  ([system env]
   (let [config (cli/read-env env (collate-config system))]
     (inject-config system config))))
