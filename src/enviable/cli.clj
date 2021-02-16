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
(def fg-light-red (wrap-escape-code 0 31))
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


(defn determine-status [{:keys       [error?]
                         ::core/keys [input parsed]}]
  (case [error? (some? input) (some? parsed)]
    [true false false] :status/missing
    [true true false] :status/invalid
    [false false true] :status/default
    [false false false] :status/optional
    :status/success
    )
  )

(defn field
  ([s]
   (field identity s))
  ([format s]
   {::text   (str s)
    ::format format
    ::type   ::cell}))

(def separator
  {::type ::separator
   ::text ""})

(defn var-name [result]
  (case result
    ::header (field bold "Name")
    ::separator separator
    (field (::core/name result))))

(defn status [result]
  (case result
    ::header (field bold "Status")
    ::separator separator
    (case (determine-status result)
      :status/invalid (field fg-red "Invalid")
      :status/missing (field fg-yellow "Required")
      :status/default (field fg-grey "Default")
      :status/optional (field fg-grey "Optional")
      (field fg-green "Loaded"))))

(defn description [result]
  (case result
    ::header (field bold "Description")
    ::separator separator
    (if-let [description (::core/description result)]
      (field description)
      (field fg-grey "-"))))

(defn received-input [result]
  (case result
    ::header (field bold "Input")
    ::separator separator
    (field (::core/input result))))

(defn parsed-value [result]
  (case result
    ::header (field bold "Value")
    ::separator separator
    (field (::core/parsed result))))

(defn flatten-results [{::core/keys [error ok] :as res}]
  (concat (map #(assoc % :error? true) error)
          (map #(assoc % :error? false) ok)))

(defn max-width [rows n]
  (->> rows
       (map (comp count ::text #(nth % n)))
       (reduce max 0)))

(defn pad
  ([s width]
   (pad " " s width))
  ([char s width]
   (apply str s
          (repeat (- width (count s)) char))))

(defn bookend [end-str s]
  (str end-str s end-str))

(defn pad-columns [rows]
  (let [col-indices (range 0 (count (first rows)))
        max-widths (map (partial max-width rows) col-indices)]
    (for [row rows]
      (->> (for [i col-indices]
             (let [{::keys [type text format]} (nth row i)
                   max-width (nth max-widths i)]
               (if (= type ::separator)
                 (str "-" (pad "-" "" max-width) "-")
                 (str " " (format (pad text max-width)) " "))))
           (str/join "|")
           (bookend "|")))))

(defn result-str [result]
  (let [results (flatten-results result)
        format (concat [::separator ::header ::separator] results [::separator])]
    (->> format
         (map (juxt var-name status received-input parsed-value description))
         pad-columns
         (str/join "\n")
         (bookend "\n"))))

(defn read-env [vars]
  (let [result (core/read-env vars)]
    (if (core/error? result)
      (do (println (result-str result))
          (System/exit 1))
      result)))
