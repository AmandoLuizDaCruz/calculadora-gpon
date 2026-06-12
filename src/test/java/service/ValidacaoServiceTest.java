package service;

import model.ParametroCalculavel;
import model.ProjetoPON;
import model.ResultadoValidacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidacaoServiceTest {

    private ValidacaoService service;

    @BeforeEach
    void configurar() {
        service = new ValidacaoService();
    }

    @Test
    void deveRejeitarProjetoNulo() {
        ResultadoValidacao resultado =
                service.validar(null);

        assertFalse(resultado.isValido());
        assertFalse(resultado.getErros().isEmpty());
    }

    @Test
    void deveAceitarProjetoCompleto() {
        ResultadoValidacao resultado =
                service.validar(criarProjetoValido());

        assertTrue(resultado.isValido());
    }

    @Test
    void deveAceitarExatamenteUmCampoVazio() {
        ProjetoPON projeto = criarProjetoValido();
        projeto.setComprimentoFibra(null);

        ResultadoValidacao resultado =
                service.validar(projeto);

        assertTrue(resultado.isValido());
    }

    @Test
    void deveRejeitarDoisCamposVazios() {
        ProjetoPON projeto = criarProjetoValido();

        projeto.setComprimentoFibra(null);
        projeto.setMargemDeSeguranca(null);

        ResultadoValidacao resultado =
                service.validar(projeto);

        assertFalse(resultado.isValido());
    }

    @Test
    void deveRejeitarTodosOsCamposVazios() {
        ProjetoPON projeto = new ProjetoPON();

        ResultadoValidacao resultado =
                service.validar(projeto);

        assertFalse(resultado.isValido());

        assertTrue(
                resultado.getErros()
                        .get(0)
                        .contains("8 parâmetros vazios")
        );
    }

    @Test
    void deveRejeitarAtenuacaoNegativa() {
        ProjetoPON projeto = criarProjetoValido();
        projeto.setAtenuacaoFibra(-0.35);

        ResultadoValidacao resultado =
                service.validar(projeto);

        assertFalse(resultado.isValido());
    }

    @Test
    void deveRejeitarQuantidadeNegativaDeConectores() {
        ProjetoPON projeto = criarProjetoValido();
        projeto.setNumeroDeConectores(-1);

        ResultadoValidacao resultado =
                service.validar(projeto);

        assertFalse(resultado.isValido());
    }

    @Test
    void deveAlertarPotenciaForaDoConvencional() {
        ProjetoPON projeto = criarProjetoValido();
        projeto.setPotenciaTransmissao(20.0);

        ResultadoValidacao resultado =
                service.validar(projeto);

        assertTrue(resultado.isValido());
        assertFalse(resultado.getAlertas().isEmpty());
    }

    @Test
    void deveRejeitarSensibilidadeForaDoLimiteFisico() {
        ProjetoPON projeto = criarProjetoValido();
        projeto.setSensibilidadeReceptor(-100.0);

        ResultadoValidacao resultado =
                service.validar(projeto);

        assertFalse(resultado.isValido());
    }

    @Test
    void deveAlertarMargemForaDoConvencional() {
        ProjetoPON projeto = criarProjetoValido();
        projeto.setMargemDeSeguranca(15.0);

        ResultadoValidacao resultado =
                service.validar(projeto);

        assertTrue(resultado.isValido());
        assertFalse(resultado.getAlertas().isEmpty());
    }

    @Test
    void deveAlertarQuantidadeCalculadaFracionaria() {
        ResultadoValidacao resultado =
                service.validarValorCalculado(
                        ParametroCalculavel.NUMERO_CONECTORES,
                        7.43
                );

        assertTrue(resultado.isValido());
        assertFalse(resultado.getAlertas().isEmpty());
    }

    @Test
    void deveRejeitarResultadoCalculadoInfinito() {
        ResultadoValidacao resultado =
                service.validarValorCalculado(
                        ParametroCalculavel.COMPRIMENTO_FIBRA,
                        Double.POSITIVE_INFINITY
                );

        assertFalse(resultado.isValido());
    }

    private ProjetoPON criarProjetoValido() {
        ProjetoPON projeto = new ProjetoPON();

        projeto.setPotenciaTransmissao(5.0);
        projeto.setSensibilidadeReceptor(-28.0);
        projeto.setAtenuacaoFibra(0.35);
        projeto.setComprimentoFibra(10.0);
        projeto.setPerdaPorConector(0.5);
        projeto.setNumeroDeConectores(4);
        projeto.setPerdaPorSplitter(7.2);
        projeto.setMargemDeSeguranca(3.0);

        return projeto;
    }
}