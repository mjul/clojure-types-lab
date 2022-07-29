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

# The Problem with Tagged Values 
A common Lisp idiom to avoid [Primitive Obsession](https://wiki.c2.com/?PrimitiveObsession) is by
using tagged values (wrapping primitives in maps that also include a type specifier), *e.g.* `{:tag ::foo-id :value 42}` and `{:tag ::bar-id :value 42}`.

Tagged values are well suited for multimethod dispatch, the primitive Clojure compiler cannot provide much help for these.

# Deftype Value Objects
Would using `deftype` be useful for value types, *e.g.* specifically typed FooId and BarId for IDs for domain objects of type Foo and Bar?

We explore this in [deftypes.clj](src/clojure_types_lab/deftypes.clj) and the associated [test](test/clojure_types_lab/deftypes_test.clj).

# Typed Clojure
In lieu of compiler support, we may use the Typed Clojure library to assist with type checking 
and possibly gain benefits from this.

See [source](src/clojure_types_lab/typed/core.clj)


## Running the Type Checker
Thanks to the `lein-typed` extension, you can run the type-checker from Leiningen like this:

```
    lein typed check
```

See https://github.com/typedclojure/lein-typed for more information

In the REPL, you can run the type-checker on the current namespace:

```clojure
    (t/cns)
```

You can get the type of a single form like this:

```clojure
    (t/cf (fn [x] (str x)))
    ;=> [[t/Any -> t/Str] {:then tt, :else ff}]
```

Or check it against a specific type like this:

```clojure
    (t/cf (fn [x] (str x)) (t/IFn [t/Any :-> t/Int]))
    ;=> [[t/Any -> t/Str] {:then tt, :else ff}]
```

## Type Annotations for Clojure Code

## Type Annotations for Java (JVM) interop

This is also covered. 

You may need to clarify whether methods may or may not return nil (null) for reference types
using the `non-nil-return` annotation in the `typed.clojure.jvm` namespace.

See [source](src/clojure_types_lab/typed/core.clj)

## Typing multiple files

You must add a `typedclojure_config.clj` file (or `.cljc` or `.cljs` as applicable) in the root of
your source tree (or in a resources folder) specifying where to find the type annotations.

You will see the type-checker output where it is reading annotations and extensions when it is loading,
e.g. `lein typed check` gives this output:

```
Registering annotations from typed.ann.clojure...
Registering annotations from clojure-types-lab.typed.core...
Registering annotations from clojure-types-lab.typed.foobars...
Registering annotations from typed.ann.clojure...
Registering annotations from clojure-types-lab.typed.core...
Registering annotations from clojure-types-lab.typed.foobars...
Start checking clojure-types-lab.typed.core
```

See the config here: [typedclojure_config.clj](src/typedclojure_config.clj).


## Typed Clojure Issues

See the [issues.clj](src/clojure_types_lab/typed/issues.clj) for various 
problems related to Typed Clojure.

## Documentation for Typed Clojure

Unfortunately, the documentation for Typed Clojure is quite sparse.

You can find the API documentation here: https://api.typedclojure.org/latest/typed.clj.runtime/index.html

In the REPL, you can look up documentation like for any fragment like this:

```clojure
    (t/doc-clj t/HVec)
```

### Example Type Annotations

Typed Clojure includes annotations for most of the standard Clojure core library.
You can find the annotations in `typed.ann.clojure.core`. 

In the [Typed Clojure repository](https://github.com/typedclojure/typedclojure)
it is located in the file `typed/lib.clojure/src/typed/ann/clojure.cljc`

There are also some small examples in the `example-projects` directory.


### Core Typed Docs
Core Typed was the previous incarnation of Typed Clojure. They are quite similar.
The Core Typed documentation here is thus quite useful for Typed Clojure:

https://clojure-doc.org/articles/ecosystem/core_typed/home/

https://clojure-doc.org/articles/ecosystem/core_typed/quick_guide/

https://clojure-doc.org/articles/ecosystem/core_typed/types/


### Differences from Core Typed

- Function return values are indicated by `:->`
- JVM interop features are now in the `typed.clojure.jvm` namespace, e.g. `non-nil-return`.
