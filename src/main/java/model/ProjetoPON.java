package model;

/**
 * Armazena os parâmetros de um projeto de rede óptica PON.
 *
 * O tipo Double é utilizado para permitir valores nulos.
 * Um valor nulo representa um parâmetro que o usuário
 * deseja que o programa calcule.
 */
public class ProjetoPON {

    private Double potenciaTransmissao;
    private Double sensibilidadeReceptor;
    private Double atenuacaoFibra;
    private Double comprimentoFibra;
    private Double perdaPorConector;
    private Integer numeroDeConectores;
    private Double perdaPorSplitter;
    private Double margemDeSeguranca;

    public ProjetoPON() {
    }

    public Double getPotenciaTransmissao() {
        return potenciaTransmissao;
    }

    public void setPotenciaTransmissao(
            Double potenciaTransmissao
    ) {
        this.potenciaTransmissao = potenciaTransmissao;
    }

    public Double getSensibilidadeReceptor() {
        return sensibilidadeReceptor;
    }

    public void setSensibilidadeReceptor(
            Double sensibilidadeReceptor
    ) {
        this.sensibilidadeReceptor = sensibilidadeReceptor;
    }

    public Double getAtenuacaoFibra() {
        return atenuacaoFibra;
    }

    public void setAtenuacaoFibra(
            Double atenuacaoFibra
    ) {
        this.atenuacaoFibra = atenuacaoFibra;
    }

    public Double getComprimentoFibra() {
        return comprimentoFibra;
    }

    public void setComprimentoFibra(
            Double comprimentoFibra
    ) {
        this.comprimentoFibra = comprimentoFibra;
    }

    public Double getPerdaPorConector() {
        return perdaPorConector;
    }

    public void setPerdaPorConector(
            Double perdaPorConector
    ) {
        this.perdaPorConector = perdaPorConector;
    }

    public Integer getNumeroDeConectores() {
        return numeroDeConectores;
    }

    public void setNumeroDeConectores(
            Integer numeroDeConectores
    ) {
        this.numeroDeConectores = numeroDeConectores;
    }

    public Double getPerdaPorSplitter() {
        return perdaPorSplitter;
    }

    public void setPerdaPorSplitter(
            Double perdaPorSplitter
    ) {
        this.perdaPorSplitter = perdaPorSplitter;
    }

    public Double getMargemDeSeguranca() {
        return margemDeSeguranca;
    }

    public void setMargemDeSeguranca(
            Double margemDeSeguranca
    ) {
        this.margemDeSeguranca = margemDeSeguranca;
    }

    /**
     * Conta quantos parâmetros não foram informados.
     */
    public int contarParametrosAusentes() {
        int quantidade = 0;

        if (potenciaTransmissao == null) {
            quantidade++;
        }

        if (sensibilidadeReceptor == null) {
            quantidade++;
        }

        if (atenuacaoFibra == null) {
            quantidade++;
        }

        if (comprimentoFibra == null) {
            quantidade++;
        }

        if (perdaPorConector == null) {
            quantidade++;
        }

        if (numeroDeConectores == null) {
            quantidade++;
        }

        if (perdaPorSplitter == null) {
            quantidade++;
        }

        if (margemDeSeguranca == null) {
            quantidade++;
        }

        return quantidade;
    }

    /**
     * Identifica qual parâmetro está ausente.
     *
     * Deve ser utilizado quando somente um parâmetro
     * estiver vazio.
     */
    public ParametroCalculavel identificarParametroAusente() {
        if (potenciaTransmissao == null) {
            return ParametroCalculavel.POTENCIA_TRANSMISSAO;
        }

        if (sensibilidadeReceptor == null) {
            return ParametroCalculavel.SENSIBILIDADE_RECEPTOR;
        }

        if (atenuacaoFibra == null) {
            return ParametroCalculavel.ATENUACAO_FIBRA;
        }

        if (comprimentoFibra == null) {
            return ParametroCalculavel.COMPRIMENTO_FIBRA;
        }

        if (perdaPorConector == null) {
            return ParametroCalculavel.PERDA_POR_CONECTOR;
        }

        if (numeroDeConectores == null) {
            return ParametroCalculavel.NUMERO_CONECTORES;
        }

        if (perdaPorSplitter == null) {
            return ParametroCalculavel.PERDA_POR_SPLITTER;
        }

        if (margemDeSeguranca == null) {
            return ParametroCalculavel.MARGEM_SEGURANCA;
        }

        return ParametroCalculavel.NENHUM;
    }
}