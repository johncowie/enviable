(ns enviable.cli
  (:require [enviable.core :as core]))


;; TODO
;; [ ] Design output
;;     | Var | Status | Description | Input | Parsed |
;; [ ] Bring in library for colours
;;     | Add colours to output (for status, and default flag)


(defn result-str [result]
  (println "ERROR: " result))

(defn read-env [vars]
  (let [result (core/read-env vars)]
    (if (core/error? result)
      (do (print (result-str result))
          (System/exit 1))
      result)))
