(ns version
  (:require [leiningen.change :as lc]
            [clojure.string :as str]
            [clojure.java.io :as io]))

(defn next-snapshot-str [version]
  (let [[major minor patch]
        (as-> version $
              (str/replace $ #"[^0-9.]" "")
              (str/split $ #"\.")
              (map #(Integer/parseInt %) $))]
    (format "%s.%s.%s-SNAPSHOT" major minor (inc patch))))

(defn current-version [& args]
  (-> (io/file "project.clj")
      (slurp)
      (read-string)
      (nth 2)
      (println)))

(defn set-version [version & args]
  (lc/change {:root "./"} "version" (constantly version)))

(defn next-snapshot [& _args]
  (lc/change {:root "./"} "version" next-snapshot-str))
