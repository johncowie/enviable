(ns enviable.cli
  (:require [enviable.reader :as reader]
            [enviable.component :as component]
            [enviable.reporter.markdown :as md]))

(defn cli-args->opts [args]
  {:doc? (contains? (set args) "--config.doc")})

(defn configure
  ([vars args]
   (configure vars {:env      (System/getenv)
                    :reporter md/markdown-reporter} args))
  ([vars base-opts args]
   (let [opts (-> base-opts
                  (merge (cli-args->opts args)))]
     (reader/configure-throw vars opts))))

(defn configure-system
  ([vars args]
   (configure-system vars {:env      (System/getenv)
                           :reporter md/markdown-reporter} args))
  ([vars base-opts args]
   (let [opts (-> base-opts
                  (merge (cli-args->opts args)))]
     (component/configure-system vars opts))))

