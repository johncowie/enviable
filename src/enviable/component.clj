(ns enviable.component
  (:require [enviable.reader :as reader]
            [enviable.output.markdown :as md]))

(defprotocol Configurable
  (configuration [this]))

(defrecord WithConfiguration [config]
  Configurable
  (configuration [this]
    config))

(defn with-configuration [component config]
  (-> (WithConfiguration. config)
      (assoc ::component-with-configuration component)))

(defn map-over-record-vals [f r]
  (->> (keys r)
       (reduce (fn [acc k]
                 (let [v (get acc k)]
                   (assoc acc k (f k v)))) r)))

(defn- collate-config [system]
  (->> system
       (map-over-record-vals (fn [_k v]
                               (cond
                                 (satisfies? Configurable v)
                                 (configuration v)
                                 (reader/env-var? v)
                                 v
                                 (associative? v)
                                 (collate-config v))))))

(defn- inject-config [config component]
  (->> component
       (map-over-record-vals (fn [k v]
                               (cond
                                 (::component-with-configuration v)
                                 (assoc (::component-with-configuration v) :config (get config k))
                                 (satisfies? Configurable v)
                                 (assoc v :config (get config k))
                                 (reader/env-var? v)
                                 (get config k)
                                 (associative? v)
                                 (inject-config (get config k) v)
                                 :else
                                 v)))))

(defn configure-system
  ([system]
   (configure-system system (System/getenv)))
  ([system opts]
   (-> system
       collate-config
       (reader/read-env opts)
       (reader/lmap (fn [error]
                      (throw (Exception. (str "\nError reading config:\n" (md/result-str error))))))
       (reader/fmap inject-config system))))
