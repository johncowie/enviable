(ns enviable.component
  (:require [enviable.reader :as reader]
            [enviable.cli :as cli]
            [com.stuartsierra.component :as component])
  (:import (com.stuartsierra.component SystemMap)))

(defprotocol Configurable
  (configuration [this]))

(defrecord WithConfiguration [config]
  Configurable
  (configuration [this]
    config))

(defn with-configuration [component config]
  (-> (WithConfiguration. config)
      (assoc ::component-with-configuration component)))

(defn- collate-config [system]
  (->> system
       (map (fn [[k v]]
              (cond
                (satisfies? Configurable v)
                [k (configuration v)]
                (reader/env-var? v)
                [k v]
                (associative? v)
                [k (collate-config v)]
                :else
                [k nil])))
       flatten
       (apply component/system-map)))

(defn- inject-config [config component]
  (->> component
       (map (fn [[k v]]
              (cond
                (::component-with-configuration v)
                [k (assoc (::component-with-configuration v) :config (get config k))]
                (satisfies? Configurable v)
                [k (assoc v :config (get config k))]
                (reader/env-var? v)
                [k (get config k)]
                (associative? v)
                [k (inject-config (get config k) v)]
                :else
                [k v])))
       flatten
       (apply component/system-map)))

(defn configure-system
  ([system]
   (configure-system system (System/getenv)))
  ([system opts]
   (-> system
       collate-config
       (reader/read-env opts)
       (reader/lmap (fn [error]
                      (throw (Exception. (str "\nError reading config:\n" (cli/result-str error))))))
       (reader/fmap inject-config system))))
