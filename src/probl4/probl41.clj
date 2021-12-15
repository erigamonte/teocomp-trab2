(ns probl4.probl41
  (:require [clojure.string :as str]
            [clojure.core.match :refer (match)]))

(defn altera-vari
  [r x]
  (dosync
    (ref-set r x)))

;;retira da estrutura partes que não precisa ser usada, deixando aperna input, output e var
(defn extract
  [x]
  (println x)
  (match [x]
         [{:expr :input :index index}] [x]
         [{:expr :output :index index}] [x]
         [{:expr :var :name name}] [x]
         [{:expr :funcall :name _ :args args }] args
         [{:expr :assign :lhs lhs :rhs rhs}] [lhs (extract rhs)]))

(defn analize-type
  [x]
  (println x)
  (match [x]
         [{:expr :input :index index}] 1
         [{:expr :var :name name}] 2
         [{:expr :output :index index}] 3))

(defn return-number
  [x n m v]
  (println x)
  (match [x]
         [{:expr :input :index index}] index
         [{:expr :var :name name}] (+ (.indexOf (into [] v) x) (count n))
         [{:expr :output :index index}] (+ (+ (.indexOf (into [] m) x) (count n)) (count v))))

;;feito um custom set prq o clojure nao tem nativo um set que mantem a ordem de insercao
(defn custom-set
  [X x]
  (cond
    (> (count (set (conj X x))) (count X)) (into [] (conj X x))
    :else X))

(defn tuples
  [X]
  (let [n (ref [])
        m (ref [])
        v (ref [])
        L (ref [])
        ;;formato de saida esperado [a [b c]]
        te (into [] (map #(extract %) X))
        ;;organiza todas as saidas de te em uma unica lista para facilita a iteracao
        flatten-te (into [] (flatten te))]
      (dotimes [i (count flatten-te)]
        (if
          (= (analize-type (get flatten-te i)) 1)
            (altera-vari n (custom-set @n (get flatten-te i))))
        (if
          (= (analize-type (get flatten-te i)) 2)
          (altera-vari v (custom-set @v (get flatten-te i))))
        (if
          (= (analize-type (get flatten-te i)) 3)
          (altera-vari m (custom-set @m (get flatten-te i)))))
      (dotimes [i (count te)]
        (altera-vari L (conj @L
                             (seq [(return-number (first (get te i)) @n @m @v)
                              (return-number (first (second (get te i))) @n @m @v)
                              (return-number (second (second (get te i))) @n @m @v)]))))
      [(count @n) (count @m) @L]))