(ns clojure-types-lab.typed-issues
  {:lang :core.typed}
  (:require 
   [clojure.set :as set]
   [typed.clojure :as t]))


;;; Issues and interesting-looking snippets related to Typed Clojure below:

(comment
  (t/cf
   (defn numerical-tests
     [x]
     ;; juxt is not annotated in Typed Clojure 1.0.31, so the below will not check
     (juxt (partial identical? 1)
           zero?
           pos?
           neg?
           even?
           odd?
           number?
        ;;ratio?
        ;;rational?
           integer?
           int?
           pos-int?
           nat-int?
           decimal?
        ;;float?
           double?) x))

  ;
  )



(comment

  ;; This looks like it could be reduced (Typed Clojure 1.0.31)

  (t/cf (defn foo? [x]
          (and (map? x)
               (set/subset? #{:id :foo} (set (keys x))))))

  ;=> 
  ; [(Var
  ;  [t/Any -> Boolean :filters {:then (is (t/Map t/Any t/Any) 0), :else tt}]
  ;  [t/Any -> Boolean :filters {:then (is (t/Map t/Any t/Any) 0), :else tt}])
  ; {:then tt, :else ff}]

  )


(comment
  
  ;; clojure.core/tree-seq is not annotated:
  (t/cf (tree-seq map?
                  :children
                  {:tag :root :value 1
                   :children [{:tag :child :value :c1} {:tag :child :value :c2}]}
                  :children))

  ;=>
  ; ; Type Error (c:\Users\marti\src\github\mjul\clojure-types-lab\src\clojure_types_lab\typed_issues.clj:56:9) 
  ; ; Unannotated var clojure.core/tree-seq
  ; ; 
  ; ; 
  ; in:
  ; tree-seq

  )