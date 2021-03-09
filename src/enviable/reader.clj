(ns enviable.reader
  (:require [clojure.string :as str]))

(defn get-ns []
  (try
    (throw (Exception. ""))
    (catch Exception e
      (let [stack (->> e
                       Throwable->map
                       :trace
                       (map first)
                       (remove #(str/starts-with? % "clojure."))
                       (remove #(str/starts-with? % "enviable.reader")))]
        (-> stack
            first
            str
            (str/split #"\$")
            first)))))

(defn var [var-name]
  {::name   var-name
   ::ns     (get-ns)
   ::parser identity})

(defn env-var? [x]
  (::name x))

(defn parse-with [var parser]
  (assoc var ::parser parser))

(defn default-to [var default]
  (assoc var ::default {::default-value default}))

(defn is-optional [var]
  (default-to var nil))

(defn describe [var description]
  (assoc var ::description description))

(defn read-result
  ([var input-val]
   (read-result var input-val nil))
  ([var input-val parsed-val]
   {::name        (::name var)
    ::description (::description var)
    ::ns          (::ns var)
    ::input       input-val
    ::parsed      parsed-val}))

(defn error [read-result]
  {::error [read-result]})

(defn ok [read-result read-val]
  {::ok    [read-result]
   ::value read-val})

(defn error? [result]
  (boolean (::error result)))

(def success? (complement error?))

(defn- lookup-var [env {::keys [name]}]
  (get env name))

(defn- parse-var [s {::keys [parser] :as var}]
  (try
    (let [parsed (parser s)]
      (if (nil? parsed)
        (error (read-result var s))
        (ok (read-result var s parsed) parsed)))
    (catch Exception e
      (error (read-result var s)))))

(defn- read-var [env var]
  (if-let [val (lookup-var env var)]
    (parse-var val var)
    (if-let [default (::default var)]
      (let [v (::default-value default)]
        (ok (read-result var nil v) v))
      (error (read-result var nil)))))

(declare -read-env)

(defn- add-to-result [acc-result [k result]]
  (if (and (success? acc-result) (success? result))
    (-> acc-result
        (assoc-in [::value k] (::value result))
        (update ::ok concat (::ok result)))
    {::error (concat (::error acc-result) (::error result))
     ::ok    (concat (::ok acc-result) (::ok result))}))

(defn- read-env-map
  [env var-map]
  (->> var-map
       (map (fn [[k v]]
              [k (-read-env env v)]))
       (reduce add-to-result {})))

(defn -read-env [env x]
  (cond (env-var? x)
        (read-var env x)
        (map? x)
        (read-env-map env x)
        :else
        {::value x}))

(defn read-env
  ([var-map]
   (read-env var-map (System/getenv)))
  ([var-map env]
   (let [res (-read-env env var-map)]
     (if (error? res)
       res
       (::value res)))))