### Ideas

- DSL for parsing environment variables nested in data structure
  - env reader provides nice, coloured CLI output for user
  - CLI output could be done in enviable.pretty
- CLI tool for asking user for environment variables one by one if not supplied (maybe --cli flag)
  - Perhaps this could be on by default, and you can disable it with an environment variable  
- Aero plugin/adapter
- Hook into component system - add 'configure' method to component protocol, so part of the components startup is configurating its self - then there could be a custom 'configure-system' function
- Can load from a different source than just env vars
- Can obscure sensitive config vals

### Releases
- Setup github actions to run tests
- Create clojars deployment key for github
- Add deployment key to github secrets
- Create script/function that: 
  - when commit is tagged with release number that:
    - bumps snapshot to release number in project.clj (https://cljdoc.org/d/leiningen/leiningen/2.9.4/api/leiningen.change)
    - deploys to clojars
    - bumps to next snapshot release
  - when commit isn't tagged
    - deploys to clojars with snapshot version

