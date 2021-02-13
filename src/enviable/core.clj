(ns enviable.core)

(defn get-env []
  (System/getenv)
  )

(defn with-parser [var parser]
  (assoc var ::parser parser))

(defn error [val]
  {::error {:value val}})

(defn var [var-name]
  {::name   var-name
   ::parser identity})


(defn- lookup-var [env {::keys [name]}]
  (get env name))

(defn- parse-var [s {::keys [parser]}]
  (try
    (let [parsed (parser s)]
      (if (nil? parsed)
        (error s)
        parsed))
    (catch Exception e
      (error s))))

(defn read-var [env var]
  (some-> env
          (lookup-var var)
          (parse-var var)))

(defn read-env
  ([var-map]
   (read-env (get-env) var-map))
  ([env var-map]
   (->> var-map
        (map (fn [[k v]]
               [k (read-var env v)]
               ))
        (into {}))))