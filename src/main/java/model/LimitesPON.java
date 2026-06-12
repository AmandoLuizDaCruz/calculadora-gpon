package model;

/**
 * Centraliza as faixas e limites adotados pelo projeto.
 *
 * As faixas convencionais geram alertas, mas não impedem
 * necessariamente o cálculo.
 *
 * Os limites físicos, quando existentes, geram erros.
 */
public final class LimitesPON {

    public static final double EPSILON = 0.000000001;

    public static final double POTENCIA_TX_MIN_FISICA = -50.0;
    public static final double POTENCIA_TX_MAX_FISICA = 50.0;

    public static final double POTENCIA_TX_MIN_CONVENCIONAL = -10.0;
    public static final double POTENCIA_TX_MAX_CONVENCIONAL = 10.0;

    public static final double SENSIBILIDADE_MIN_FISICA = -60.0;
    public static final double SENSIBILIDADE_MAX_FISICA = 10.0;

    public static final double SENSIBILIDADE_MIN_CONVENCIONAL = -40.0;
    public static final double SENSIBILIDADE_MAX_CONVENCIONAL = -10.0;

    public static final double ATENUACAO_MIN_CONVENCIONAL = 0.1;
    public static final double ATENUACAO_MAX_CONVENCIONAL = 1.0;

    public static final double COMPRIMENTO_MAX_CONVENCIONAL = 60.0;

    public static final double PERDA_CONECTOR_MAX_CONVENCIONAL = 2.0;

    public static final int NUMERO_CONECTORES_MAX_CONVENCIONAL = 24;

    public static final double PERDA_SPLITTER_MAX_CONVENCIONAL = 35.0;

    public static final double MARGEM_MIN_CONVENCIONAL = 2.0;
    public static final double MARGEM_MAX_CONVENCIONAL = 10.0;

    private LimitesPON() {
        throw new UnsupportedOperationException(
                "Esta classe não pode ser instanciada."
        );
    }

    public static boolean aproximadamenteZero(double valor) {
        return Math.abs(valor) < EPSILON;
    }

    public static boolean aproximadamenteInteiro(double valor) {
        return Math.abs(valor - Math.rint(valor)) < EPSILON;
    }
}