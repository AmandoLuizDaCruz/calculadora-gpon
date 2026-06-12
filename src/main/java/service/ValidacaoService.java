package service;

import model.ProjetoPON;
import model.ResultadoValidacao;

/**
 * Responsável por validar os dados informados pelo usuário.
 */
public class ValidacaoService {

    public ResultadoValidacao validar(ProjetoPON projeto) {
        ResultadoValidacao resultado =
                new ResultadoValidacao();

        if (projeto == null) {
            resultado.adicionarErro(
                    "Os dados do projeto não foram informados."
            );

            return resultado;
        }

        validarQuantidadeDeCamposVazios(
                projeto,
                resultado
        );

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
                            + "Preencha todos os campos e deixe "
                            + "vazio somente o parâmetro que deseja calcular."
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

        if (valor < -50 || valor > 50) {
            resultado.adicionarErro(
                    "A potência de transmissão está fora dos limites físicos aceitos."
            );
        } else if (valor < -10 || valor > 10) {
            resultado.adicionarAlerta(
                    "A potência de transmissão informada está fora "
                            + "da faixa convencional de -10 dBm a 10 dBm."
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

        if (valor < -60 || valor > 10) {
            resultado.adicionarErro(
                    "A sensibilidade do receptor está fora dos limites físicos aceitos."
            );
        } else if (valor < -40 || valor > -10) {
            resultado.adicionarAlerta(
                    "A sensibilidade informada está fora da faixa "
                            + "convencional de -40 dBm a -10 dBm."
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

        if (valor < 0.1 || valor > 1.0) {
            resultado.adicionarAlerta(
                    "A atenuação da fibra está fora da faixa "
                            + "convencional de 0,1 dB/km a 1,0 dB/km."
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

        if (valor > 60) {
            resultado.adicionarAlerta(
                    "O comprimento da fibra é superior a 60 km "
                            + "e deve ser analisado cuidadosamente."
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

        if (valor > 2) {
            resultado.adicionarAlerta(
                    "A perda por conector está acima de 2 dB."
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

        if (valor > 24) {
            resultado.adicionarAlerta(
                    "A quantidade de conectores é superior a 24."
            );
        }
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

        if (valor > 35) {
            resultado.adicionarAlerta(
                    "A perda informada para os splitters é superior a 35 dB."
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

        if (valor < 2 || valor > 10) {
            resultado.adicionarAlerta(
                    "A margem de segurança está fora da faixa "
                            + "convencional de 2 dB a 10 dB."
            );
        }
    }
}