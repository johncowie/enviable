(ns enviable.cli
  (:require [enviable.reader :as reader]
            [enviable.output.markdown :as md]))

(defn cli-args->opts [args]
  {:doc? (contains? (set args) "--config.doc")})

(defn configure
  ([vars args]
   (configure vars {:env (System/getenv)} args))
  ([vars base-opts args]
   (let [opts (-> base-opts
                  (merge (cli-args->opts args)))
         r (reader/configure vars opts)]
     (if (reader/error? r)
       (throw (Exception. (if (:doc? opts)
                            (md/doc-str r)
                            (md/result-str r))))
       r))))

