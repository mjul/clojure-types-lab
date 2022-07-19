# Clojure Types Lab 
## Or how to make the compiler take over more tedious work

Following the virtues of laziness, impatience and hubris known to any Perl programmer: 
the computer should be doing more of your work so you can be "lazy". 

The Clojure compiler is quite lazy: it is not doing much in terms of type checking and
thus puts undue burden on the developer. 

Let's see if we can stretch this Lisp to help us a bit more.

# Deftype Value Objects
Would using `deftype` be useful for value types, e.g. specifically typed FooId BarId for domain objects of type Foo and Bar?

# Typed Clojure
Giving typed Clojure a quick trial.
See [source](src/clojure_types_lab/typed.clj)

## Running the Type Checker
Thanks to the `lein-typed` extension, you can run the type-checker from Leiningen like this:

```
    lein typed check
```

See https://github.com/typedclojure/lein-typed for more information

