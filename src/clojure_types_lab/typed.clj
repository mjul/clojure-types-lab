(ns clojure-types-lab.typed
  {:lang :core.typed}
  (:require [clojure.core.typed :as t]))


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
(t/ann positive-int? [t/Any -> t/Bool
                      :filters {:then (is Number 0)
                                ;; :else branch not defined
                                }])

;; We can use t/print-env to inspect the type-checker's environment
;; at any given point when compiling. 
;; This is useful to troubleshoot problems.
(defn add-type-information-from-pre [x]
  {:pre [(positive-int? x)]}
  (t/print-env "The type-checker environment here is: ")
  ;; When you compile you get this message from print-env:
  ;;;; 
  ;;;; The type-checker environment here is: 
  ;;;; {:env {x__#0 Number}, :props ((is typed.clojure/Num x__#0)), :aliases {}}
  ;;;;
  ;; Here, you can see that the type-checker uses the pre-condition contract
  ;; to deduce that x is a number inside the function body. Pretty cool.
  (inc x))


;; Let's try this latent filter in action:
(t/ann party-id? [t/Any :-> t/Bool :filters {:then (is Number 0)}])
(defn party-id? [x]
  (and (number? x)
       (int? x)
       (pos? ^int x)))



;; We can return a fixed map structure by specifying the keys and their values
(t/ann party [t/Int t/Str :-> '{:id t/Int :name t/Str}])
(defn party [id name]
  {:pre [(party-id? id)]}
  {:id id, :name name})


;; Heterogeneous maps are maps with a minimum set of defined keys
;; E.g. if the parties to a contract are at least a buyer and a seller we can define it like this
;;(t/ann parties [Party Party :-> (t/HMap :mandatory {:buyer Party :seller Party})])
#_(defn parties
  [buyer seller]
  {:buyer buyer :seller seller})

;; We can define a type alias for the Parties data structure like this to make it reusable

;;(t/ann-record Company [id :- t/UUID, contract-id :- t/Str, name :- t/Str])
;;(defrecord Contract [id contract-id name])






(defn numerical-tests
  [x]
  ;; juxt is not annotated in Typed Clojure 1.0.31, so the below will not check
  #_(juxt (partial identical? 1)
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
        double?) x)



;;


(comment

  (type (int 32))
  (t/install)

  (t/check-ns)
  (t/cf '(clojure.core.typed/rclass-pred :a :b))
  (t/cf '(juxt :a :b {:a 1 :b 2 :c 3}))
  
  )