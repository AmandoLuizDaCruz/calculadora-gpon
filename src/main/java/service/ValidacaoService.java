package service;

import model.LimitesPON;
import model.ParametroCalculavel;
import model.ProjetoPON;
import model.ResultadoValidacao;

/**
 * Responsável pelas validações dos dados informados
 * e dos valores calculados pelo sistema.
 */
public class ValidacaoService {

    public ResultadoValidacao validar(ProjetoPON projeto) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (projeto == null) {
            resultado.adicionarErro(
                    "Os dados do projeto não foram informados."
            );

            return resultado;
        }

        validarQuantidadeDeCamposVazios(projeto, resultado);

        validarPotenciaTransmissao(
                projeto.getPotenciaTransmissao(),
                resultado
        );

        validarSensibilidade(
                projeto.getSensibilidadeReceptor(),
                resultado
        );

        validarAtenuacao(
                projeto.getAtenuacaoFibra(),
                resultado
        );

        validarComprimento(
                projeto.getComprimentoFibra(),
                resultado
        );

        validarPerdaConector(
                projeto.getPerdaPorConector(),
                resultado
        );

        validarQuantidadeConectores(
                projeto.getNumeroDeConectores(),
                resultado
        );

        validarPerdaSplitter(
                projeto.getPerdaPorSplitter(),
                resultado
        );

        validarMargem(
                projeto.getMargemDeSeguranca(),
                resultado
        );

        return resultado;
    }

    /**
     * Valida novamente um valor produzido pelo cálculo.
     *
     * Isso evita que o sistema aceite resultados negativos,
     * infinitos ou fisicamente incompatíveis.
     */
    public ResultadoValidacao validarValorCalculado(
            ParametroCalculavel parametro,
            double valor
    ) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (parametro == null
                || parametro == ParametroCalculavel.NENHUM) {
            resultado.adicionarErro(
                    "O parâmetro calculado não foi identificado."
            );

            return resultado;
        }

        if (!Double.isFinite(valor)) {
            resultado.adicionarErro(
                    "O resultado calculado não é um número válido."
            );

            return resultado;
        }

        switch (parametro) {
            case POTENCIA_TRANSMISSAO ->
                    validarPotenciaTransmissao(valor, resultado);

            case SENSIBILIDADE_RECEPTOR ->
                    validarSensibilidade(valor, resultado);

            case ATENUACAO_FIBRA ->
                    validarAtenuacao(valor, resultado);

            case COMPRIMENTO_FIBRA ->
                    validarComprimento(valor, resultado);

            case PERDA_POR_CONECTOR ->
                    validarPerdaConector(valor, resultado);

            case NUMERO_CONECTORES ->
                    validarQuantidadeCalculadaDeConectores(
                            valor,
                            resultado
                    );

            case PERDA_POR_SPLITTER ->
                    validarPerdaSplitter(valor, resultado);

            case MARGEM_SEGURANCA ->
                    validarMargem(valor, resultado);

            case NENHUM ->
                    resultado.adicionarErro(
                            "Nenhum parâmetro foi calculado."
                    );
        }

        return resultado;
    }

    private void validarQuantidadeDeCamposVazios(
            ProjetoPON projeto,
            ResultadoValidacao resultado
    ) {
        int quantidadeAusentes =
                projeto.contarParametrosAusentes();

        if (quantidadeAusentes > 1) {
            resultado.adicionarErro(
                    "Existem " + quantidadeAusentes
                            + " parâmetros vazios. "
                            + "Preencha todos os campos e deixe vazio "
                            + "somente o parâmetro que deseja calcular."
            );
        }
    }

    private void validarPotenciaTransmissao(
            Double valor,
            ResultadoValidacao resultado
    ) {
        if (valor == null) {
            return;
        }

        if (!Double.isFinite(valor)) {
            resultado.adicionarErro(
                    "A potência de transmissão deve ser um número válido."
            );

            return;
        }

        if (valor < LimitesPON.POTENCIA_TX_MIN_FISICA
                || valor > LimitesPON.POTENCIA_TX_MAX_FISICA) {
            resultado.adicionarErro(
                    "A potência de transmissão está fora dos limites "
                            + "físicos adotados pelo projeto."
            );

            return;
        }

        if (valor < LimitesPON.POTENCIA_TX_MIN_CONVENCIONAL
                || valor > LimitesPON.POTENCIA_TX_MAX_CONVENCIONAL) {
            resultado.adicionarAlerta(
                    "A potência de transmissão está fora da faixa "
                            + "convencional adotada de "
                            + LimitesPON.POTENCIA_TX_MIN_CONVENCIONAL
                            + " dBm a "
                            + LimitesPON.POTENCIA_TX_MAX_CONVENCIONAL
                            + " dBm."
            );
        }
    }

    private void validarSensibilidade(
            Double valor,
            ResultadoValidacao resultado
    ) {
        if (valor == null) {
            return;
        }

        if (!Double.isFinite(valor)) {
            resultado.adicionarErro(
                    "A sensibilidade do receptor deve ser um número válido."
            );

            return;
        }

        if (valor < LimitesPON.SENSIBILIDADE_MIN_FISICA
                || valor > LimitesPON.SENSIBILIDADE_MAX_FISICA) {
            resultado.adicionarErro(
                    "A sensibilidade do receptor está fora dos limites "
                            + "físicos adotados pelo projeto."
            );

            return;
        }

        if (valor < LimitesPON.SENSIBILIDADE_MIN_CONVENCIONAL
                || valor > LimitesPON.SENSIBILIDADE_MAX_CONVENCIONAL) {
            resultado.adicionarAlerta(
                    "A sensibilidade do receptor está fora da faixa "
                            + "convencional adotada de "
                            + LimitesPON.SENSIBILIDADE_MIN_CONVENCIONAL
                            + " dBm a "
                            + LimitesPON.SENSIBILIDADE_MAX_CONVENCIONAL
                            + " dBm."
            );
        }
    }

    private void validarAtenuacao(
            Double valor,
            ResultadoValidacao resultado
    ) {
        if (valor == null) {
            return;
        }

        if (!Double.isFinite(valor) || valor <= 0) {
            resultado.adicionarErro(
                    "A atenuação da fibra deve ser maior que zero."
            );

            return;
        }

        if (valor < LimitesPON.ATENUACAO_MIN_CONVENCIONAL
                || valor > LimitesPON.ATENUACAO_MAX_CONVENCIONAL) {
            resultado.adicionarAlerta(
                    "A atenuação da fibra está fora da faixa "
                            + "convencional adotada de "
                            + LimitesPON.ATENUACAO_MIN_CONVENCIONAL
                            + " dB/km a "
                            + LimitesPON.ATENUACAO_MAX_CONVENCIONAL
                            + " dB/km."
            );
        }
    }

    private void validarComprimento(
            Double valor,
            ResultadoValidacao resultado
    ) {
        if (valor == null) {
            return;
        }

        if (!Double.isFinite(valor) || valor < 0) {
            resultado.adicionarErro(
                    "O comprimento da fibra não pode ser negativo."
            );

            return;
        }

        if (valor > LimitesPON.COMPRIMENTO_MAX_CONVENCIONAL) {
            resultado.adicionarAlerta(
                    "O comprimento da fibra é superior ao limite "
                            + "convencional adotado de "
                            + LimitesPON.COMPRIMENTO_MAX_CONVENCIONAL
                            + " km."
            );
        }
    }

    private void validarPerdaConector(
            Double valor,
            ResultadoValidacao resultado
    ) {
        if (valor == null) {
            return;
        }

        if (!Double.isFinite(valor) || valor < 0) {
            resultado.adicionarErro(
                    "A perda por conector não pode ser negativa."
            );

            return;
        }

        if (valor > LimitesPON.PERDA_CONECTOR_MAX_CONVENCIONAL) {
            resultado.adicionarAlerta(
                    "A perda por conector é superior ao limite "
                            + "convencional adotado de "
                            + LimitesPON.PERDA_CONECTOR_MAX_CONVENCIONAL
                            + " dB."
            );
        }
    }

    private void validarQuantidadeConectores(
            Integer valor,
            ResultadoValidacao resultado
    ) {
        if (valor == null) {
            return;
        }

        if (valor < 0) {
            resultado.adicionarErro(
                    "A quantidade de conectores não pode ser negativa."
            );

            return;
        }

        if (valor > LimitesPON.NUMERO_CONECTORES_MAX_CONVENCIONAL) {
            resultado.adicionarAlerta(
                    "A quantidade de conectores é superior ao limite "
                            + "convencional adotado de "
                            + LimitesPON.NUMERO_CONECTORES_MAX_CONVENCIONAL
                            + " conectores."
            );
        }
    }

    private void validarQuantidadeCalculadaDeConectores(
            double valor,
            ResultadoValidacao resultado
    ) {
        if (!Double.isFinite(valor) || valor < 0) {
            resultado.adicionarErro(
                    "A quantidade calculada de conectores "
                            + "não pode ser negativa."
            );

            return;
        }

        if (valor > Integer.MAX_VALUE) {
            resultado.adicionarErro(
                    "A quantidade calculada de conectores é excessivamente alta."
            );

            return;
        }

        if (!LimitesPON.aproximadamenteInteiro(valor)) {
            resultado.adicionarAlerta(
                    "A quantidade matemática de conectores não é inteira. "
                            + "Será considerada a quantidade máxima inteira "
                            + "que não ultrapassa o orçamento óptico."
            );
        }

        int quantidadeInteira = (int) Math.floor(
                valor + LimitesPON.EPSILON
        );

        validarQuantidadeConectores(
                quantidadeInteira,
                resultado
        );
    }

    private void validarPerdaSplitter(
            Double valor,
            ResultadoValidacao resultado
    ) {
        if (valor == null) {
            return;
        }

        if (!Double.isFinite(valor) || valor < 0) {
            resultado.adicionarErro(
                    "A perda dos splitters não pode ser negativa."
            );

            return;
        }

        if (valor > LimitesPON.PERDA_SPLITTER_MAX_CONVENCIONAL) {
            resultado.adicionarAlerta(
                    "A perda dos splitters é superior ao limite "
                            + "convencional adotado de "
                            + LimitesPON.PERDA_SPLITTER_MAX_CONVENCIONAL
                            + " dB."
            );
        }
    }

    private void validarMargem(
            Double valor,
            ResultadoValidacao resultado
    ) {
        if (valor == null) {
            return;
        }

        if (!Double.isFinite(valor) || valor < 0) {
            resultado.adicionarErro(
                    "A margem de segurança não pode ser negativa."
            );

            return;
        }

        if (valor < LimitesPON.MARGEM_MIN_CONVENCIONAL
                || valor > LimitesPON.MARGEM_MAX_CONVENCIONAL) {
            resultado.adicionarAlerta(
                    "A margem de segurança está fora da faixa "
                            + "convencional adotada de "
                            + LimitesPON.MARGEM_MIN_CONVENCIONAL
                            + " dB a "
                            + LimitesPON.MARGEM_MAX_CONVENCIONAL
                            + " dB."
            );
        }
    }
}  