(ns enviable.core)

(defn with-parser [var parser]
  (assoc var ::parser parser))

(defn error [var val]
  {::error [[var val]]})

(defn ok [var val]
  {::ok [[var val]]
   ::value val})

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
        (ok name parsed)))
    (catch Exception e
      (error name s))))

(declare -read-env)

(defn nil-success [{::keys [name]}]
  (ok name nil))

(defn read-var [env x]
  (cond (env-var? x)
        (-> (some-> env
                    (lookup-var x)
                    (parse-var x))
            (or (nil-success x)))
        (map? x)
        (-read-env env x)
        :else
        {::value x}))

(defn add-to-result [acc-result [k result]]
  (case [(error? acc-result) (error? result)]
    [false false]
    (-> acc-result
        (assoc-in [::value k] (::value result))
        (update ::ok concat (::ok result)))
    {::error (concat (::error acc-result) (::error result))
     ::ok    (concat (::ok acc-result) (::ok result))}))

(defn -read-env
  [env var-map]
  (->> var-map
       (map (fn [[k v]]
              [k (read-var env v)]))
       (reduce add-to-result {})))

(defn read-env
  ([var-map]
    (read-env (System/getenv) var-map))
  ([env var-map]
   (let [res (-read-env env var-map)]
     (if (error? res)
       res
       (::value res)))))