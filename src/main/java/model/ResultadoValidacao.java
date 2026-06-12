package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Armazena erros impeditivos e alertas de valores atípicos.
 */
public class ResultadoValidacao {

    private final List<String> erros;
    private final List<String> alertas;

    public ResultadoValidacao() {
        this.erros = new ArrayList<>();
        this.alertas = new ArrayList<>();
    }

    public void adicionarErro(String erro) {
        if (erro != null && !erro.isBlank()) {
            erros.add(erro);
        }
    }

    public void adicionarAlerta(String alerta) {
        if (alerta != null && !alerta.isBlank()) {
            alertas.add(alerta);
        }
    }

    public boolean isValido() {
        return erros.isEmpty();
    }

    public List<String> getErros() {
        return Collections.unmodifiableList(erros);
    }

    public List<String> getAlertas() {
        return Collections.unmodifiableList(alertas);
    }
}