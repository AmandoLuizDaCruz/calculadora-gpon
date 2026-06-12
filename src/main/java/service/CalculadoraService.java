package service;

import model.LimitesPON;
import model.ParametroCalculavel;
import model.ProjetoPON;
import model.ResultadoCalculo;
import model.ResultadoValidacao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Executa os cálculos do orçamento de potência
 * de uma rede óptica passiva PON.
 */
public class CalculadoraService {

    private static final Locale LOCALE_BR =
            Locale.forLanguageTag("pt-BR");

    private final ValidacaoService validacaoService;

    public CalculadoraService() {
        this.validacaoService = new ValidacaoService();
    }

    public ResultadoCalculo analisar(ProjetoPON projeto) {
        ResultadoValidacao validacao =
                validacaoService.validar(projeto);

        if (!validacao.isValido()) {
            return ResultadoCalculo.erro(
                    montarMensagemDeErros(validacao),
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
        double perdaFibra = calcularPerdaFibra(projeto);

        double perdaConectores =
                calcularPerdasConectores(projeto);

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

        String formula =
                "Prx = Ptx - [(α × L) + (Pc × Nc) + Ps]";

        String substituicao = String.format(
                LOCALE_BR,
                "Prx = %s - [(%s × %s) + (%s × %d) + %s]",
                formatar(projeto.getPotenciaTransmissao()),
                formatar(projeto.getAtenuacaoFibra()),
                formatar(projeto.getComprimentoFibra()),
                formatar(projeto.getPerdaPorConector()),
                projeto.getNumeroDeConectores(),
                formatar(projeto.getPerdaPorSplitter())
        );

        String mensagem =
                "ANÁLISE COMPLETA DO PROJETO\n\n"
                        + "Fórmula da potência recebida:\n"
                        + formula
                        + "\n\nSubstituição:\n"
                        + substituicao
                        + "\n\nDETALHAMENTO DAS PERDAS\n\n"
                        + "Perda na fibra: "
                        + formatar(perdaFibra)
                        + " dB\n"
                        + "Perda nos conectores: "
                        + formatar(perdaConectores)
                        + " dB\n"
                        + "Perda nos splitters: "
                        + formatar(perdaSplitters)
                        + " dB\n"
                        + "Perdas físicas totais: "
                        + formatar(perdasFisicas)
                        + " dB\n\n"
                        + "Potência estimada na recepção: "
                        + formatar(potenciaRecebida)
                        + " dBm\n"
                        + "Sensibilidade do receptor: "
                        + formatar(projeto.getSensibilidadeReceptor())
                        + " dBm\n"
                        + "Margem disponível: "
                        + formatar(margemDisponivel)
                        + " dB\n"
                        + "Margem de segurança exigida: "
                        + formatar(projeto.getMargemDeSeguranca())
                        + " dB\n\n"
                        + "RESULTADO: "
                        + conclusao;

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

        double resultadoMatematico;

        try {
            resultadoMatematico = switch (parametro) {
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
                        throw new IllegalArgumentException(
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

        resultadoMatematico =
                normalizarValorProximoDeZero(
                        resultadoMatematico
                );

        ResultadoValidacao validacaoResultado =
                validacaoService.validarValorCalculado(
                        parametro,
                        resultadoMatematico
                );

        List<String> alertas = new ArrayList<>(
                alertasRecebidos
        );

        alertas.addAll(
                validacaoResultado.getAlertas()
        );

        if (!validacaoResultado.isValido()) {
            return ResultadoCalculo.erro(
                    "RESULTADO INCONSISTENTE\n\n"
                            + String.join(
                            "\n",
                            validacaoResultado.getErros()
                    ),
                    alertas
            );
        }

        DetalheFormula detalhe =
                montarDetalheFormula(
                        parametro,
                        projeto
                );

        double valorApresentado = resultadoMatematico;

        String blocoResultado;

        if (parametro
                == ParametroCalculavel.NUMERO_CONECTORES) {
            int quantidadeMaxima = (int) Math.floor(
                    resultadoMatematico
                            + LimitesPON.EPSILON
            );

            valorApresentado = quantidadeMaxima;

            blocoResultado =
                    "Resultado matemático: "
                            + formatar(resultadoMatematico)
                            + " conectores\n"
                            + "Quantidade máxima recomendada: "
                            + quantidadeMaxima
                            + " conectores";
        } else {
            blocoResultado =
                    "Resultado: "
                            + formatar(resultadoMatematico)
                            + " "
                            + parametro.getUnidade();
        }

        String mensagem =
                "VARIÁVEL CALCULADA\n\n"
                        + "Parâmetro: "
                        + parametro.getDescricao()
                        + "\n\nFórmula:\n"
                        + detalhe.formula()
                        + "\n\nSubstituição:\n"
                        + detalhe.substituicao()
                        + "\n\n"
                        + blocoResultado
                        + "\n\nO valor foi determinado a partir "
                        + "dos demais parâmetros informados.";

        return ResultadoCalculo.variavelCalculada(
                parametro,
                valorApresentado,
                mensagem,
                alertas
        );
    }

    private String montarMensagemDeErros(
            ResultadoValidacao validacao
    ) {
        return "DADOS INVÁLIDOS OU INSUFICIENTES\n\n"
                + String.join(
                "\n",
                validacao.getErros()
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
                "O comprimento da fibra deve ser maior que zero "
                        + "para calcular a atenuação."
        );

        double perdasDisponiveis =
                projeto.getPotenciaTransmissao()
                        - projeto.getSensibilidadeReceptor()
                        - calcularPerdasConectores(projeto)
                        - projeto.getPerdaPorSplitter()
                        - projeto.getMargemDeSeguranca();

        return perdasDisponiveis
                / projeto.getComprimentoFibra();
    }

    private double calcularComprimentoFibra(
            ProjetoPON projeto
    ) {
        validarDivisor(
                projeto.getAtenuacaoFibra(),
                "A atenuação da fibra deve ser maior que zero "
                        + "para calcular o comprimento."
        );

        double perdasDisponiveis =
                projeto.getPotenciaTransmissao()
                        - projeto.getSensibilidadeReceptor()
                        - calcularPerdasConectores(projeto)
                        - projeto.getPerdaPorSplitter()
                        - projeto.getMargemDeSeguranca();

        return perdasDisponiveis
                / projeto.getAtenuacaoFibra();
    }

    private double calcularPerdaPorConector(
            ProjetoPON projeto
    ) {
        validarDivisor(
                projeto.getNumeroDeConectores(),
                "A quantidade de conectores deve ser maior que zero "
                        + "para calcular a perda por conector."
        );

        double perdasDisponiveis =
                projeto.getPotenciaTransmissao()
                        - projeto.getSensibilidadeReceptor()
                        - calcularPerdaFibra(projeto)
                        - projeto.getPerdaPorSplitter()
                        - projeto.getMargemDeSeguranca();

        return perdasDisponiveis
                / projeto.getNumeroDeConectores();
    }

    private double calcularNumeroConectores(
            ProjetoPON projeto
    ) {
        validarDivisor(
                projeto.getPerdaPorConector(),
                "A perda por conector deve ser maior que zero "
                        + "para calcular a quantidade."
        );

        double perdasDisponiveis =
                projeto.getPotenciaTransmissao()
                        - projeto.getSensibilidadeReceptor()
                        - calcularPerdaFibra(projeto)
                        - projeto.getPerdaPorSplitter()
                        - projeto.getMargemDeSeguranca();

        return perdasDisponiveis
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
        if (LimitesPON.aproximadamenteZero(divisor)) {
            throw new IllegalArgumentException(mensagem);
        }
    }

    private double normalizarValorProximoDeZero(
            double valor
    ) {
        if (LimitesPON.aproximadamenteZero(valor)) {
            return 0.0;
        }

        return valor;
    }

    private DetalheFormula montarDetalheFormula(
            ParametroCalculavel parametro,
            ProjetoPON projeto
    ) {
        return switch (parametro) {
            case POTENCIA_TRANSMISSAO ->
                    new DetalheFormula(
                            "Ptx = Srx + (α × L) + (Pc × Nc) + Ps + M",
                            "Ptx = "
                                    + formatar(projeto.getSensibilidadeReceptor())
                                    + " + ("
                                    + formatar(projeto.getAtenuacaoFibra())
                                    + " × "
                                    + formatar(projeto.getComprimentoFibra())
                                    + ") + ("
                                    + formatar(projeto.getPerdaPorConector())
                                    + " × "
                                    + projeto.getNumeroDeConectores()
                                    + ") + "
                                    + formatar(projeto.getPerdaPorSplitter())
                                    + " + "
                                    + formatar(projeto.getMargemDeSeguranca())
                    );

            case SENSIBILIDADE_RECEPTOR ->
                    new DetalheFormula(
                            "Srx = Ptx - (α × L) - (Pc × Nc) - Ps - M",
                            "Srx = "
                                    + formatar(projeto.getPotenciaTransmissao())
                                    + " - ("
                                    + formatar(projeto.getAtenuacaoFibra())
                                    + " × "
                                    + formatar(projeto.getComprimentoFibra())
                                    + ") - ("
                                    + formatar(projeto.getPerdaPorConector())
                                    + " × "
                                    + projeto.getNumeroDeConectores()
                                    + ") - "
                                    + formatar(projeto.getPerdaPorSplitter())
                                    + " - "
                                    + formatar(projeto.getMargemDeSeguranca())
                    );

            case ATENUACAO_FIBRA ->
                    new DetalheFormula(
                            "α = [Ptx - Srx - (Pc × Nc) - Ps - M] / L",
                            "α = ["
                                    + formatar(projeto.getPotenciaTransmissao())
                                    + " - ("
                                    + formatar(projeto.getSensibilidadeReceptor())
                                    + ") - ("
                                    + formatar(projeto.getPerdaPorConector())
                                    + " × "
                                    + projeto.getNumeroDeConectores()
                                    + ") - "
                                    + formatar(projeto.getPerdaPorSplitter())
                                    + " - "
                                    + formatar(projeto.getMargemDeSeguranca())
                                    + "] / "
                                    + formatar(projeto.getComprimentoFibra())
                    );

            case COMPRIMENTO_FIBRA ->
                    new DetalheFormula(
                            "L = [Ptx - Srx - (Pc × Nc) - Ps - M] / α",
                            "L = ["
                                    + formatar(projeto.getPotenciaTransmissao())
                                    + " - ("
                                    + formatar(projeto.getSensibilidadeReceptor())
                                    + ") - ("
                                    + formatar(projeto.getPerdaPorConector())
                                    + " × "
                                    + projeto.getNumeroDeConectores()
                                    + ") - "
                                    + formatar(projeto.getPerdaPorSplitter())
                                    + " - "
                                    + formatar(projeto.getMargemDeSeguranca())
                                    + "] / "
                                    + formatar(projeto.getAtenuacaoFibra())
                    );

            case PERDA_POR_CONECTOR ->
                    new DetalheFormula(
                            "Pc = [Ptx - Srx - (α × L) - Ps - M] / Nc",
                            "Pc = ["
                                    + formatar(projeto.getPotenciaTransmissao())
                                    + " - ("
                                    + formatar(projeto.getSensibilidadeReceptor())
                                    + ") - ("
                                    + formatar(projeto.getAtenuacaoFibra())
                                    + " × "
                                    + formatar(projeto.getComprimentoFibra())
                                    + ") - "
                                    + formatar(projeto.getPerdaPorSplitter())
                                    + " - "
                                    + formatar(projeto.getMargemDeSeguranca())
                                    + "] / "
                                    + projeto.getNumeroDeConectores()
                    );

            case NUMERO_CONECTORES ->
                    new DetalheFormula(
                            "Nc = [Ptx - Srx - (α × L) - Ps - M] / Pc",
                            "Nc = ["
                                    + formatar(projeto.getPotenciaTransmissao())
                                    + " - ("
                                    + formatar(projeto.getSensibilidadeReceptor())
                                    + ") - ("
                                    + formatar(projeto.getAtenuacaoFibra())
                                    + " × "
                                    + formatar(projeto.getComprimentoFibra())
                                    + ") - "
                                    + formatar(projeto.getPerdaPorSplitter())
                                    + " - "
                                    + formatar(projeto.getMargemDeSeguranca())
                                    + "] / "
                                    + formatar(projeto.getPerdaPorConector())
                    );

            case PERDA_POR_SPLITTER ->
                    new DetalheFormula(
                            "Ps = Ptx - Srx - (α × L) - (Pc × Nc) - M",
                            "Ps = "
                                    + formatar(projeto.getPotenciaTransmissao())
                                    + " - ("
                                    + formatar(projeto.getSensibilidadeReceptor())
                                    + ") - ("
                                    + formatar(projeto.getAtenuacaoFibra())
                                    + " × "
                                    + formatar(projeto.getComprimentoFibra())
                                    + ") - ("
                                    + formatar(projeto.getPerdaPorConector())
                                    + " × "
                                    + projeto.getNumeroDeConectores()
                                    + ") - "
                                    + formatar(projeto.getMargemDeSeguranca())
                    );

            case MARGEM_SEGURANCA ->
                    new DetalheFormula(
                            "M = Ptx - Srx - (α × L) - (Pc × Nc) - Ps",
                            "M = "
                                    + formatar(projeto.getPotenciaTransmissao())
                                    + " - ("
                                    + formatar(projeto.getSensibilidadeReceptor())
                                    + ") - ("
                                    + formatar(projeto.getAtenuacaoFibra())
                                    + " × "
                                    + formatar(projeto.getComprimentoFibra())
                                    + ") - ("
                                    + formatar(projeto.getPerdaPorConector())
                                    + " × "
                                    + projeto.getNumeroDeConectores()
                                    + ") - "
                                    + formatar(projeto.getPerdaPorSplitter())
                    );

            case NENHUM ->
                    throw new IllegalArgumentException(
                            "Nenhuma fórmula foi selecionada."
                    );
        };
    }

    private String formatar(double valor) {
        return String.format(
                LOCALE_BR,
                "%.2f",
                valor
        );
    }

    private record DetalheFormula(
            String formula,
            String substituicao
    ) {
    }
}