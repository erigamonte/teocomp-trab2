# Tarefa 2 -- Teoria da Computação -- 2021/2

**Definindo Computação & Açucar Sintático & Código como Dados, Dados como Código**

**Autor:** Eduardo R. Costa

## Problema 4.1
Este problema pede a construção de uma função que receba como entrada uma sequência de 
dicionários de expressões do tipo “:assign”, e retorne a representação em lista de tuplas 
deste programa

**Solução**
A solução adotada foi basicamente identificar as estruturas que estavam no dicionário de
entrada e classifica-los. Após tudo identificado, bastou retornar a lista com a numeração
associada a operação do algoritmo de entrada.

**Execução**
Para executar a função no prompt do Clojure execute, por exemplo:

````
(tuples '({:expr :assign,
          :lhs {:expr :var, :name "ZEROtemp1$0_0"},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args ({:expr :input, :index 1} {:expr :input, :index 1})}}
         {:expr :assign,
          :lhs {:expr :var, :name "ZEROtemp2$0_0"},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args
                       ({:expr :input, :index 1} {:expr :var, :name "ZEROtemp1$0_0"})}}
         {:expr :assign,
          :lhs {:expr :var, :name "mult0_0"},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args
                       ({:expr :var, :name "ZEROtemp2$0_0"}
                        {:expr :var, :name "ZEROtemp2$0_0"})}}
         {:expr :assign,
          :lhs {:expr :var, :name "ZEROtemp1$0_1"},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args ({:expr :input, :index 1} {:expr :input, :index 1})}}
         {:expr :assign,
          :lhs {:expr :var, :name "ZEROtemp2$0_1"},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args
                       ({:expr :input, :index 1} {:expr :var, :name "ZEROtemp1$0_1"})}}
         {:expr :assign,
          :lhs {:expr :var, :name "mult0_1"},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args
                       ({:expr :var, :name "ZEROtemp2$0_1"}
                        {:expr :var, :name "ZEROtemp2$0_1"})}}
         {:expr :assign,
          :lhs {:expr :var, :name "ANDtemp$0_0"},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args ({:expr :input, :index 0} {:expr :input, :index 1})}}
         {:expr :assign,
          :lhs {:expr :var, :name "mult0_0"},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args
                       ({:expr :var, :name "ANDtemp$0_0"}
                        {:expr :var, :name "ANDtemp$0_0"})}}
         {:expr :assign,
          :lhs {:expr :var, :name "ANDtemp$1000_2000"},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args
                       ({:expr :var, :name "mult0_0"} {:expr :var, :name "mult0_0"})}}
         {:expr :assign,
          :lhs {:expr :output, :index 1},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args
                       ({:expr :var, :name "ANDtemp$1000_2000"}
                        {:expr :var, :name "ANDtemp$1000_2000"})}}
         {:expr :assign,
          :lhs {:expr :var, :name "ANDtemp$1000_2001"},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args
                       ({:expr :var, :name "mult0_1"} {:expr :var, :name "mult0_1"})}}
         {:expr :assign,
          :lhs {:expr :output, :index 0},
          :rhs
                {:expr :funcall,
                 :name "NAND",
                 :args
                       ({:expr :var, :name "ANDtemp$1000_2001"}
                        {:expr :var, :name "ANDtemp$1000_2001"})}}))
````

Saida
:
````
[2 2 [(2 1 1) (3 1 2) (4 3 3) (5 1 1) (6 1 5) (7 6 6) (8 0 1) (4 8 8) (9 4 4) (11 9 9) (10 7 7) (12 10 10)]]
````

## Problema 4.2
Este problema pede a construção de uma função que receba como entrada as tuplas conforme foi gerado
no problema 4.1 e execute retornando uma sequência de bits como resultado.

**Solução**
A solução adotada foi realizar a operação contrária, mapeando as entradas e a partir disto mapeando 
as saídas e o bloco de código executável. Foi criado um dicionário para armazenar o valor de cada uma
das váriaveis do programa, este dicionário é atualizado a cada operação executada.

**Execução**
Para executar a função no prompt do Clojure execute, por exemplo:

````
(eval-tuple [2 2 ['(2 1 1) '(3 1 2) '(4 3 3) '(5 1 1) '(6 1 5) '(7 6 6) '(8 0 1) '(4 8 8) '(9 4 4) '(11 9 9) '(10 7 7) '(12 10 10)]] [1 1])
````

Saida:

````
[0 1]
````