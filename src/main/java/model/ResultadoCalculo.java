package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa o resultado produzido pela camada de serviço.
 */
public class ResultadoCalculo {

    private final boolean sucesso;
    private final boolean calculouVariavel;
    private final boolean projetoViavel;

    private final ParametroCalculavel parametroCalculado;
    private final Double valorCalculado;

    private final Double perdaFibra;
    private final Double perdaConectores;
    private final Double perdaSplitters;
    private final Double perdasFisicas;
    private final Double potenciaRecebida;
    private final Double margemDisponivel;

    private final String mensagem;
    private final List<String> alertas;

    private ResultadoCalculo(
            boolean sucesso,
            boolean calculouVariavel,
            boolean projetoViavel,
            ParametroCalculavel parametroCalculado,
            Double valorCalculado,
            Double perdaFibra,
            Double perdaConectores,
            Double perdaSplitters,
            Double perdasFisicas,
            Double potenciaRecebida,
            Double margemDisponivel,
            String mensagem,
            List<String> alertas
    ) {
        this.sucesso = sucesso;
        this.calculouVariavel = calculouVariavel;
        this.projetoViavel = projetoViavel;
        this.parametroCalculado = parametroCalculado;
        this.valorCalculado = valorCalculado;
        this.perdaFibra = perdaFibra;
        this.perdaConectores = perdaConectores;
        this.perdaSplitters = perdaSplitters;
        this.perdasFisicas = perdasFisicas;
        this.potenciaRecebida = potenciaRecebida;
        this.margemDisponivel = margemDisponivel;
        this.mensagem = mensagem;

        this.alertas = alertas == null
                ? new ArrayList<>()
                : new ArrayList<>(alertas);
    }

    public static ResultadoCalculo erro(
            String mensagem,
            List<String> alertas
    ) {
        return new ResultadoCalculo(
                false,
                false,
                false,
                ParametroCalculavel.NENHUM,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                mensagem,
                alertas
        );
    }

    public static ResultadoCalculo variavelCalculada(
            ParametroCalculavel parametro,
            double valor,
            String mensagem,
            List<String> alertas
    ) {
        return new ResultadoCalculo(
                true,
                true,
                false,
                parametro,
                valor,
                null,
                null,
                null,
                null,
                null,
                null,
                mensagem,
                alertas
        );
    }

    public static ResultadoCalculo analiseCompleta(
            boolean projetoViavel,
            double perdaFibra,
            double perdaConectores,
            double perdaSplitters,
            double perdasFisicas,
            double potenciaRecebida,
            double margemDisponivel,
            String mensagem,
            List<String> alertas
    ) {
        return new ResultadoCalculo(
                true,
                false,
                projetoViavel,
                ParametroCalculavel.NENHUM,
                null,
                perdaFibra,
                perdaConectores,
                perdaSplitters,
                perdasFisicas,
                potenciaRecebida,
                margemDisponivel,
                mensagem,
                alertas
        );
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public boolean isCalculouVariavel() {
        return calculouVariavel;
    }

    public boolean isProjetoViavel() {
        return projetoViavel;
    }

    public ParametroCalculavel getParametroCalculado() {
        return parametroCalculado;
    }

    public Double getValorCalculado() {
        return valorCalculado;
    }

    public Double getPerdaFibra() {
        return perdaFibra;
    }

    public Double getPerdaConectores() {
        return perdaConectores;
    }

    public Double getPerdaSplitters() {
        return perdaSplitters;
    }

    public Double getPerdasFisicas() {
        return perdasFisicas;
    }

    public Double getPotenciaRecebida() {
        return potenciaRecebida;
    }

    public Double getMargemDisponivel() {
        return margemDisponivel;
    }

    public String getMensagem() {
        return mensagem;
    }

    public List<String> getAlertas() {
        return Collections.unmodifiableList(alertas);
    }
}