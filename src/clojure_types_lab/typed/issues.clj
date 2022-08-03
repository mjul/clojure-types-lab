(ns clojure-types-lab.typed.issues
  {:lang :core.typed}
  (:require
   [clojure.set :as set]
   [typed.clojure :as t])
  (:import [java.util Currency]))


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

;; ----------------------------------------------------------------
;; Combining latent filters for predicates in and form is 
;; currently weaker than necessary: (remove ^:no-check and type-check to see the error)

(t/defalias PartyId Long)
(t/ann ^:no-check party-id? (t/Pred PartyId))
(defn party-id? [x]
  ^::t/dbg
  (and
   ^::t/dbg
   (instance? Long x)
   ^::t/dbg
   (pos-int? x)))

;; The type-checker says this function has the type
;; => [(Var
;;      [t/Any -> Boolean :filters {:then (is Long 0), :else tt}]
;;      [t/Any -> Boolean :filters {:then (is Long 0), :else tt}])
;;     {:then tt, :else ff}]

;; Somehow it does not capture that the fact that we know that :else (! Long x) after the (instance? ...) form.
;; So the :else filter becomes :tt instead of narrowing down the filter to :else (! Long 0)

;; The debug tagging prints this:

;;  ::t/dbg id=G__52873 (and (instance? Long x) (pos-int? x))
;;  ::t/dbg id=G__52873 expected: Boolean
;;   ::t/dbg id=G__52874 (instance? Long x)
;;   ::t/dbg id=G__52874 result: [(t/U false true) {:then (is Long x__#0), :else (! Long x__#0)}]
;;   ::t/dbg id=G__52875 (pos-int? x)
;;   ::t/dbg id=G__52875 expected: Boolean
;;   ::t/dbg id=G__52875 result: [Boolean {:then (is (t/U Short Byte Long Integer) x__#0), :else tt}]
;;  ::t/dbg id=G__52873 result: [Boolean {:then (is Long x__#0), :else tt}]
;;  ; Type Error (file:/C:/Users/marti/src/github/mjul/clojure-types-lab/src/clojure_types_lab/typed_issues.clj:77:1) 
;;  ; Expected result with filter {:then (is clojure-types-lab.typed-issues/PartyId x__#0), :else (! clojure-types-lab.typed-issues/PartyId x__#0)}, got filter {:then (is Long x__#0), :else tt}
;;  ; 
;;  ; 
;;  in:
;;  (fn* ([x] (and (instance? Long x) (pos-int? x))))


;; ----------------------------------------------------------------
;; Multiple arities can make the type-checker fail:


;; We can annotate functions with multiple arities like this
(t/ann ^:no-check sum (t/IFn
                       [:-> t/Int]
                       [t/Int :-> t/Int]
                       [t/Int t/Int :-> t/Int]
                       [t/Int t/Int * :-> t/Int]))
(defn sum
  ([] 0)
  ([x] x)
  ([x y] (+ x y))
  ;; The apply sum breaks the type-checker (it can be rewritten with reduce sum to make it work)
  ([x y & more] (+ x y (apply sum more))))


;; The type-checker does not support that construct yet, so we get an error:

;; Type Error (file:...typed_issues.clj:127:24) 
;; core.typed Not Yet Implemented Error:(file:...typed_issues.clj:127:24) "NYI HSequential inference " (t/HSeq [t/Int *]) (t/HSequential [z ... z])


;; ----------------------------------------------------------------

;; Annotations do not understand imports: class names must be fully qualified
;;
;; Note:
;; java.util.Currency has been imported above

(comment

  ;; Referencing the imported class via the imported name:
  (macroexpand '(t/ann get-francs [:-> Currency]))

  ;; result v- the second parameter to ann* is the type, you can see
  ;; it lost its namespace
  (do
    (clojure.core.typed/ann*
     'clojure-types-lab.typed.issues/get-francs
     '[:-> Currency]
     'true
     '(clojure.core.typed/ann get-francs [:-> Currency])))

  ;; 

  (t/ann get-francs [:-> Currency])
  (t/cf (Currency/getAvailableCurrencies))

  ;; Referencing the class via its fully qualified name:
  (macroexpand '(t/ann get-francs [:-> java.util.Currency]))

  ;; Result - we keep the namespace 
  (do
    (clojure.core.typed/ann*
     'clojure-types-lab.typed.issues/get-francs
     '[:-> java.util.Currency]
     'true
     '(clojure.core.typed/ann get-francs [:-> java.util.Currency])))


  ;; You can also use the reader to expand the import with a back-quote

  (macroexpand '(t/ann get-francs [:-> ~`Currency]))

  ;; Result
  (do
    (clojure.core.typed/ann*
     'clojure-types-lab.typed.issues/get-francs
     '[:-> 'java.util.Currency]
     'true
     '(clojure.core.typed/ann get-francs [:-> 'java.util.Currency])))

  ;;
  )



;; ----------------------------------------------------------------

(comment
  ;; ann-record expects (symbol :- type) not (:keyword :- type) 
  ;; This causes the type-checker to raise an assertion exception
  ;; 
  (t/ann-record Baz [:baz :- t/Keyword])

  ; Execution error (AssertionError) at typed.cljc.checker.type-rep/DataType-maker (type_rep.clj:305).
  ; Assert failed: ((con/array-map-c? symbol? (some-fn Scope? Type?)) fields)

  ;; typed.cljc.checker.type-rep/DataType-maker (type_rep.clj:305)
  ;; typed.cljc.checker.type-ctors/DataType* (type_ctors.clj:626)
  ;; typed.cljc.checker.type-ctors/DataType* (type_ctors.clj:618)
  ;; ...

  )

;; ----------------------------------------------------------------

;; Records have a problem with dashed namespace names (and the corresponding underscored directory names)

;; You cannot reference the record type with the namespace alias
;; (t/defalias DashedBaz fb/Baz)

;; Also, you cannot reference the record type with fully qualified namespace name for the record
;; (Perhaps because it is not a Var)
;; (t/defalias DashedBaz clojure-types-lab.typed.foobars.Baz)

;; However, you can refer to its JVM class name where the dashes are underscores:
(t/defalias DashedBaz clojure_types_lab.typed.foobars.Baz)

;; ----------------------------------------------------------------

;; You can make ann-record crash by omitting the record name 
;; It also has some other quirks

(comment
  ;; Correct usage:
  (t/ann-record Foo [name :- tc/Str])

  ;; Missing record name
  (t/ann-record #_Foo [name :- tc/Str])

  ;;=>
  ;;  ; Execution error (AssertionError) at clojure.core.typed.current-impl/gen-datatype* (current_impl.cljc:567).
  ;;  ; Assert failed: (impl-case :clojure (simple-symbol? provided-name) :cljs (qualified-symbol? provided-name))
  ;;  clojure.core.typed.current-impl/gen-datatype* (current_impl.cljc:567)
  ;;  clojure.lang.Var/invoke (Var.java:424)
  ;;  clojure.core.typed/ann-record* (typed.clj:928)
  ;;  clojure.core.typed.current-impl/with-clojure-impl* (current_impl.cljc:297)
  ;;  clojure.core/apply (core.clj:667)
  ;;  clojure.core/with-bindings* (core.clj:1990)
  ;;  clojure.core.typed.current-impl/with-clojure-impl* (current_impl.cljc:296)
  ;;  clojure.core.typed.current-impl/with-clojure-impl* (current_impl.cljc:295)
  ;;  clojure.lang.Var/invoke (Var.java:384)
  ;;  clojure.core.typed/ann-record* (typed.clj:927)
  ;;  clojure.core.typed/ann-record* (typed.clj:926)
  ;;  clojure.core.typed/ann-record* (typed.clj:910)
  ;;  clojure-types-lab.typed.issues/eval41412 (form-init2898751586910906665.clj:233)
  ;;  clojure.lang.Compiler/eval (Compiler.java:7194)


  ;; Other examples with bad syntax that make it crash

  ;; surprisingly, this is accepted with a keyword instead of a symbol field name
  (t/ann-record Foo [:name :- tc/Str])
  ;;=> nil

  ;; surprisingly, this is accepted with a return arrow instead of :-
  (t/ann-record Foo [name :-> tc/Str])
  ;;=> nil

  ;; surprisingly, if the :- is missing, it is also accepted:
  (t/ann-record Foo [name #_:- tc/Str])
  ;;=> nil

  )


;; ----------------------------------------------------------------

;; unparse-type uses the "old" arrow notation from Core Typed (->), not the new one (:->)

(comment
  (t/ann add-2 [t/Int t/Int :-> t/Int])
  (t/cf (defn add-2 [x] 1) add-2)
  ;;=>
  ;; ; Type Error (c:\Users\marti\src\github\mjul\clojure-types-lab\.calva\output-window\output.calva-repl:160:7) 
  ;; ; No matching arities: [t/Int t/Int -> t/Int]
  ;; ; 
  ;; ; 
  ;; in:
  ;; ([x] 1)
  )

;; ----------------------------------------------------------------

;; sort-by does not understand the Comparable<T> interface

(t/defalias Interval '{:start java.time.Instant :end java.time.Instant})
(t/defn interval-start [x :- Interval] :- java.time.Instant
        (:start x))

^::t/ignore
(t/defn sort-intervals-by-start [xs :- (t/Coll Interval)] :- (t/ASeq java.time.Instant)
        (sort-by interval-start xs))

;; If you remove ^::tc/ignore above and check the types, you get this error
;; even if sort-by works since java.time.Instant implements Comparable<Instant>

;;  ; Type Error (file:/C:/Users/marti/src/github/mjul/clojure-types-lab/src/clojure_types_lab/typed/issues.clj:297:9) 
;;  ; Polymorphic function sort-by could not be applied to arguments:
;;  ; Polymorphic Variables:
;;  	a
;;  
;;  Domains:
;;  	[a -> Number] (t/Seqable a)
;;  
;;  Arguments:
;;  	[clojure-types-lab.typed.issues/Interval -> java.time.Instant] (IPersistentCollection clojure-types-lab.typed.issues/Interval)
;;  
;;  Ranges:
;;  	(t/ASeq a)
;;  
;;  with expected type:
;;  	(t/ASeq java.time.Instant)
;;  
;;  
;;  ; 
;;  ; 
;;  in:
;;  (sort-by interval-start xs)


;; ----------------------------------------------------------------
