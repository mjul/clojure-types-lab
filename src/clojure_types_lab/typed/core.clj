(ns clojure-types-lab.typed.core
  {:lang :core.typed}
  (:require [typed.clojure :as t]
            [typed.clojure.jvm :as tjvm]
            [clojure-types-lab.typed.foobars :as fb])
  (:import [java.util Currency]))


;; t/ann adds an annotation to a var
;; We can type-check a function by annotating it this way:
(t/ann welcome-string [t/Str :-> t/Str])
(defn welcome-string [a-name]
  (str "Welcome, " a-name))

;; The type-checker checks that it conforms. It will fail if we
;; e.g. declare that it must return an integer:
;;
;;   (t/ann welcome-string [t/Str :-> t/Int])
;;
;;; Type Error (...\clojure_types_lab\typed.clj:11:3)
;;; Type mismatch:
;;; 
;;Expected: 	typed.clojure/Int
;;
;;Actual: 	typed.clojure/Str
;;;
;;;
;;in:
;;(str "Welcome, " a-name)


;; We can also annotate with wildcars, t/Any.
;; This is useful for the idiomatic predicates that pervade Clojure code.
;; See below for how we might refine this 
(t/ann positive-int? [t/Any -> t/Bool])
(defn positive-int? [x]
  (and (number? x)
       ;; For some reason if we don't have the number? above, the type-checker 
       ;; does not know that x is a t/Number so it will fail the type-check on 
       ;; (pos? ^int x) below.
       ;; The annotation for int? does not provide that information.
       ;; (Typed Clojure 1.0.31)
       (int? x)
       (pos? ^int x)))

;; We can even tell the type system that if the predicate is true
;; the argument x is a number. This is called occurrence typing.
;; It uses something called latent filters in Typed Clojure:
;; 0 is the first argument
(t/ann positive-int? [t/Any :-> t/Bool
                      :filters {:then (is Number 0)
                                ;; :else branch not defined
                                }])

;; We can use t/print-env to inspect the type-checker's environment
;; at any given point when compiling. 
;; This is useful to troubleshoot problems.
(t/ann add-type-information-from-pre [t/Any :-> t/Num])
(defn add-type-information-from-pre [x]
  {:pre [(positive-int? x)]}
  ;; If you uncomment this print-env you will get the message below when the checker runs
  ;; (t/print-env "The type-checker environment here is: ")
  ;;;; 
  ;;;; The type-checker environment here is: 
  ;;;; {:env {x__#0 Number}, :props ((is typed.clojure/Num x__#0)), :aliases {}}
  ;;;;
  ;; Here, you can see that the type-checker uses the pre-condition contract
  ;; to deduce that x is a number inside the function body. Pretty cool.
  (inc x))


;; Now that we know about the latent filters, we can do something simpler
;; In the common case of predicate functions, we can annotate them with
;; the shorter t/Pred.

;; Note that :filters don't compose well with `and` so we have do use ^:no-check
;; (this is also the case if we wrote out the predicate signature with latent filters like above)
(t/defalias PartyId Long)
(t/ann ^:no-check party-id? (t/Pred PartyId))

(defn party-id? [x]
  (and (instance? Long x)
       (pos? x)))

;; We can return a fixed map structure by specifying the keys and their values
(t/ann party [t/Int t/Str :-> '{:id t/Int :name t/Str}])
(defn party [id name]
  {:pre [(party-id? id)]}
  {:id id, :name name})

;; It would be convenient to define aliases for e.g. the Party map above.
;; We can do this with defalias:
(t/defalias Party '{:id t/Int :name t/Str})

;; Heterogeneous maps are maps with a minimum set of defined keys
;; E.g. if the parties to a contract are at least a buyer and a seller we can define it like this
(t/ann parties [Party Party
                :-> (t/HMap :mandatory {:buyer Party :seller Party})])
(defn parties
  [buyer seller]
  {:buyer buyer :seller seller})


;; You can ask the type-checker to output debug information by adding 
;; meta-data to a form (^::t/dbg ):
(t/ann debug-info [t/Int :-> t/Num])
(defn debug-info
  [x]
  ^::t/dbg (+ 1 x))


;; We can annotate functions with multiple arities like this
(t/ann sum (t/IFn
            [:-> t/Int]
            [t/Int :-> t/Int]
            [t/Int t/Int :-> t/Int]
            [t/Int t/Int t/Int * :-> t/Int]))
(defn sum
  ([] 0)
  ([x] x)
  ([x y] (+ x y))
  ([x y & more]
   ;; If we write it like this with reduce the type-checker will be happy (see typed_issues.clj for a counter-example))
   (reduce sum (sum x y) more)))


;;
;; Java (JVM) interop
;;

;; The type-checker assumes that reference types may be null when analyzing type signatures for JVM code.
;; For example, consider the Java method
;; 
;; public String getFoo() { return "foo"; }
;; 
;; It has the signature [:-> (t/U String nil)]
;;
;; When we know that it cannot return null we can annotate it with "non-nil-return"
;;

;; For example, we get this problem with currencies
;; (t/cf (java.util.Currency/getInstance "CHF"))
;; ;=> (t/U nil java.util.Currency)
;;
;; This method raises returns a non-nil instance or raises an exception.
;; We can eliminate the nil-case like this:
;; (:all means all overloads)
;; 
;; Note: specify the fully qualified symbol, not the shorter form created by :import in the ns form.
(tjvm/non-nil-return java.util.Currency/getInstance :all)

;; Note that we have to type out is its fully qualified name
(t/ann get-francs [:-> java.util.Currency])

(defn get-francs
  []
   (Currency/getInstance "CHF"))


;;(t/ann-record Company [id :- t/UUID, contract-id :- t/Str, name :- t/Str])
;;(defrecord Contract [id contract-id name])



;; ----------------------------------------------------------------
;; We can use annotations from another namespace
;;
;; BUT we need to tell the type-checker to from which namespaces to load annotations. 
;;
;; This is done in the config file:   typedclojure_config.clj


(t/ann foobar [fb/Foo fb/Bar :-> '{:foobar t/Str}])
(defn foobar [x y]
  {:foobar (str (:foo x) (:bar y))})
