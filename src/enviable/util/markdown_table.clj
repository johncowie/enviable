(ns enviable.util.markdown-table
  (:require [clojure.string :as str]))

(defn cell
  ([v]
    (cell v identity))
  ([v f]
   (let [text (str v)]
     {::text      text
      ::width     (count text)
      ::formatter f
      ::borders   {::bottom false
                   ::top    false
                   ::left   true
                   ::right  true}})))

(defn- render-cell [{::keys [formatter text width borders]}]
  (let [{::keys [bottom top left right]} borders]
    (->> [(when top (apply str (repeat width "-")))
          (apply str (formatter text) (repeat (- width (count text)) " "))
          (when bottom (apply str (repeat width "-")))]
         (remove nil?)
         (map (fn [row] (if left (str "| " row) (str " " row))))
         (map (fn [row] (if right (str row " |") (str row " ")))))))

(defn- dedupe-horizontal-borders [row-str]
  (str/replace row-str #"\|\|" "|"))

(defn- vertical-border? [row-str]
  (when row-str
    (->> row-str
         distinct
         (every? #{\- \| \space}))))

(defn- dedupe-vertical-borders [row-strs]
  (->> row-strs
       (reduce (fn [rows row]
                 (if (and (vertical-border? (last rows)) (vertical-border? row))
                   rows
                   (conj rows row))) [])))

(defn- render-row [cells]
  (->> cells
       (map render-cell)
       (apply map str)
       (map dedupe-horizontal-borders)))

(defn transpose [m]
  (apply mapv vector m))

(defn- justify-column [cells]
  (let [max-width (->> cells
                       (map ::width)
                       (apply max))]
    (->> cells
         (map #(assoc % ::width max-width)))))

(defn- justify-columns [cell-grid]
  (->> cell-grid
       transpose
       (map justify-column)
       transpose))

(defn auto-set-borders [[headers & rows]]
  (concat [(map #(assoc-in % [::borders ::bottom] true) headers)]
          rows))

(defn bookend [be s]
  (str be s be))

(defn render-table [cell-grid]
  (->> cell-grid
       auto-set-borders
       (justify-columns)
       (mapcat render-row)
       (dedupe-vertical-borders)
       (str/join "\n")
       (bookend "\n")))


;; TODO escape pipes in strings (\|)
;; TODO Take care of borders automatically?
;; TODO write test for table

;; Move CLI namespace into outputs
