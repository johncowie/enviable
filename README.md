# enviable

![example workflow](https://github.com/johncowie/enviable/actions/workflows/core.yml/badge.svg)

A user-friendly way of configuring Clojure apps with environment variables.

## Latest version

[![Clojars Project](https://img.shields.io/clojars/v/johncowie/enviable.svg)](https://clojars.org/johncowie/enviable)

## Goals

- An app should document the configuration it requires.
- Useful, user-friendly feedback should be provided for missing configuration.
- The app should only request the config it actually needs.
- Configuration requirements should be composable.
- Configuration should be describable with simple Clojure data.

## Usage

### Defining config

```clojure
(require '[enviable.core :as e])

;; Let's start be defining a very simple config that tries to pull a value from an the environment variable MY_VAR
(def config (e/var "MY_VAR"))

;; When MY_VAR=foo is present in environment...
(e/read-env config) ;; => "foo"

;; When MY_VAR is not present in environment...
(e/read-env config) ;; => An error map explaining that MY_VAR is missing

;; You can verify if the return type is an error using the error? function i.e.
(e/error? (e/read-env config))


;; Just reading one environment variable is probably not going to be much use to you, so you can specify multiple at once!
;; E.g. 
(def config {:server {:host (e/var "HOST")
                      :port (e/var "PORT")}
             :db {:uri (e/var "DB_URI")}})
             
;; Given an environment with HOST=localhost PORT=1234 DB_URI=postgresql://localhost:5432/mydb
(e/read-env config) ;; => {:server {:host "localhost" 
                    ;;              :port "1234"} 
                    ;;     :db {:uri "postgresql://localhost:5432/mydb"}}

;; If any of the values are missing from the environment, then
(e/read-env config) ;; => returns some error data indicating which variables are missing and present

```

TODO 
- `parse-with`
- `default-to`
- `is-optional`
- `describe`

### Types
TODO
- `int-var`
- `double-var`
- `bool-var`

### CLI Output
TODO
- `cli/read-env`

### Configuring Components
TODO
- `Configurable / configuration`
- `configure-system`

## Examples
TODO
- `lein examples.basic`
- `lein examples.component`

## Contributing
TODO

### License

Copyright © 2021 John Cowie

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
