# scala-pkg-deps
Hacky tool for inferring Scala projects' intra-project, inter-package dependencies and rendering them to DOT graphs.

[![Build Status](https://travis-ci.org/ryan-williams/scala-pkg-deps.svg?branch=master)](https://travis-ci.org/ryan-williams/scala-pkg-deps)
[![Coverage Status](https://coveralls.io/repos/github/ryan-williams/scala-pkg-deps/badge.svg?branch=master)](https://coveralls.io/github/ryan-williams/scala-pkg-deps?branch=master)

## Usage

```bash
git clone git@github.com:ryan-williams/scala-pkg-deps.git
cd scala-pkg-deps
sbt pack
target/pack/bin/inter-package-deps-to-dot <path-to-analyze>  # Outputs DOT to stdout.
target/pack/bin/inter-package-deps-to-dot <path-to-analyze> <out-file.dot>
```

## Examples

[`src/test/resources/guac-readsets`](src/test/resources/guac-readsets) has a small example taken from [hammerlab/guacamole](https://github.com/hammerlab/guacamole).

Here is a rendered dot-graph on all of [hammerlab/guacamole](https://github.com/hammerlab/guacamole):

![](https://d3vv6lp55qjaqc.cloudfront.net/items/15300m2y3a2g0c0m0P3N/download%20(2).png?X-CloudApp-Visitor-Id=486740)

## Caveats

This tool uses a couple of simple regexs to try to find `package` declarations and `import` statements; as a result, it may produce incorrect results in arbitrary ways.
