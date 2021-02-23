(ns enviable.component
  (:require [enviable.core :as env]))

(defprotocol Configurable
  (configuration [this]))

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
              (if (satisfies? Configurable v)
                [k (assoc v :config (get config k))]
                [k v])))
       (into {})))

(defn configure-system
  ([system]
   (configure-system system (System/getenv)))
  ([system opts]
   (-> system
       collate-config
       (env/read-env opts)
       (env/fmap inject-config system))))
