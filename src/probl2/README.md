# Tarefa 2 -- Teoria da Computação -- 2021/2
**Definindo Computação & Açucar Sintático & Código como Dados, Dados como Código**
**Autor:** Eduardo R. Costa

## Problema 2
Este problema pede a construção de uma função que receba como entrada um AST de um programa 
em NAND-CIRC-IF e traduza ele para NAND-CIRC, foi permitido assumir a
existência da função IF em NAND-CIRC.

**Solução**
A solução adotada foi implementar uma função recursiva que mapeia cada estrutura até a mais básica.
Caso encontre uma estrutura de IF, todos os elementos da estrutura then e else são mapeados com outros
nomes e seus valores calculados para depois, utilizando a funcão IF, verificar qual valor irá prevalecer.
Para isso, foi criado um dicionário que mantem os dados anteriores das variáveis alteradas para que seja
possível realizar o bind das no momento de inclusão da função traduzida IF.

**Execução**
Para executar a função no prompt do Clojure execute, por exemplo:

````

````

Saida
:
````

````
