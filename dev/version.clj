(ns version
  (:require [leiningen.change :as lc]
            [clojure.string :as str]))

(defn next-snapshot-str [version]
  (let [[major minor patch]
        (as-> version $
              (str/replace $ #"[^0-9.]" "")
              (str/split $ #"\.")
              (map #(Integer/parseInt %) $))]
    (format "%s.%s.%s-SNAPSHOT" major minor (inc patch))))

(defn set-version [version & args]
  (lc/change {:root "./"} "version" (constantly version)))

(defn next-snapshot [& _args]
  (lc/change {:root "./"} "version" next-snapshot-str))
