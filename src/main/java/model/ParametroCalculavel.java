package model;

/**
 * Representa todos os parâmetros que podem ser
 * informados ou calculados pelo programa.
 */
public enum ParametroCalculavel {

    POTENCIA_TRANSMISSAO(
            "Potência de transmissão",
            "dBm"
    ),

    SENSIBILIDADE_RECEPTOR(
            "Sensibilidade do receptor",
            "dBm"
    ),

    ATENUACAO_FIBRA(
            "Atenuação da fibra",
            "dB/km"
    ),

    COMPRIMENTO_FIBRA(
            "Comprimento da fibra",
            "km"
    ),

    PERDA_POR_CONECTOR(
            "Perda por conector",
            "dB"
    ),

    NUMERO_CONECTORES(
            "Quantidade de conectores",
            "unidades"
    ),

    PERDA_POR_SPLITTER(
            "Perda por splitters",
            "dB"
    ),

    MARGEM_SEGURANCA(
            "Margem de segurança",
            "dB"
    ),

    NENHUM(
            "Nenhum",
            ""
    );

    private final String descricao;
    private final String unidade;

    ParametroCalculavel(
            String descricao,
            String unidade
    ) {
        this.descricao = descricao;
        this.unidade = unidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUnidade() {
        return unidade;
    }
}   