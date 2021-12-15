(ns probl3.probl3
  (:require [clojure.string :as str]
            [clojure.core.match :refer (match)]))

(defn and-function
  [a b j i output]
  (conj []
    (str "ANDtemp$" j "_" i " = NAND(" a ", " b ")")
    (str output " = NAND(" "ANDtemp$" j "_" i ", " "ANDtemp$" j "_" i ")")))

(defn or-function
  [a b j i output]
  (conj []
    (str "ORtemp1$" j "_" i " = NAND(" a ", " a ")")
    (str "ORtemp2$" j "_" i " = NAND(" b ", " b ")")
    (str output " = NAND(" "ORtemp1$" j "_" i ", " "ORtemp2$" j "_" i ")")))

(defn xor-function
  [a b j i output]
  (conj []
        (str "XORtemp1$" j "_" i " = NAND(" a ", " b ")")
        (str "XORtemp2$" j "_" i " = NAND(" a ", " "XORtemp1$" j "_" i ")")
        (str "XORtemp3$" j "_" i " = NAND(" b ", " "XORtemp1$" j "_" i ")")
        (str output " = NAND(" "XORtemp2$" j "_" i ", " "XORtemp3$" j "_" i ")")))

(defn zero-function
  [a j i output]
  (conj []
      (str "ZEROtemp1$" j "_" i " = NAND(" a ", " a ")")
      (str "ZEROtemp2$" j "_" i " = NAND(" a ", " "ZEROtemp1$" j "_" i ")")
      (str output " = NAND(" "ZEROtemp2$" j "_" i ", " "ZEROtemp2$" j "_" i ")")))

;;monta o corpo da parte responsavel por somar
(defn sum-function-script
  [X1 X2 j i]
  (conj []
        (into []
              (concat []
                (xor-function (get X1 i) (get X2 i) j (+ i 1) (str "temp" "_" j "_" (+ i 1)))
                (xor-function (str "temp" "_" j "_"  (+ i 1)) (str "Carry" "_" j "_" i) j (+ i 2) (str "Out" "_" j "_" i))
                (and-function (get X1 i) (get X2 i) j (+ i 3) (str "a" "_" j "_"  (+ i 3)))
                (and-function (str "temp" "_" j "_" (+ i 1)) (str "Carry" "_" j "_"  i) j (+ i 4) (str "b" "_" j "_"  (+ i 4)))
                (or-function (str "a" "_" j "_"  (+ i 3)) (str "b" "_" j "_"  (+ i 4)) j (+ i 5) (str "Carry" "_" j "_"  (+ i 1)))))
              (str "Out" "_" j "_" i)))

;;faz a iteracao montando a soma a quantidade de vezes necessaria
(defn sum-function
  [X1 X2 j]
  (conj []
        (into []
              (concat
                (zero-function (get X1 0) j 0 (str "Carry" "_" j "_"  0))
                (flatten (for [i (range (count X1))]
                            (flatten (conj []
                                           (first (sum-function-script X1 X2 j i))))))))
        (into [] (flatten (for [i (range (count X1))]
                            (flatten (conj []
                                           (second (sum-function-script X1 X2 j i)))))))))

;; inicializa a primeira multiplicacao do bit menos significativo com cada bit do outro numeo
(defn create-params
  [x X2 j s]

  (conj []
        (into [] (flatten
                   (concat (for [i (range (* (count X2) 2))] (zero-function (get X2 0)
                                                                            j i
                                                                            (str "mult" j "_" i)))
                           (for [i (range (count X2))] (and-function x
                                                                     (get X2 (- (- (count X2) 1) i))
                                                                     j (+ i s)
                                                                     (str "mult" j "_" (+ i s)))))))
        (into [] (for [i (range (* (count X2) 2))] (str "mult" j "_" i))))
  )

;;monta a saida do algoritmo gerado
(defn output-function
  [X j]
  (flatten (conj []  (for [i (range (count X))] (and-function (get X i) (get X i) j (+ i (+ j 1000)) (str "Y[" (- (- (count X) i) 1) "]") )))))

(defn altera-variavel
  [r x]
  (dosync
    (ref-set r x)))

(defn multiply
  [n]
  (let [prog              (ref [])
        out               (ref [])
        X1                (into [] (for [x (range 0 (/ n 2))] (str "X[" x "]")))
        X2                (into [] (for [x (range (/ n 2) n)] (str "X[" x "]")))
        body              (into [] (flatten (conj (for [i (range (count X2))](first (create-params (get X1 (- (- (count X1) 1) i)) X2 i i))))))
        params            (into [] (conj (for [i (range (count X2))](second (create-params (get X1 (- (- (count X1) 1) i)) X2 i i)))))]
      (let []
          (altera-variavel prog (conj [] body))
          (altera-variavel out  (first params)))
          (dotimes [i (- (count X1) 1)]
            (let []
              (altera-variavel prog (into [] (flatten (conj [] @prog (first (sum-function @out (get params (+ i 1)) (+ 10 i)))))))
              (altera-variavel out  (second (sum-function (get params i) @out (+ 10 i))))))
          (altera-variavel prog (conj [] @prog (output-function @out 1000)))
      (into [] (flatten @prog))))

;;imprime o codigo nand de n bits
(defn str-multiply
  [n]
  (println (str/join "\n" (multiply (* n 2)))))