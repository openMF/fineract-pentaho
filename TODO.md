# TODO

1. make hard-coded `~/.mifosx/pentahoReports` configurable?

1. inherit dependency management from Fineract, so that e.g.
   JAX RS & commons lang versions don't have to be repeated.
   Actually, perhaps JAX RS & commons lang should be considered
   part of a (future..) Fineract Plugin API, and not need to be declared at all!

1. run ClasspathDuplicateTest, on all plugins

1. run Fineract code style checks against plugin code like this

1. how to avoid files("../") ?

1. build standalone "service", not just "plugin"

