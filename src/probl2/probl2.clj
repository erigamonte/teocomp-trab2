(ns probl2.probl2
  (:require [clojure.string :as str]
            [clojure.core.match :refer (match)]))

(declare ast-decoder)

(defn update-var
  [r x]
  (dosync
    (ref-set r x)))

(defn funcall-if
  [var-name test arg1 arg2]
  (def return {:expr :assign
                      :lhs  var-name
                      :rhs  {:expr :funcall :name "IF"
                             :args (seq [test arg1 arg2])}})
  return)

(defn update-ast-as-used
  [S x]
  (def ast-filtred
    (for [i S] (let []
                  (cond (=
                    (get i :name)
                    (get x :name)) (let []
                                     (println i)
                                     (assoc i :used true))
                  :else i))))
    ast-filtred)

(defn ast-decoder
  ([x] (ast-decoder x "" 0 false))
  ([x sufix counter internal]
   (match [x]
          [(list :guard list?)] (let []
                                  (map #(ast-decoder % sufix counter internal) list))
          [{:expr :input :index index}] [x x]
          [{:expr :output :index index}] (cond
                                           (= internal true)
                                            [{:expr :var :name (str "y" index sufix counter)}
                                             (assoc x :name (str "y" index sufix)
                                                      :oldname (str "y" index)
                                                      :sufix sufix
                                                      :count counter
                                                      :old-format x
                                                      :new-format (assoc x :name (str "y" index sufix)))]
                                           :else [x x])
          [{:expr :var :name name}] (cond
                                      (= internal true)
                                        [(assoc x :name (str name counter sufix))
                                            (assoc x :name (str name counter sufix)
                                              :oldname name
                                              :sufix sufix
                                              :count counter
                                              :old-format x
                                              :new-format (assoc x :name (str name counter sufix)))
                                          ]
                                      :else [x x])
          [{:expr :funcall :name _ :args args }] (let [args-part (ast-decoder args sufix counter internal)
                                                       args-first (map first args-part)
                                                       args-second (map second args-part)]
                                                   [(assoc x :args args-first) args-second])
          [{:expr :assign :lhs lhs :rhs rhs}] (let [lhs-part (ast-decoder lhs sufix counter internal)
                                                    rhs-part (ast-decoder rhs sufix counter internal)]
                                                [(assoc x :lhs (first lhs-part) :rhs (first rhs-part))
                                                 (second lhs-part)])
          [{:expr :if :cond test :then t :else e }]
          (let [body (ref [])
                then (ast-decoder t (str sufix "$then") (inc counter) true)
                else (ast-decoder e (str sufix "$else") (inc counter) true)
                then-part (map first then)
                else-part (map first else)
                lhs-then-variaveis (ref (map second then))
                lhs-else-variaveis (ref (map second else))]
            (println "T: " t)
            (println "E: " e)
            (println "THEN:" then)
            (println "ELSE:" else)
            (println "THEN-PART:" then-part)
            (println "ELSE-PART:" else-part)
            (println "LHS-THEN:" @lhs-then-variaveis)
            (println "LHS-ELSE:" @lhs-else-variaveis)
            (update-var body (into [] (flatten (conj [] then-part else-part))))
            (println "BODY: " @body)
            (dotimes [i (count @lhs-then-variaveis)]
              (println "1-")
              (dotimes [j (count @lhs-else-variaveis)]
                (cond
                  (= (get (nth @lhs-then-variaveis i) :oldname) (get (nth @lhs-else-variaveis j) :oldname))
                    (let []
                      (println "1--")
                      (update-var body (conj @body (funcall-if (get (nth @lhs-then-variaveis i) :old-format) test (get (nth @lhs-then-variaveis i) :new-format) (get (nth @lhs-else-variaveis j) :new-format))))
                      (update-var lhs-then-variaveis (update-ast-as-used @lhs-then-variaveis (nth @lhs-then-variaveis i)))
                      (update-var lhs-else-variaveis (update-ast-as-used @lhs-else-variaveis (nth @lhs-else-variaveis j)))
                      (println "1--")
                    ))))
              (dotimes [i (count @lhs-then-variaveis)]
                (println "2")
                (cond
                  (not= (get (nth @lhs-then-variaveis i) :used) true)
                  (let []
                    (println "2--")
                    (update-var body (conj @body (funcall-if (get (nth @lhs-then-variaveis i) :old-format) test (get (nth @lhs-then-variaveis i) :new-format) (get (nth @lhs-then-variaveis i) :old-format))))
                    (update-var lhs-then-variaveis (update-ast-as-used @lhs-then-variaveis (nth @lhs-then-variaveis i)))
                    (println "2--")
                    )))
              (dotimes [i (count @lhs-else-variaveis)]
                (println "3")
                (cond
                  (not= (get (nth @lhs-else-variaveis i) :used) true)
                  (let []
                    (println "3--")
                    (update-var body (conj @body (funcall-if (get (nth @lhs-else-variaveis i) :old-format) test (get (nth @lhs-else-variaveis i) :new-format) (get (nth @lhs-else-variaveis i) :old-format))))
                    (update-var lhs-else-variaveis (update-ast-as-used @lhs-else-variaveis (nth @lhs-else-variaveis i)))
                    (println "3--")
                    )))
            [(seq @body) (seq @body)]))))

(defn str-ast-decoder
  [x] (map first (ast-decoder (read-string x))))