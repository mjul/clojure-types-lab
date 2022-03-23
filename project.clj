(defproject clojure-types-lab "0.1.0-SNAPSHOT"
  :description "Experiments with more typing in Clojure"
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/data.json "2.4.0"]
                 [org.typedclojure/typed.clj.checker "1.0.19"]
                 [org.typedclojure/typed.clj.runtime "1.0.19"]]
  :plugins [[lein-typed "0.4.6"]]
  :core.typed {:check [clojure-types-lab.typed]}
  :repl-options {:init-ns clojure-types-lab.typed})
 