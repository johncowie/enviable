(ns enviable.reader
  (:require [clojure.string :as str]
            [enviable.source :as source]
            [enviable.source.env :refer [env-var]]
            [enviable.reporter :as reporter]))

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
  {::source (env-var var-name)
   ::ns     (get-ns)
   ::parser identity})

(defn config-val [source]
  {::source source
   ::ns (get-ns)
   ::parser identity})

(defn config-var? [x]
  (-> x ::source source/config-source?))

(defn parse-with [var parser]
  (assoc var ::parser parser))

(defn default-to [var default]
  (assoc var ::default {::default-value default}))

(defn is-optional [var]
  (default-to var nil))

(defn describe [var description]
  (assoc var ::description description))

(defn is-sensitive [var]
  (assoc var ::sensitive true))

(defn read-result
  ([var input-val]
   (read-result var input-val nil))
  ([var input-val parsed-val]
   {::name        (-> var ::source source/label)
    ::description (::description var)
    ::ns          (::ns var)
    ::input       input-val
    ::parsed      parsed-val
    ::sensitive   (::sensitive var)
    ::default     (::default var)}))

(defn doc-result
  [var]
  {::name         (-> var ::source source/label)
   ::description  (::description var)
   ::ns           (::ns var)
   ::default      (::default var)})

(defn error [read-result]
  {::error [read-result]})

(defn ok [read-result read-val]
  {::ok    [read-result]
   ::value read-val})

(defn error? [result]
  (boolean (::error result)))

(defn fmap [v-or-error f & args]
  (if (error? v-or-error)
    v-or-error
    (apply f v-or-error args)))

(defn lmap [v-or-error f & args]
  (if (error? v-or-error)
    (apply f v-or-error args)
    v-or-error))

(def success? (complement error?))

(defn- lookup-var [env var]
  (-> var ::source (source/read-config-value env)))

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

(defn- document-var [var]
  (error (doc-result var)))

(defn- configure-var [{:keys [doc? env]} var]
  (if doc?
    (document-var var)
    (read-var env var)))

(defn- add-to-result [acc-result [k result]]
  (if (and (success? acc-result) (success? result))
    (-> acc-result
        (assoc-in [::value k] (::value result))
        (update ::ok concat (::ok result)))
    {::error (concat (::error acc-result) (::error result))
     ::ok    (concat (::ok acc-result) (::ok result))}))

(declare -read-env)

(defn- read-env-map
  [opts var-map]
  (->> var-map
       (map (fn [[k v]]
              [k (-read-env opts v)]))
       (reduce add-to-result {})))

(defn -read-env [opts x]
  (cond (config-var? x)
        (configure-var opts x)
        (map? x)
        (read-env-map opts x)
        :else
        {::value x}))

(defn configure
  ([var-map]
   (configure var-map {:env (System/getenv)
                       :doc? false}))
  ([var-map opts]
   (let [res (-read-env opts var-map)]
     (if (error? res)
       res
       (::value res)))))

(defn read-env
  ([var-map]
   (read-env var-map (System/getenv)))
  ([var-map env]
   (configure var-map {:env env})))

(defn document
  [var-map]
  (configure var-map {:env {} :doc? true}))

(defn configure-throw
  [vars opts]
  (let [r (configure vars opts)]
    (if (error? r)
      (throw (Exception. (if (:doc? opts)
                           (reporter/document-config (:reporter opts) r)
                           (reporter/report-config-status (:reporter opts) r))))
      r)))
