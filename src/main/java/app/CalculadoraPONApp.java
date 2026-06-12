package app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.ProjetoPON;
import model.ResultadoCalculo;
import service.CalculadoraService;

import java.net.URL;
import java.util.List;

public class CalculadoraPONApp extends Application {

    private final CalculadoraService calculadoraService =
            new CalculadoraService();

    private final TextField potenciaTxField =
            new TextField();

    private final TextField sensibilidadeRxField =
            new TextField();

    private final TextField atenuacaoFibraField =
            new TextField();

    private final TextField comprimentoFibraField =
            new TextField();

    private final TextField perdasConectoresField =
            new TextField();

    private final TextField quantidadeConectoresField =
            new TextField();

    private final TextField perdasSplittersField =
            new TextField();

    private final TextField margemSegurancaField =
            new TextField();

    private final TextArea resultadoArea =
            new TextArea();

    @Override
    public void start(Stage stage) {
        stage.setTitle(
                "Calculadora de Orçamento de Potência PON"
        );

        configurarCampos();

        StackPane rootPane = new StackPane();
        rootPane.setId("background-pane");
        rootPane.setPadding(new Insets(25));

        BorderPane layout = new BorderPane();
        layout.setId("main-layout");

        layout.setTop(criarCabecalho());
        layout.setCenter(criarConteudo());

        rootPane.getChildren().add(layout);

        Scene scene = new Scene(
                rootPane,
                1280,
                800
        );

        URL css = getClass().getResource(
                "/style.css"
        );

        if (css != null) {
            scene.getStylesheets().add(
                    css.toExternalForm()
            );
        }

        stage.setScene(scene);
        stage.setMinWidth(1150);
        stage.setMinHeight(720);
        stage.show();
        stage.centerOnScreen();
    }

    private VBox criarCabecalho() {
        VBox cabecalho = new VBox(8);
        cabecalho.setId("header-box");
        cabecalho.setAlignment(Pos.CENTER);

        Label titulo = new Label(
                "Calculadora de Orçamento de Potência PON"
        );

        titulo.setId("title-label");

        Label subtitulo = new Label(
                "Informe todos os parâmetros ou deixe "
                        + "exatamente um campo vazio para calculá-lo"
        );

        subtitulo.setId("subtitle-label");
        subtitulo.setWrapText(true);

        cabecalho.getChildren().addAll(
                titulo,
                subtitulo
        );

        return cabecalho;
    }

    private HBox criarConteudo() {
        HBox conteudo = new HBox(35);
        conteudo.setPadding(
                new Insets(35, 40, 40, 40)
        );

        conteudo.setAlignment(Pos.TOP_CENTER);

        VBox formulario = criarFormulario();
        VBox resultado = criarResultado();

        conteudo.getChildren().addAll(
                formulario,
                resultado
        );

        HBox.setHgrow(
                formulario,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                resultado,
                Priority.ALWAYS
        );

        return conteudo;
    }

    private VBox criarFormulario() {
        VBox coluna = new VBox(20);
        coluna.setId("left-column");
        coluna.setMinWidth(620);

        GridPane grid = new GridPane();
        grid.setHgap(18);
        grid.setVgap(17);

        ColumnConstraints rotulos =
                new ColumnConstraints();

        rotulos.setMinWidth(300);
        rotulos.setPrefWidth(315);

        ColumnConstraints campos =
                new ColumnConstraints();

        campos.setMinWidth(280);
        campos.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(
                rotulos,
                campos
        );

        adicionarCampo(
                grid,
                0,
                "📡",
                "Potência de Transmissão (dBm):",
                potenciaTxField
        );

        adicionarCampo(
                grid,
                1,
                "⚡",
                "Sensibilidade de Recepção (dBm):",
                sensibilidadeRxField
        );

        adicionarCampo(
                grid,
                2,
                "🛠",
                "Atenuação da Fibra (dB/km):",
                atenuacaoFibraField
        );

        adicionarCampo(
                grid,
                3,
                "📏",
                "Comprimento da Fibra (km):",
                comprimentoFibraField
        );

        HBox conectores = new HBox(
                8,
                perdasConectoresField,
                new Label("Qtd:"),
                quantidadeConectoresField
        );

        conectores.setAlignment(
                Pos.CENTER_LEFT
        );

        HBox.setHgrow(
                perdasConectoresField,
                Priority.ALWAYS
        );

        adicionarCampo(
                grid,
                4,
                "🔌",
                "Perdas por Conectores (dB):",
                conectores
        );

        adicionarCampo(
                grid,
                5,
                "🔀",
                "Perdas por Splitters (dB):",
                perdasSplittersField
        );

        adicionarCampo(
                grid,
                6,
                "🛡",
                "Margem de Segurança (dB):",
                margemSegurancaField
        );

        Button analisarButton =
                new Button("Analisar Projeto");

        analisarButton.setMaxWidth(
                Double.MAX_VALUE
        );

        analisarButton.setOnAction(
                event -> executarCalculo()
        );

        Button limparButton =
                new Button("Limpar Formulário");

        limparButton.setMaxWidth(
                Double.MAX_VALUE
        );

        limparButton.getStyleClass().add(
                "secondary-button"
        );

        limparButton.setOnAction(
                event -> limparFormulario()
        );

        HBox botoes = new HBox(
                12,
                analisarButton,
                limparButton
        );

        HBox.setHgrow(
                analisarButton,
                Priority.ALWAYS
        );

        HBox.setHgrow(
                limparButton,
                Priority.ALWAYS
        );

        coluna.getChildren().addAll(
                grid,
                botoes
        );

        return coluna;
    }

    private VBox criarResultado() {
        VBox coluna = new VBox(12);
        coluna.setId("right-column");
        coluna.setMinWidth(430);

        Label titulo = new Label(
                "Análise do Projeto"
        );

        titulo.setId("result-title-label");

        resultadoArea.setId("result-area");
        resultadoArea.setEditable(false);
        resultadoArea.setWrapText(true);
        resultadoArea.setPrefHeight(500);

        resultadoArea.setText(
                """
                Aguardando análise.

                Preencha todos os campos para verificar a viabilidade
                ou deixe exatamente um campo vazio para calculá-lo.
                """
        );

        coluna.getChildren().addAll(
                titulo,
                resultadoArea
        );

        VBox.setVgrow(
                resultadoArea,
                Priority.ALWAYS
        );

        return coluna;
    }

    private void configurarCampos() {
        configurarCampo(
                potenciaTxField,
                "Ex.: 5"
        );

        configurarCampo(
                sensibilidadeRxField,
                "Ex.: -28"
        );

        configurarCampo(
                atenuacaoFibraField,
                "Ex.: 0,35"
        );

        configurarCampo(
                comprimentoFibraField,
                "Ex.: 10"
        );

        configurarCampo(
                perdasConectoresField,
                "Ex.: 0,5"
        );

        configurarCampo(
                quantidadeConectoresField,
                "Ex.: 4"
        );

        configurarCampo(
                perdasSplittersField,
                "Ex.: 7,2"
        );

        configurarCampo(
                margemSegurancaField,
                "Ex.: 3"
        );

        quantidadeConectoresField.setPrefWidth(85);
        quantidadeConectoresField.setMaxWidth(100);
    }

    private void configurarCampo(
            TextField campo,
            String exemplo
    ) {
        campo.setPromptText(exemplo);
        campo.setPrefHeight(48);
        campo.setMaxWidth(Double.MAX_VALUE);
    }

    private void adicionarCampo(
            GridPane grid,
            int linha,
            String icone,
            String texto,
            Node campo
    ) {
        Label iconeLabel = new Label(icone);

        iconeLabel.setMinWidth(30);
        iconeLabel.setPrefWidth(30);
        iconeLabel.setTextOverrun(
                OverrunStyle.CLIP
        );

        Label textoLabel = new Label(texto);

        textoLabel.setMinWidth(260);
        textoLabel.setTextOverrun(
                OverrunStyle.CLIP
        );

        HBox rotulo = new HBox(
                8,
                iconeLabel,
                textoLabel
        );

        rotulo.setAlignment(
                Pos.CENTER_LEFT
        );

        grid.add(rotulo, 0, linha);
        grid.add(campo, 1, linha);

        GridPane.setHgrow(
                campo,
                Priority.ALWAYS
        );
    }

    private void executarCalculo() {
        resultadoArea.getStyleClass().removeAll(
                "result-success",
                "result-error",
                "result-neutral"
        );

        try {
            ProjetoPON projeto = lerEntradas();

            ResultadoCalculo resultado =
                    calculadoraService.analisar(projeto);

            if (!resultado.isSucesso()) {
                resultadoArea.getStyleClass().add(
                        "result-error"
                );
            } else if (
                    resultado.isCalculouVariavel()
                            || resultado.isProjetoViavel()
            ) {
                resultadoArea.getStyleClass().add(
                        "result-success"
                );
            } else {
                resultadoArea.getStyleClass().add(
                        "result-error"
                );
            }

            resultadoArea.setText(
                    montarTextoResultado(resultado)
            );
        } catch (IllegalArgumentException exception) {
            resultadoArea.getStyleClass().add(
                    "result-error"
            );

            resultadoArea.setText(
                    "ERRO DE ENTRADA\n\n"
                            + exception.getMessage()
            );
        }
    }

    private String montarTextoResultado(
            ResultadoCalculo resultado
    ) {
        StringBuilder texto =
                new StringBuilder(
                        resultado.getMensagem()
                );

        List<String> alertas =
                resultado.getAlertas();

        if (!alertas.isEmpty()) {
            texto.append(
                    "\n\n⚠ ALERTAS\n\n"
            );

            for (String alerta : alertas) {
                texto.append("• ")
                        .append(alerta)
                        .append("\n");
            }
        }

        return texto.toString();
    }

    private ProjetoPON lerEntradas() {
        ProjetoPON projeto = new ProjetoPON();

        projeto.setPotenciaTransmissao(
                lerDouble(
                        potenciaTxField,
                        "Potência de transmissão"
                )
        );

        projeto.setSensibilidadeReceptor(
                lerDouble(
                        sensibilidadeRxField,
                        "Sensibilidade do receptor"
                )
        );

        projeto.setAtenuacaoFibra(
                lerDouble(
                        atenuacaoFibraField,
                        "Atenuação da fibra"
                )
        );

        projeto.setComprimentoFibra(
                lerDouble(
                        comprimentoFibraField,
                        "Comprimento da fibra"
                )
        );

        projeto.setPerdaPorConector(
                lerDouble(
                        perdasConectoresField,
                        "Perda por conector"
                )
        );

        projeto.setNumeroDeConectores(
                lerInteger(
                        quantidadeConectoresField,
                        "Quantidade de conectores"
                )
        );

        projeto.setPerdaPorSplitter(
                lerDouble(
                        perdasSplittersField,
                        "Perda por splitters"
                )
        );

        projeto.setMargemDeSeguranca(
                lerDouble(
                        margemSegurancaField,
                        "Margem de segurança"
                )
        );

        return projeto;
    }

    private Double lerDouble(
            TextField campo,
            String nomeCampo
    ) {
        String texto = campo.getText();

        if (texto == null || texto.isBlank()) {
            return null;
        }

        try {
            return Double.parseDouble(
                    texto.trim().replace(",", ".")
            );
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    nomeCampo
                            + " deve conter um número válido."
            );
        }
    }

    private Integer lerInteger(
            TextField campo,
            String nomeCampo
    ) {
        String texto = campo.getText();

        if (texto == null || texto.isBlank()) {
            return null;
        }

        try {
            return Integer.parseInt(
                    texto.trim()
            );
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(
                    nomeCampo
                            + " deve conter um número inteiro."
            );
        }
    }

    private void limparFormulario() {
        potenciaTxField.clear();
        sensibilidadeRxField.clear();
        atenuacaoFibraField.clear();
        comprimentoFibraField.clear();
        perdasConectoresField.clear();
        quantidadeConectoresField.clear();
        perdasSplittersField.clear();
        margemSegurancaField.clear();

        resultadoArea.getStyleClass().removeAll(
                "result-success",
                "result-error",
                "result-neutral"
        );

        resultadoArea.setText(
                """
                Formulário limpo.

                Preencha os parâmetros para realizar uma nova análise.
                """
        );

        potenciaTxField.requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
