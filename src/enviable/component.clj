(ns enviable.component
  (:require [enviable.reader :as reader]
            [enviable.cli :as cli]))

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
              (if (satisfies? Configurable v)
                [k (configuration v)]
                [k nil])))
       (into {})))

(defn- inject-config [config system]
  (->> system
       (map (fn [[k v]]
              (cond
                (::component-with-configuration v)
                [k (assoc (::component-with-configuration v) :config (get config k))]
                (satisfies? Configurable v)
                [k (assoc v :config (get config k))]
                :else
                [k v])))
       (into {})))

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
