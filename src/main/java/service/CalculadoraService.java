package service;

import model.ParametroCalculavel;
import model.ProjetoPON;
import model.ResultadoCalculo;
import model.ResultadoValidacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Executa os cálculos de orçamento de potência da rede PON.
 */
public class CalculadoraService {

    private static final double EPSILON = 0.000000001;

    private final ValidacaoService validacaoService;

    public CalculadoraService() {
        this.validacaoService = new ValidacaoService();
    }

    public ResultadoCalculo analisar(ProjetoPON projeto) {
        ResultadoValidacao validacao =
                validacaoService.validar(projeto);

        if (!validacao.isValido()) {
            String mensagem =
                    "DADOS INVÁLIDOS OU INSUFICIENTES\n\n"
                            + String.join(
                            "\n",
                            validacao.getErros()
                    );

            return ResultadoCalculo.erro(
                    mensagem,
                    validacao.getAlertas()
            );
        }

        if (projeto.contarParametrosAusentes() == 1) {
            return calcularParametroAusente(
                    projeto,
                    validacao.getAlertas()
            );
        }

        return analisarProjetoCompleto(
                projeto,
                validacao.getAlertas()
        );
    }

    private ResultadoCalculo analisarProjetoCompleto(
            ProjetoPON projeto,
            List<String> alertasRecebidos
    ) {
        double perdaFibra =
                projeto.getAtenuacaoFibra()
                        * projeto.getComprimentoFibra();

        double perdaConectores =
                projeto.getPerdaPorConector()
                        * projeto.getNumeroDeConectores();

        double perdaSplitters =
                projeto.getPerdaPorSplitter();

        double perdasFisicas =
                perdaFibra
                        + perdaConectores
                        + perdaSplitters;

        double potenciaRecebida =
                projeto.getPotenciaTransmissao()
                        - perdasFisicas;

        double margemDisponivel =
                potenciaRecebida
                        - projeto.getSensibilidadeReceptor();

        boolean projetoViavel =
                margemDisponivel
                        >= projeto.getMargemDeSeguranca();

        String conclusao = projetoViavel
                ? "PROJETO VIÁVEL"
                : "PROJETO INVIÁVEL";

        String mensagem = String.format(
                Locale.US,
                """
                ANÁLISE COMPLETA DO PROJETO

                Perda na fibra: %.2f dB
                Perda nos conectores: %.2f dB
                Perda nos splitters: %.2f dB

                Perdas físicas totais: %.2f dB
                Potência estimada na recepção: %.2f dBm
                Sensibilidade do receptor: %.2f dBm

                Margem disponível: %.2f dB
                Margem de segurança exigida: %.2f dB

                RESULTADO: %s
                """,
                perdaFibra,
                perdaConectores,
                perdaSplitters,
                perdasFisicas,
                potenciaRecebida,
                projeto.getSensibilidadeReceptor(),
                margemDisponivel,
                projeto.getMargemDeSeguranca(),
                conclusao
        ).replace(".", ",");

        return ResultadoCalculo.analiseCompleta(
                projetoViavel,
                perdaFibra,
                perdaConectores,
                perdaSplitters,
                perdasFisicas,
                potenciaRecebida,
                margemDisponivel,
                mensagem,
                alertasRecebidos
        );
    }

    private ResultadoCalculo calcularParametroAusente(
            ProjetoPON projeto,
            List<String> alertasRecebidos
    ) {
        ParametroCalculavel parametro =
                projeto.identificarParametroAusente();

        double valorCalculado;

        try {
            valorCalculado = switch (parametro) {
                case POTENCIA_TRANSMISSAO ->
                        calcularPotenciaTransmissao(projeto);

                case SENSIBILIDADE_RECEPTOR ->
                        calcularSensibilidadeReceptor(projeto);

                case ATENUACAO_FIBRA ->
                        calcularAtenuacaoFibra(projeto);

                case COMPRIMENTO_FIBRA ->
                        calcularComprimentoFibra(projeto);

                case PERDA_POR_CONECTOR ->
                        calcularPerdaPorConector(projeto);

                case NUMERO_CONECTORES ->
                        calcularNumeroConectores(projeto);

                case PERDA_POR_SPLITTER ->
                        calcularPerdaSplitter(projeto);

                case MARGEM_SEGURANCA ->
                        calcularMargemSeguranca(projeto);

                case NENHUM ->
                        throw new IllegalStateException(
                                "Nenhum parâmetro ausente foi identificado."
                        );
            };
        } catch (IllegalArgumentException exception) {
            return ResultadoCalculo.erro(
                    "NÃO FOI POSSÍVEL REALIZAR O CÁLCULO\n\n"
                            + exception.getMessage(),
                    alertasRecebidos
            );
        }

        if (!Double.isFinite(valorCalculado)) {
            return ResultadoCalculo.erro(
                    "O resultado do cálculo não é um número válido.",
                    alertasRecebidos
            );
        }

        if (deveSerNaoNegativo(parametro)
                && valorCalculado < 0) {
            return ResultadoCalculo.erro(
                    "O cálculo resultou em um valor negativo para "
                            + parametro.getDescricao()
                            + ". Os parâmetros informados são inconsistentes.",
                    alertasRecebidos
            );
        }

        List<String> alertas =
                new ArrayList<>(alertasRecebidos);

        adicionarAlertaResultado(
                parametro,
                valorCalculado,
                alertas
        );

        String observacao = "";

        if (parametro
                == ParametroCalculavel.NUMERO_CONECTORES) {
            double valorArredondado =
                    Math.rint(valorCalculado);

            if (Math.abs(valorCalculado - valorArredondado)
                    > 0.0001) {
                observacao =
                        "\n\nA quantidade calculada não é inteira. "
                                + "Na aplicação prática, escolha uma "
                                + "quantidade inteira e execute novamente a análise.";
            }
        }

        String mensagem = String.format(
                Locale.US,
                """
                VARIÁVEL CALCULADA

                Parâmetro: %s
                Valor estimado: %.2f %s

                O valor foi determinado a partir dos demais
                parâmetros informados pelo usuário.%s
                """,
                parametro.getDescricao(),
                valorCalculado,
                parametro.getUnidade(),
                observacao
        ).replace(".", ",");

        return ResultadoCalculo.variavelCalculada(
                parametro,
                valorCalculado,
                mensagem,
                alertas
        );
    }

    private double calcularPotenciaTransmissao(
            ProjetoPON projeto
    ) {
        return projeto.getSensibilidadeReceptor()
                + calcularPerdasFisicas(projeto)
                + projeto.getMargemDeSeguranca();
    }

    private double calcularSensibilidadeReceptor(
            ProjetoPON projeto
    ) {
        return projeto.getPotenciaTransmissao()
                - calcularPerdasFisicas(projeto)
                - projeto.getMargemDeSeguranca();
    }

    private double calcularAtenuacaoFibra(
            ProjetoPON projeto
    ) {
        validarDivisor(
                projeto.getComprimentoFibra(),
                "O comprimento da fibra deve ser maior que zero."
        );

        double perdasRestantes =
                projeto.getPotenciaTransmissao()
                        - projeto.getSensibilidadeReceptor()
                        - calcularPerdasConectores(projeto)
                        - projeto.getPerdaPorSplitter()
                        - projeto.getMargemDeSeguranca();

        return perdasRestantes
                / projeto.getComprimentoFibra();
    }

    private double calcularComprimentoFibra(
            ProjetoPON projeto
    ) {
        validarDivisor(
                projeto.getAtenuacaoFibra(),
                "A atenuação da fibra deve ser maior que zero."
        );

        double perdasRestantes =
                projeto.getPotenciaTransmissao()
                        - projeto.getSensibilidadeReceptor()
                        - calcularPerdasConectores(projeto)
                        - projeto.getPerdaPorSplitter()
                        - projeto.getMargemDeSeguranca();

        return perdasRestantes
                / projeto.getAtenuacaoFibra();
    }

    private double calcularPerdaPorConector(
            ProjetoPON projeto
    ) {
        validarDivisor(
                projeto.getNumeroDeConectores(),
                "A quantidade de conectores deve ser maior que zero."
        );

        double perdasRestantes =
                projeto.getPotenciaTransmissao()
                        - projeto.getSensibilidadeReceptor()
                        - calcularPerdaFibra(projeto)
                        - projeto.getPerdaPorSplitter()
                        - projeto.getMargemDeSeguranca();

        return perdasRestantes
                / projeto.getNumeroDeConectores();
    }

    private double calcularNumeroConectores(
            ProjetoPON projeto
    ) {
        validarDivisor(
                projeto.getPerdaPorConector(),
                "A perda por conector deve ser maior que zero."
        );

        double perdasRestantes =
                projeto.getPotenciaTransmissao()
                        - projeto.getSensibilidadeReceptor()
                        - calcularPerdaFibra(projeto)
                        - projeto.getPerdaPorSplitter()
                        - projeto.getMargemDeSeguranca();

        return perdasRestantes
                / projeto.getPerdaPorConector();
    }

    private double calcularPerdaSplitter(
            ProjetoPON projeto
    ) {
        return projeto.getPotenciaTransmissao()
                - projeto.getSensibilidadeReceptor()
                - calcularPerdaFibra(projeto)
                - calcularPerdasConectores(projeto)
                - projeto.getMargemDeSeguranca();
    }

    private double calcularMargemSeguranca(
            ProjetoPON projeto
    ) {
        return projeto.getPotenciaTransmissao()
                - projeto.getSensibilidadeReceptor()
                - calcularPerdasFisicas(projeto);
    }

    private double calcularPerdasFisicas(
            ProjetoPON projeto
    ) {
        return calcularPerdaFibra(projeto)
                + calcularPerdasConectores(projeto)
                + projeto.getPerdaPorSplitter();
    }

    private double calcularPerdaFibra(
            ProjetoPON projeto
    ) {
        return projeto.getAtenuacaoFibra()
                * projeto.getComprimentoFibra();
    }

    private double calcularPerdasConectores(
            ProjetoPON projeto
    ) {
        return projeto.getPerdaPorConector()
                * projeto.getNumeroDeConectores();
    }

    private void validarDivisor(
            double divisor,
            String mensagem
    ) {
        if (Math.abs(divisor) < EPSILON) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private boolean deveSerNaoNegativo(
            ParametroCalculavel parametro
    ) {
        return parametro
                != ParametroCalculavel.POTENCIA_TRANSMISSAO
                && parametro
                != ParametroCalculavel.SENSIBILIDADE_RECEPTOR;
    }

    private void adicionarAlertaResultado(
            ParametroCalculavel parametro,
            double valor,
            List<String> alertas
    ) {
        switch (parametro) {
            case POTENCIA_TRANSMISSAO -> {
                if (valor < -10 || valor > 10) {
                    alertas.add(
                            "A potência de transmissão calculada está "
                                    + "fora da faixa convencional."
                    );
                }
            }

            case SENSIBILIDADE_RECEPTOR -> {
                if (valor < -40 || valor > -10) {
                    alertas.add(
                            "A sensibilidade calculada está fora "
                                    + "da faixa convencional."
                    );
                }
            }

            case ATENUACAO_FIBRA -> {
                if (valor < 0.1 || valor > 1.0) {
                    alertas.add(
                            "A atenuação calculada está fora "
                                    + "da faixa convencional."
                    );
                }
            }

            case COMPRIMENTO_FIBRA -> {
                if (valor > 60) {
                    alertas.add(
                            "O comprimento calculado é superior a 60 km."
                    );
                }
            }

            case PERDA_POR_CONECTOR -> {
                if (valor > 2) {
                    alertas.add(
                            "A perda calculada por conector é superior a 2 dB."
                    );
                }
            }

            case NUMERO_CONECTORES -> {
                if (valor > 24) {
                    alertas.add(
                            "A quantidade calculada de conectores é superior a 24."
                    );
                }
            }

            case PERDA_POR_SPLITTER -> {
                if (valor > 35) {
                    alertas.add(
                            "A perda calculada para os splitters é superior a 35 dB."
                    );
                }
            }

            case MARGEM_SEGURANCA -> {
                if (valor < 2 || valor > 10) {
                    alertas.add(
                            "A margem calculada está fora da faixa "
                                    + "convencional de 2 dB a 10 dB."
                    );
                }
            }

            case NENHUM -> {
                // Nenhuma ação necessária.
            }
        }
    }
}