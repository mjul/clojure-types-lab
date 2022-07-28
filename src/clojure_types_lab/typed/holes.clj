(ns clojure-types-lab.typed.holes
  {:lang :core.typed}
  (:require [typed.clojure :as t]
            [clojure.core.typed.hole :as thole]))


;; Let's work with "holes", a gradual typing technique for unwritten code where we don't know the types yet.
;; If you know Idris this will be familiar to you.
;; At runtime, both (silent-hole) and (noisy-hole) are similar to (assert false)
;; Type-check time, they are are slightly different:

(t/ann todo-works-for-int-only [t/Any :-> t/Int])
(defn todo-works-for-int-only [x]
  (cond (int? x)
        (* 42 x)
        :else
        ;; The type-checker will ignore this branch since it is a silent "hole"
        (thole/silent-hole)))

(t/ann todo-works-for-str-only [t/Any :-> t/Str])
(defn todo-works-for-str-only [x]
  (cond (string? x) x 
        ;; The type-checker will fail on this branch since it is a noisy "hole"
        :else (thole/noisy-hole)))

;;; Error message:
;;;; Type mismatch:
;;;
;;;Expected: 	t/Str
;;;
;;;Actual: 	clojure.core.typed.hole.NoisyHole
;;;; in:
;;;; (thole/noisy-hole)


(defn foo [x]
  {:t1 (todo-works-for-int-only x) :t2 (todo-works-for-str-only x)})

