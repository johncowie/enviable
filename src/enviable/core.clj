(ns enviable.core)

(defn get-env []
  (System/getenv))

(defn with-parser [var parser]
  (assoc var ::parser parser))

(defn error [var val]
  {::error [[var val]]})

(defn error? [result]
  (boolean (::error result)))

(defn var [var-name]
  {::name   var-name
   ::parser identity})

(defn env-var? [x]
  (::name x))

(defn- lookup-var [env {::keys [name]}]
  (get env name))

(defn- parse-var [s {::keys [name parser]}]
  (try
    (let [parsed (parser s)]
      (if (nil? parsed)
        (error name s)
        parsed))
    (catch Exception e
      (error name s))))

(declare read-env)

(defn read-var [env x]
  (cond (env-var? x)
        (some-> env
                (lookup-var x)
                (parse-var x))
        (map? x)
        (read-env env x)
        :else
        x))

(defn add-to-result [acc-result [k result]]
  (case [(error? acc-result) (error? result)]
    [true true]
    {::error (concat (::error acc-result) (::error result))}
    [true false]
    acc-result
    [false true]
    result
    [false false]
    (assoc acc-result k result)))

(defn read-env
  ([var-map]
   (read-env (get-env) var-map))
  ([env var-map]
   (->> var-map
        (map (fn [[k v]]
               [k (read-var env v)]))
        (reduce add-to-result {}))))