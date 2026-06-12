package service;

import model.ParametroCalculavel;
import model.ProjetoPON;
import model.ResultadoCalculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraServiceTest {

    private CalculadoraService service;

    @BeforeEach
    void configurar() {
        service = new CalculadoraService();
    }

    @Test
    void deveAnalisarProjetoCompletoViavel() {
        ProjetoPON projeto = criarProjetoValido();

        ResultadoCalculo resultado =
                service.analisar(projeto);

        assertTrue(resultado.isSucesso());
        assertFalse(resultado.isCalculouVariavel());
        assertTrue(resultado.isProjetoViavel());

        assertEquals(
                3.50,
                resultado.getPerdaFibra(),
                0.001
        );

        assertEquals(
                2.00,
                resultado.getPerdaConectores(),
                0.001
        );

        assertEquals(
                12.70,
                resultado.getPerdasFisicas(),
                0.001
        );
    }

    @Test
    void deveCalcularComprimentoDaFibra() {
        ProjetoPON projeto = criarProjetoValido();
        projeto.setComprimentoFibra(null);

        ResultadoCalculo resultado =
                service.analisar(projeto);

        assertTrue(resultado.isSucesso());
        assertTrue(resultado.isCalculouVariavel());

        assertEquals(
                ParametroCalculavel.COMPRIMENTO_FIBRA,
                resultado.getParametroCalculado()
        );

        assertEquals(
                59.428571,
                resultado.getValorCalculado(),
                0.001
        );
    }

    @Test
    void deveCalcularPotenciaDeTransmissao() {
        ProjetoPON projeto = criarProjetoValido();
        projeto.setPotenciaTransmissao(null);

        ResultadoCalculo resultado =
                service.analisar(projeto);

        assertTrue(resultado.isSucesso());

        assertEquals(
                ParametroCalculavel.POTENCIA_TRANSMISSAO,
                resultado.getParametroCalculado()
        );

        assertEquals(
                -12.30,
                resultado.getValorCalculado(),
                0.001
        );
    }

    @Test
    void deveCalcularMargemDeSeguranca() {
        ProjetoPON projeto = criarProjetoValido();
        projeto.setMargemDeSeguranca(null);

        ResultadoCalculo resultado =
                service.analisar(projeto);

        assertTrue(resultado.isSucesso());

        assertEquals(
                ParametroCalculavel.MARGEM_SEGURANCA,
                resultado.getParametroCalculado()
        );

        assertEquals(
                20.30,
                resultado.getValorCalculado(),
                0.001
        );
    }

    @Test
    void deveRecusarMaisDeUmCampoVazio() {
        ProjetoPON projeto = criarProjetoValido();

        projeto.setComprimentoFibra(null);
        projeto.setMargemDeSeguranca(null);

        ResultadoCalculo resultado =
                service.analisar(projeto);

        assertFalse(resultado.isSucesso());

        assertTrue(
                resultado.getMensagem()
                        .contains("parâmetros vazios")
        );
    }

    @Test
    void deveRecusarResultadoNegativo() {
        ProjetoPON projeto = criarProjetoValido();

        projeto.setPotenciaTransmissao(-30.0);
        projeto.setComprimentoFibra(null);

        ResultadoCalculo resultado =
                service.analisar(projeto);

        assertFalse(resultado.isSucesso());

        assertTrue(
                resultado.getMensagem()
                        .contains("valor negativo")
        );
    }

    @Test
    void deveImpedirDivisaoPorZero() {
        ProjetoPON projeto = criarProjetoValido();

        projeto.setComprimentoFibra(0.0);
        projeto.setAtenuacaoFibra(null);

        ResultadoCalculo resultado =
                service.analisar(projeto);

        assertFalse(resultado.isSucesso());

        assertTrue(
                resultado.getMensagem()
                        .contains("maior que zero")
        );
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