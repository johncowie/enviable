(ns enviable.core
  (:require [enviable.reader :as reader]
            [enviable.component :as component]
            [clojure.string :as str]))

(def var reader/var)
(def parse-with reader/parse-with)
(def default-to reader/default-to)
(def is-optional reader/is-optional)
(def describe reader/describe)
(def is-sensitive reader/is-sensitive)

(def error? reader/error?)
(def read-env reader/read-env)

(def configure reader/configure)
(def document reader/document)

(def configure-system component/configure-system)
(def Configurable component/Configurable)
(def configuration component/configuration)
(def with-configuration component/with-configuration)

;; Useful Parsers TODO move to separate ns

(defn- wrap-parser [parser]
  (fn [var]
    (parse-with var parser)))

(defn- parse-int [s]
  (Integer/parseInt s))

(defn- parse-bool [s]
  (case (str/lower-case s)
    "true" true
    "false" false
    nil))

(defn- parse-double [s]
  (Double/parseDouble s))

(def int-var (comp (wrap-parser parse-int) var))

(def double-var (comp (wrap-parser parse-double) var))

(def bool-var (comp (wrap-parser parse-bool) var))



