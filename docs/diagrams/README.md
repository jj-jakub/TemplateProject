# Architecture diagram sources

Each `*.mmd` file here is the [Mermaid](https://mermaid.js.org) source for one diagram. Render it to a
`*.png` beside the source and embed **that** in `ARCHITECTURE.md`, so the diagrams show up in any
Markdown viewer, not only in one with a Mermaid plugin.

Keeping the source beside the image is the point: a diagram whose source was lost is a diagram nobody
will ever update, and it drifts from the code silently.

To re-render after editing a `.mmd` (needs Node and a local Chrome):

```
npx @mermaid-js/mermaid-cli -i module-graph.mmd -o module-graph.png \
  -t default -b white -s 3 \
  -p '{"executablePath":"/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"}'
```

`-s 3` renders at 3x for crisp text; `-b white` gives a light card that stays readable in both light
and dark viewers.
