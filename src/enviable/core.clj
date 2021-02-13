(ns enviable.core)

(defn get-env []
  (System/getenv)
  )

(defn var [var-name]
  {::name var-name})

(defn read-var [env var]
  (let [{::keys [name]} var]
    (get env name)))