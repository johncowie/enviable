(ns enviable.cli.ansi)

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
