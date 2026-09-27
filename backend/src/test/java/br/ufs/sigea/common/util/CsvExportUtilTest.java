package br.ufs.sigea.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CsvExportUtilTest {

    @Test
    @DisplayName("Deve gerar CSV com BOM, cabeçalho e linhas simples")
    void shouldGenerateCsvWithBomHeaderAndRows() {
        byte[] bytes = CsvExportUtil.create()
                .header("ID", "NOME", "VALOR")
                .addRow("1", "Matheus", 100)
                .addRow("2", "Ana", 200.5)
                .toByteArray();

        assertThat(bytes).isNotNull();
        String content = new String(bytes, StandardCharsets.UTF_8);

        // Verifica o BOM
        assertThat(content.charAt(0)).isEqualTo('\uFEFF');

        // Remove o BOM e verifica o conteúdo
        String text = content.substring(1);
        assertThat(text).contains("ID,NOME,VALOR\r\n");
        assertThat(text).contains("1,Matheus,100\r\n");
        assertThat(text).contains("2,Ana,200.5\r\n");
    }

    @Test
    @DisplayName("Deve aplicar escape RFC 4180 para vírgulas, aspas e quebras de linha")
    void shouldEscapeSpecialCharactersRfc4180() {
        byte[] bytes = CsvExportUtil.create()
                .header("DESCRICAO", "OBSERVACOES")
                .addRow("Texto com, virgula", "Aspas \"aqui\" e quebra\nde linha")
                .toByteArray();

        String text = new String(bytes, StandardCharsets.UTF_8).substring(1);
        assertThat(text).contains("\"Texto com, virgula\",\"Aspas \"\"aqui\"\" e quebra\nde linha\"");
    }

    @Test
    @DisplayName("Deve tratar valores nulos como strings vazias")
    void shouldHandleNullValuesGracefully() {
        byte[] bytes = CsvExportUtil.create()
                .header("COL1", "COL2")
                .addRow("val1", null)
                .addRows(List.of(List.of("val2", "val3")))
                .toByteArray();

        String text = new String(bytes, StandardCharsets.UTF_8).substring(1);
        assertThat(text).contains("val1,\r\n");
        assertThat(text).contains("val2,val3\r\n");
    }

    @Test
    @DisplayName("Deve permitir criação de CSV vazio sem cabeçalho nem linhas")
    void shouldHandleEmptyCsv() {
        byte[] bytes = CsvExportUtil.create()
                .header((String[]) null)
                .addRow((Object[]) null)
                .addRows(null)
                .toByteArray();

        assertThat(bytes).isNotNull();
        String content = new String(bytes, StandardCharsets.UTF_8);
        assertThat(content).isEqualTo("\uFEFF");
    }

    @Test
    @DisplayName("Deve escapar corretamente via método estático escapeRfc4180")
    void shouldTestStaticEscapeMethod() {
        assertThat(CsvExportUtil.escapeRfc4180(null)).isEqualTo("");
        assertThat(CsvExportUtil.escapeRfc4180("normal")).isEqualTo("normal");
        assertThat(CsvExportUtil.escapeRfc4180("com,virgula")).isEqualTo("\"com,virgula\"");
        assertThat(CsvExportUtil.escapeRfc4180("com\nquebra")).isEqualTo("\"com\nquebra\"");
        assertThat(CsvExportUtil.escapeRfc4180("com\rretorno")).isEqualTo("\"com\rretorno\"");
        assertThat(CsvExportUtil.escapeRfc4180("com\"aspas\"")).isEqualTo("\"com\"\"aspas\"\"\"");
    }
}
