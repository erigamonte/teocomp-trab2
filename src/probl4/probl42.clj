(ns probl4.probl42
  (:require [clojure.string :as str]
            [clojure.core.match :refer (match)]))

(defn altera-var
  [r x]
  (dosync
    (ref-set r x)))

(defn nand-validator
  [a b]
  (cond
    (and (= a 1) (= b 1)) 0
    :else 1))

(defn get-value
  [expr i j]
  (nth (get expr i) j))

(defn get-keyword
  [expr i j]
  (keyword (str (get-value expr i j))))

(defn get-output
  [all pos]
  (let [out (ref [])]
    (dotimes [i (count pos)]
      (altera-var out (conj @out (get all (get pos (- (- (count pos) 1) i))))))
  @out))

(defn eval-tuple
  [X args]
  (let [max_number (apply max (flatten X))
        n (get X 0)
        m (get X 1)
        L (get X 2)
        X1 (subvec args 0 (/ (count args) 2))
        X2 (subvec args (/ (count args) 2) (count args))
        hash_n (ref {})
        hash_m_v (ref {})
        output-pos (ref [])]
    (altera-var hash_n (apply merge (apply map hash-map [(into [] (map keyword (map str (range n)))) (concat [] (reverse X1) (reverse X2))])))
    (altera-var hash_m_v (apply merge (apply map hash-map [(into [] (map keyword (map str (range n (+ max_number 1))))) (into [] (repeat (- (+ max_number 1) n) 0))])))
    (altera-var output-pos (into [] (map keyword (map str (range (+ (- max_number m) 1) (+ max_number 1))))))
    ;;organiza todos os numeros em uma grande estrutura de contadores para acompanhar o estado de cada numero
    (let [all (ref (merge @hash_n @hash_m_v))]
      (println @all)
      (dotimes [i (count L)]
        ;;itera de tupla em tupla armazenando o valor do resultado nand
        (altera-var all (assoc @all (get-keyword L i 0) (nand-validator (get @all (get-keyword L i 1)) (get @all (get-keyword L i 2)))))
        )
      (get-output @all @output-pos))))