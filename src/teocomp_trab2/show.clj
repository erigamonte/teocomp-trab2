(ns teocomp-trab2.show
  (:require [clojure.string :as str]
            [clojure.core.match :refer (match)]))

(declare str-expr str-define str-if str-for str-meta str-indent)


(defn str-prog
  [prog]
  (str/join "\n" (map str-expr prog)))


;;; expr -> list str
(defn str-expr
  [x]
  (match [x]
         [(_ :guard nil?)] nil
         [(i :guard integer?)] (str i)
         [(list :guard list?)] (let []
                                 (map #(str-expr %) list))
         [({:expr :var :name v} :guard (comp nil? :index))] v
         [{:expr :var :name v :index i}] (str v (str-expr i))
         [{:expr :input :index i}] (format "X[%s]" (str-expr i))
         [{:expr :output :index i}] (format "Y[%s]" (str-expr i))
         [{:expr :assign :lhs lhs :rhs rhs}]
         (format "%s = %s" (str-expr lhs) (str-expr rhs))
         [{:expr :define :name name :args args :body body}]
         (str-define name args body)
         [{:expr :if :cond cond :then then :else else}]
         (str-if cond then else)
         [{:expr :for :var var :from from :to to :body body}]
         (str-for var from to body)
         [{:expr :funcall :name name :args args}]
         (format "%s(%s)" name (str/join ", " (map str-expr args)))
         [{:expr :retn :retexpr expr}]
         (format "return %s" (str-expr expr))
         [{:metaexpr :var}] (format "@%s" (str-meta x))
         [{:metaexpr :funcall}] (format "@{%s}" (str-meta x))
         :else (throw (IllegalArgumentException.
                        (format "Expressão não suportada: %s" x)))))


(defn ->operator
  [key name preced fun]
  {:oper key :name name :preced preced :fun fun})

(def opers {:plus (->operator :plus "+" 200 #(+ %1 %2))
            :minus (->operator :minus "-" 200 #(- %1 %2))
            :times (->operator :times "*" 400 #(* %1 %2))
            :quot (->operator :quot "/" 400 #(/ %1 %2))
            :uminus (->operator :quot "-" 800 #(- %1))})

(defn fmtpars
  "Formata a meta-expressão `x` colocando ou não parênteses de acordo com a
  precedência relativa dos operadores."
  [op1 x]
  (let [tx (str-meta x)]
    (match [x]
           [{:metaexpr :funcall :op opx}]
           (let [prec (get-in opers [op1 :preced])
                 precx (get-in opers [opx :preced])]
             (if (< precx prec) (format "(%s)" tx) tx))
           :else tx)))


(defn str-meta
  [x]
  (match [x]
         [(n :guard integer?)] (str n)
         [{:metaexpr :var :name v}] v
         [{:metaexpr :funcall :op op1 :args [x1 x2]}]
         (let [s1 (fmtpars op1 x1)
               s2 (fmtpars op1 x2)]
           (format "%s %s %s" s1 (:name op1) s2))))


(def default-indentation 4)

(defn str-indent
  [lines &
   {:keys [indentation]
    :or {indentation default-indentation}}]
  (let [padding (apply str (take indentation (repeat \space)))]
    (map (fn [ln] (str padding ln)) lines)))


;;; expr -> vector str
(defn str-expr-split
  [x]
  (-> x
      (str-expr)
      (str/split #"\n")))

;;; str -> list expr -> list expr -> list str
(defn str-define
  [name args body]
  (let [sargs (map str-expr args)
        the-header (format "def %s(%s):" name (str/join ", " sargs))
        the-body (mapcat str-expr-split body)]
    (str/join "\n" (concat [the-header] (str-indent the-body) ["end"]))))


(defn str-if
  [cond then else]
  (let [the-cond (str-expr cond)
        the-then (mapcat str-expr-split then)
        the-else (mapcat str-expr-split else)]
    (str/join
      "\n"
      (concat
        [(format "if %s:" the-cond)]
        (str-indent the-then)
        (when (not (empty? the-else))
          (concat ["else:"] (str-indent the-else)))
        ["end"]))))


(defn str-for
  [var from to body]
  (let [the-var (str-expr var)
        the-header (format "for %s from %d to %d:" the-var from to)
        the-body (mapcat str-expr-split body)]
    (str/join "\n" (concat [the-header]
                           (str-indent the-body)
                           ["next"]))))
