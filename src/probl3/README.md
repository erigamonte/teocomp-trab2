# Tarefa 2 -- Teoria da Computação -- 2021/2

**Definindo Computação & Açucar Sintático & Código como Dados, Dados como Código**

**Autor:** Eduardo R. Costa

## Problema 3
Este problema pede a construção de uma função que receba como entrada um número
$n$ representando o tamanho de uma cadeia de bits de um numero e retorne programa
m NAND-CIRC capaz de multiplicar dois número do tamanho informado.

**Solução**
A solução adotada foi basicamente a idéia do standart multiplication, onde o bit menos
significativo do primeiro numero multiplica todos os bits do segundo numero, após isso
realiza a soma das cadeias de numeros geradas 
resultado0 * 10^0 + resultado1 * 10^1 ... resultadon * 10^n.

**Execução**
Para executar a função no prompt do Clojure execute, por exemplo:

````
(str-multiply 1)
````

Saida:

````
ZEROtemp1$0_0 = NAND(X[1], X[1])
ZEROtemp2$0_0 = NAND(X[1], ZEROtemp1$0_0)
mult0_0 = NAND(ZEROtemp2$0_0, ZEROtemp2$0_0)
ZEROtemp1$0_1 = NAND(X[1], X[1])
ZEROtemp2$0_1 = NAND(X[1], ZEROtemp1$0_1)
mult0_1 = NAND(ZEROtemp2$0_1, ZEROtemp2$0_1)
ANDtemp$0_0 = NAND(X[0], X[1])
mult0_0 = NAND(ANDtemp$0_0, ANDtemp$0_0)
ANDtemp$1000_2000 = NAND(mult0_0, mult0_0)
Y[1] = NAND(ANDtemp$1000_2000, ANDtemp$1000_2000)
ANDtemp$1000_2001 = NAND(mult0_1, mult0_1)
Y[0] = NAND(ANDtemp$1000_2001, ANDtemp$1000_2001)
````