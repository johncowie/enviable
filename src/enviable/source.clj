(ns enviable.source)

(defprotocol ConfigSource
  (label [this])
  (read-config-value [this env]))

(defn config-source? [x]
  (satisfies? ConfigSource x))
