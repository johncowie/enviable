(ns enviable.cli
  (:require [enviable.core :as core]
            [clojure.string :as str]))

(defn- escape-code
  ([i] (escape-code 0 i))
  ([g i]
   (str "\033[" g ";" i "m")))

(defn wrap-escape-code
  ([code] (wrap-escape-code 0 code))
  ([sub-code code]
   (fn [s]
     (str (escape-code sub-code code)
          s
          (escape-code 0)))))

(def fg-grey (wrap-escape-code 1 30))
(def fg-red (wrap-escape-code 31))
(def fg-green (wrap-escape-code 32))
(def fg-yellow (wrap-escape-code 33))

(def bold (wrap-escape-code 1))

;(def ^:dynamic *colors*
;  "foreground color map"
;  (zipmap [:grey :red :green :yellow
;           :blue :magenta :cyan :white]
;          (map escape-code
;               (range 30 38))))
;
;(def ^:dynamic *highlights*
;  "background color map"
;  (zipmap [:on-grey :on-red :on-green :on-yellow
;           :on-blue :on-magenta :on-cyan :on-white]
;          (map escape-code
;               (range 40 48))))
;
;(def ^:dynamic *attributes*
;  "attributes color map"
;  (into {}
;        (filter (comp not nil? key)
;                (zipmap [:bold, :dark, nil, :underline,
;                         :blink, nil, :reverse-color, :concealed]
;                        (map escape-code (range 1 9))))))


(defn determine-status [{:keys [error?]
                         ::core/keys [input parsed]}]
  (case [error? (some? input) (some? parsed)]
    [true false false] :status/missing
    [true true false] :status/invalid
    [false false true] :status/default
    :status/success
    )
  )

(defn field
  ([str]
    (field identity str))
  ([format str]
   {::text   str
    ::format format}))

(defn var-name [result]
  (if (= result ::header)

    (field bold "Name")
    (field (::core/name result))))

(defn status [result]
  (if (= result ::header)
    (field bold "Status")
    (case (determine-status result)
      :status/invalid (field fg-red "Invalid")
      :status/missing (field fg-yellow "Missing")
      :status/default (field fg-grey "Default")
      (field fg-green "Success"))))

(defn description [result]
  (if (= result ::header)
    (field bold "Description")
    (field (::core/description result))))

(defn received-input [result]
  (if (= result ::header)
    (field bold "Input")
    (field (::core/input result))))

(defn parsed-value [result]
  (if (= result ::header)
    (field bold "Parsed")
    (field (::core/parsed result))))

(defn flatten-results [{::core/keys [error ok] :as res}]
  (concat (map #(assoc % :error? true) error)
          (map #(assoc % :error? false) ok)))

(defn max-width [rows n]
  (->> rows
       (map (comp count ::text #(nth % n)))
       (reduce max 0)))

(defn pad [s width]
  (apply str s
         (repeat (- width (count s)) " ")))

(defn pad-columns [rows]
  (let [col-indices (range 0 (count (first rows)))
        max-widths (map (partial max-width rows) col-indices)]
    (for [row rows]
      (for [i col-indices]
        (let [s (::text (nth row i))
              f (::format (nth row i))]
          (f (pad s (nth max-widths i))))))))

(defn add-separators [row]
  (str "| " (str/join " | " row) " |"))

(defn result-str [result]
  (->> result
       flatten-results
       (concat [::header])
       (map (juxt var-name status received-input parsed-value description))
       pad-columns
       (map add-separators)
       (str/join "\n")))

(defn read-env [vars]
  (let [result (core/read-env vars)]
    (if (core/error? result)
      (do (println (result-str result))
          (System/exit 1))
      result)))
