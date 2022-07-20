# Clojure Types Lab 

When Clojure projects grow they become unwieldy and expensive to maintain. 

Small refactorings take a long time. There is very little help from the compiler, so you end up with lots of preconditions and predicates to check especially function inputs over and over. 

This problem happens earlier for applications with higher domain complexity and later for e.g. small "CRUD"-style 
applications with shallow business logic and a database schema to act as a forcing function to keep everything consistent.

Let us explore some of the options to reduce these problems inside the Clojure ecosystem
before we abandon it altogether.


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


## Documentation for Typed Clojure
Core Typed was the previous incarnation of Typed Clojure. They are quite similar.
The Core Typed documentation here is thus quite useful for Typed Clojure:

https://clojure-doc.org/articles/ecosystem/core_typed/home/

https://clojure-doc.org/articles/ecosystem/core_typed/quick_guide/

https://clojure-doc.org/articles/ecosystem/core_typed/types/
