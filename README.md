Convert leiningen dependencies format to tools.deps preserving order, comments and whitespace (i.e. grouping).

```
› clj -M -m lein2deps /path/to/your/project.clj
```

Spits a map of deps for each `:dependencies` section in project.clj. Indentation might be off, use your editor to re-align.
