(ns enviable.output.markdown
  (:require [enviable.reader :as core]
            [enviable.util.markdown-table :as table]
            [enviable.util.ansi :as ascii]))


(defn determine-status [{:keys       [error?]
                         ::core/keys [input parsed]}]
  (case [error? (some? input) (some? parsed)]
    [true false false] :status/missing
    [true true false] :status/invalid
    [false false true] :status/default
    [false false false] :status/optional
    :status/success
    ))

(defn flatten-results [{::core/keys [error ok] :as res}]
  (concat (map #(assoc % :error? true) error)
          (map #(assoc % :error? false) ok)))

(defn header-cell [text]
  (table/cell text ascii/bold))

(defn name-cell [{::core/keys [name]}]
  (table/cell name))

(def sensitive-value-cell
  (table/cell "********"))

(defn input-cell [{::core/keys [input sensitive]}]
  (if (and input sensitive)
    sensitive-value-cell
    (table/cell input)))

(defn value-cell [{::core/keys [parsed sensitive] :as val}]
  (if (and parsed sensitive)
    sensitive-value-cell
    (table/cell parsed)))

(defn description-cell [{::core/keys [description]}]
  (if description
    (table/cell description)
    (table/cell "-" ascii/fg-grey)))

(defn ns-cell [{::core/keys [ns]}]
  (table/cell ns))

(defn status-cell [result]
  (case (determine-status result)
    :status/invalid (table/cell "Invalid" ascii/fg-red)
    :status/missing (table/cell "Required" ascii/fg-yellow)
    :status/default (table/cell "Default" ascii/fg-grey)
    :status/optional (table/cell "Optional" ascii/fg-grey)
    (table/cell "Loaded" ascii/fg-green)))

(defn result-str [result]
  (let [results (flatten-results result)
        cols [["Name" name-cell]
              ["Status" status-cell]
              ["Input" input-cell]
              ["Value" value-cell]
              ["Requested by" ns-cell]
              ["Description" description-cell]]]
    (->> (for [[header value-cell-fn] cols]
           (concat [(header-cell header)]
                   (map value-cell-fn results)))
         (table/transpose)
         (table/render-table))))
