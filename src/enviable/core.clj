(ns enviable.core)

(defn get-env []
  (System/getenv)
  )

(defn var [var-name]
  {::name var-name})

(defn read-var [env var]
  (let [{::keys [name]} var]
    (get env name)))

(defn read-env
  ([var-map]
    (read-env (get-env) var-map))
  ([env var-map]
    (->> var-map
         (map (fn [[k v]]
                [k (read-var env v)]
                ))
         (into {}))))