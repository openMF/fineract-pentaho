# TODO

1. move Java package name (back, again); see [FINERACT-1174](https://issues.apache.org/jira/browse/FINERACT-1174)

1. how to avoid `files("../")` ?  We would have to have an `api` artifact, and publish it..
   watch [FINERACT-1171](https://issues.apache.org/jira/browse/FINERACT-1171) - DONE

1. inherit dependency management from Fineract, so that e.g.
   JAX RS & commons lang versions don't have to be repeated.
   Actually, perhaps JAX RS & commons lang should be considered
   part of a (future..) Fineract Plugin API, and not need to be declared at all!

1. run `ClasspathDuplicateTest`, on all such plugins

1. run usual Fineract code style checks against plugin code like this

1. build standalone "service", not just "plugin"

1. make hard-coded `~/.mifosx/pentahoReports` configurable? - DONE
