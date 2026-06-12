# Calculadora GPON

Aplicação desktop desenvolvida em Java para calcular o orçamento óptico de uma rede GPON/PON, validar os parâmetros informados e verificar a viabilidade do enlace.

O sistema permite preencher todos os parâmetros para analisar o projeto ou deixar exatamente um campo vazio para que a variável correspondente seja calculada automaticamente.

## Objetivo

O projeto integra conhecimentos das disciplinas de Engenharia de Software e Propagação de Ondas Eletromagnéticas.

A aplicação foi criada para auxiliar no dimensionamento de enlaces ópticos, apresentando:

- cálculo do orçamento de potência;
- cálculo automático de uma variável ausente;
- validação dos dados informados;
- alertas para valores fora das faixas convencionais;
- detalhamento das perdas ópticas;
- fórmula utilizada e substituição dos valores;
- classificação do projeto como viável ou inviável.

## Funcionalidades

- Informar os parâmetros do projeto GPON.
- Calcular uma variável deixada em branco.
- Verificar a viabilidade do enlace.
- Calcular a perda da fibra.
- Calcular a perda total dos conectores.
- Considerar a perda dos splitters.
- Calcular a potência estimada no receptor.
- Calcular a margem disponível.
- Exibir alertas para valores atípicos.
- Impedir cálculos com dados inválidos ou insuficientes.
- Limpar todos os campos do formulário.
- Executar testes automatizados com JUnit.

## Parâmetros utilizados

| Parâmetro | Símbolo | Unidade |
|---|---:|---:|
| Potência de transmissão | Ptx | dBm |
| Sensibilidade do receptor | Srx | dBm |
| Atenuação da fibra | α | dB/km |
| Comprimento da fibra | L | km |
| Perda por conector | Pc | dB |
| Número de conectores | Nc | unidade |
| Perda total dos splitters | Ps | dB |
| Margem de segurança | M | dB |

## Fórmula principal

O orçamento óptico é representado por:

```text
Ptx - Srx = (α × L) + (Pc × Nc) + Ps + M
```

A potência estimada no receptor é calculada por:

```text
Prx = Ptx - [(α × L) + (Pc × Nc) + Ps]
```

A margem disponível é:

```text
Margem disponível = Prx - Srx
```

O projeto é considerado viável quando a margem disponível é maior ou igual à margem de segurança exigida.

## Tecnologias

- Java 17
- JavaFX 17
- Maven
- JUnit 5
- CSS
- Programação orientada a objetos
- Git e GitHub

## Arquitetura

O projeto utiliza uma separação simples em camadas:

```text
app
└── Interface gráfica JavaFX

model
├── Dados do projeto
├── Resultados
├── Enumerações
└── Limites de validação

service
├── Regras de cálculo
└── Regras de validação
```

## Estrutura do projeto

```text
calculadora-gpon/
├── docs/
│   ├── diagramaDeClasse.png
│   ├── diagramaDeUso.png
│   └── documentoDeRequisitos.pdf
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── app/
│   │   │   │   └── CalculadoraPONApp.java
│   │   │   ├── model/
│   │   │   │   ├── LimitesPON.java
│   │   │   │   ├── ParametroCalculavel.java
│   │   │   │   ├── ProjetoPON.java
│   │   │   │   ├── ResultadoCalculo.java
│   │   │   │   └── ResultadoValidacao.java
│   │   │   └── service/
│   │   │       ├── CalculadoraService.java
│   │   │       └── ValidacaoService.java
│   │   └── resources/
│   │       ├── background.jpg
│   │       └── style.css
│   └── test/
│       └── java/
│           └── service/
│               ├── CalculadoraServiceTest.java
│               └── ValidacaoServiceTest.java
├── .gitignore
├── pom.xml
└── README.md
```

## Pré-requisitos

Em uma distribuição Linux baseada em Ubuntu ou Debian, instale o Java 17, Maven e Git:

```bash
sudo apt update
sudo apt install openjdk-17-jdk maven git -y
```

Confirme as instalações:

```bash
java -version
mvn -version
git --version
```

## Clonar o projeto

```bash
git clone https://github.com/AmandoLuizDaCruz/calculadora-gpon.git
cd calculadora-gpon
```

## Executar os testes

```bash
mvn clean test
```

Resultado esperado:

```text
Tests run: 26, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Executar a aplicação

```bash
mvn javafx:run
```

O Maven baixa automaticamente as dependências necessárias e abre a interface JavaFX.

## Gerar o pacote

```bash
mvn clean package
```

O arquivo gerado será armazenado na pasta:

```text
target/
```

## Como utilizar

1. Abra a aplicação.
2. Preencha todos os parâmetros para analisar a viabilidade do enlace.
3. Para calcular uma variável, deixe exatamente o campo correspondente vazio.
4. Clique no botão de análise.
5. Consulte a fórmula, a substituição, o resultado e os alertas apresentados.
6. Use o botão de limpar para iniciar um novo cálculo.

## Exemplo de entrada

```text
Potência de transmissão: 5 dBm
Sensibilidade do receptor: -28 dBm
Atenuação da fibra: 0,35 dB/km
Comprimento da fibra: 10 km
Perda por conector: 0,5 dB
Número de conectores: 4
Perda dos splitters: 7,2 dB
Margem de segurança: 3 dB
```

Para esse exemplo:

```text
Perda da fibra: 3,50 dB
Perda dos conectores: 2,00 dB
Perdas físicas totais: 12,70 dB
Potência estimada no receptor: -7,70 dBm
Margem disponível: 20,30 dB
Resultado: projeto viável
```

## Validações

O sistema verifica:

- existência de mais de um campo vazio;
- valores negativos incompatíveis;
- divisões por zero;
- resultados infinitos ou inválidos;
- limites físicos adotados;
- valores fora das faixas convencionais;
- quantidade fracionária de conectores.

Quando o cálculo da quantidade de conectores resulta em um número decimal, a aplicação apresenta o resultado matemático e utiliza a maior quantidade inteira que não ultrapassa o orçamento óptico.

## Testes automatizados

O projeto possui 26 testes automatizados, distribuídos entre:

- testes dos cálculos;
- testes das validações;
- projetos viáveis e inviáveis;
- cálculo de cada variável;
- campos ausentes;
- valores negativos;
- limites físicos;
- alertas de faixas convencionais;
- divisões por zero;
- quantidade fracionária de conectores.

## Documentação

A pasta `docs/` contém:

- documento de requisitos;
- diagrama de classes;
- diagrama de casos de uso.

## Limitações

- As faixas convencionais são premissas acadêmicas adotadas pelo projeto.
- O sistema analisa um enlace por vez.
- O sistema calcula somente uma variável ausente por operação.
- A quantidade de conectores deve ser apresentada como número inteiro na aplicação.

## Autor

**Amando Luiz da Cruz**

## Estado do projeto

Projeto funcional e concluído, com:

- aplicação JavaFX executando;
- estrutura Maven configurada;
- oito variáveis calculáveis;
- validações implementadas;
- documentação produzida;
- 26 testes automatizados aprovados.
