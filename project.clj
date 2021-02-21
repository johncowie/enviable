(defproject johncowie/enviable "0.1.0-SNAPSHOT"
  :description "A user-friendly and composable way of configuring clojure apps"
  :url "https://github.com/johncowie/enviable"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :repl-options {:init-ns enviable.core}
  :main nil
  :source-paths ["src"]
  :profiles {:dev {:source-paths ["src" "examples" "test"]
                   :dependencies [[com.stuartsierra/component "1.0.0"]]
                   :aliases      {"example.basic"     ["run" "-m" "enviable.examples.basic"]
                                  "example.component" ["run" "-m" "enviable.examples.component"]}}}
  )
