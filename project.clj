(defproject johncowie/enviable "0.2.1-SNAPSHOT"
  :description "A user-friendly and composable way of configuring clojure apps"
  :url "https://github.com/johncowie/enviable"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]]
  :repl-options {:init-ns enviable.core}
  :main nil
  :source-paths ["src"]
  :profiles {:dev {:source-paths ["src" "examples" "dev" "test"]
                   :dependencies [[com.stuartsierra/component "1.0.0"]
                                  [pjstadig/humane-test-output "0.11.0"]
                                  [leiningen "2.9.5"]]
                   :injections   [(require 'pjstadig.humane-test-output)
                                  (pjstadig.humane-test-output/activate!)]
                   :aliases      {"set-version"       ["run" "-m" "version/set-version"]
                                  "next-snapshot"     ["run" "-m" "version/next-snapshot"]
                                  "current-version"   ["run" "-m" "version/current-version"]
                                  "example.basic"     ["run" "-m" "enviable.examples.basic"]
                                  "example.component" ["run" "-m" "enviable.examples.component"]
                                  "example.other-sources" ["run" "-m" "enviable.examples.other-sources"]}}}

  :repositories {"releases" {:url           "https://repo.clojars.org"
                             :username      :env/deploy_username
                             :password      :env/deploy_token
                             :sign-releases false}}
  )
