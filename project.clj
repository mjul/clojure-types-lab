(defproject clojure-types-lab "0.1.0-SNAPSHOT"
  :description "Experiments with more typing in Clojure"
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/data.json "2.4.0"]
                 [org.typedclojure/typed.clj.checker "1.0.31"]
                 [org.typedclojure/typed.clj.runtime "1.0.31"]]
  :plugins [[lein-typed "0.4.6"]
            [lein-ancient "1.0.0-RC4-SNAPSHOT"]]
  :core.typed {:check [clojure-types-lab.typed.core clojure-types-lab.typed.foobars]}
  :repl-options {:init-ns clojure-types-lab.typed.core})
 