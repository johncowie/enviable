(ns enviable.source)

(defprotocol ConfigSource
  (label [this])
  (read-config-value [this env]))

(defrecord EnvVar [env-var-name]
  ConfigSource
  (label [this]
    env-var-name)
  (read-config-value [_this env]
    (get env env-var-name)))

(defn env-var [env-var-name]
  (EnvVar. env-var-name))

(defn config-source? [x]
  (satisfies? ConfigSource x))
