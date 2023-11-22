(ns enviable.examples.other-sources
  (:require [enviable.cli :as env-cli]
            [enviable.core :as env]
            [enviable.source :as source]
            [enviable.reader :refer [config-val]]))

(def config-state (atom {:a "1" :b "2"}))
(defrecord AtomSource [k]
  source/ConfigSource
  (label [_this]
    (str "atom:" (name k)))
  (read-config-value [_this _env]
    (get @config-state k)))

(def config {:this      (-> (config-val (AtomSource. :a))   ;; TODO tidy this syntax up
                            (env/describe "A little bit of this"))
             :that      (config-val (AtomSource. :b))
             :the-other (config-val (AtomSource. :c))})

(defn -main [& args]
  (try
    (let [r (env-cli/configure config args)]
      (println "Loaded config: " r))
    (catch Exception e
      (println (.getMessage e)))))
