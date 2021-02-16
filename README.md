# enviable

A user-friendly way of configuring Clojure apps with environment variables.

## Goals

- An app should document the configuration it requires.
- Useful, user-friendly feedback should be provided for missing configuration.
- The app should only request the config it actually needs.
- Configuration requirements should be composable.
- Configuration should be describable with simple Clojure data.

## Usage

### Defining config
- `var`
- `read-env`
- `parse-with`
- `default-to`
- `is-optional`
- `describe`
- `error?`

### Types
- `int-var`
- `double-var`
- `bool-var`

### CLI Output
- `cli/read-env`

### Configuring Components
- `Configurable / configuration`
- `configure-system`

## Examples
- `lein examples.basic`
- `lein examples.component

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
