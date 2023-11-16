(ns enviable.source.env
  (:require [enviable.source :refer [ConfigSource]]))

(defrecord EnvVar [env-var-name]
  ConfigSource
  (label [this]
    env-var-name)
  (read-config-value [_this env]
    (get env env-var-name)))

(defn env-var [env-var-name]
  (EnvVar. env-var-name))
