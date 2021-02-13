### Ideas

- DSL for parsing environment variables nested in data structure
  - env reader provides nice, coloured CLI output for user
  - CLI output could be done in enviable.pretty
- CLI tool for asking user for environment variables one by one if not supplied (maybe --cli flag)
  - Perhaps this could be on by default, and you can disable it with an environment variable  
- Aero plugin/adapter
  


### Todo
- [X] Create basic data type to represent an env variable
- [X] Can read config from flat data structure
- [ ] Can read config from nested data structure
- [X] Can specify parser
- [ ] Can collate errors
- [ ] can specify default
- [ ] can specify description
- [ ] can specify optional/mandatory vars (implemented as setting nil as default?)

- [ ] Plan CLI work

- [ ] Plan Aero work
