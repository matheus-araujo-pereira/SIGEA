package br.ufs.sigea.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utilitário determinístico para construção e exportação de dados tabulares em formato CSV.
 * Conformidade com a RFC 4180 e inclusão automática de Byte Order Mark (BOM UTF-8)
 * para abertura nativa e sem distorções no Microsoft Excel, R, SPSS e Python (pandas).
 */
public final class CsvExportUtil {

    private static final char BOM = '\uFEFF';
    private static final String LINE_SEPARATOR = "\r\n";
    private static final String COMMA = ",";

    private final List<String> header = new ArrayList<>();
    private final List<List<String>> rows = new ArrayList<>();

    private CsvExportUtil() {
    }

    /**
     * Cria uma nova instância do construtor de CSV.
     *
     * @return Construtor CSV fluente
     */
    public static CsvExportUtil create() {
        return new CsvExportUtil();
    }

    /**
     * Define os cabeçalhos das colunas do arquivo CSV.
     *
     * @param columns Nomes das colunas
     * @return A própria instância para encadeamento fluente
     */
    public CsvExportUtil header(String... columns) {
        if (columns != null) {
            this.header.clear();
            this.header.addAll(Arrays.asList(columns));
        }
        return this;
    }

    /**
     * Adiciona uma linha de dados ao arquivo CSV.
     *
     * @param values Valores das células da linha
     * @return A própria instância para encadeamento fluente
     */
    public CsvExportUtil addRow(Object... values) {
        if (values != null) {
            List<String> row = new ArrayList<>(values.length);
            for (Object val : values) {
                row.add(val != null ? String.valueOf(val) : "");
            }
            this.rows.add(row);
        }
        return this;
    }

    /**
     * Adiciona múltiplas linhas de dados ao arquivo CSV.
     *
     * @param allRows Lista de linhas de valores
     * @return A própria instância para encadeamento fluente
     */
    public CsvExportUtil addRows(List<List<String>> allRows) {
        if (allRows != null) {
            this.rows.addAll(allRows);
        }
        return this;
    }

    /**
     * Serializa o conteúdo formatado em um array de bytes com codificação UTF-8 e BOM.
     *
     * @return Array de bytes do arquivo CSV
     */
    public byte[] toByteArray() {
        StringBuilder sb = new StringBuilder();

        // Injeta o Byte Order Mark (BOM) UTF-8
        sb.append(BOM);

        // Escreve cabeçalho se presente
        if (!header.isEmpty()) {
            writeRow(sb, header);
        }

        // Escreve linhas de dados
        for (List<String> row : rows) {
            writeRow(sb, row);
        }

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Escreve uma linha formatada no buffer, aplicando o devido escape RFC 4180.
     */
    private void writeRow(StringBuilder sb, List<String> cells) {
        for (int i = 0; i < cells.size(); i++) {
            if (i > 0) {
                sb.append(COMMA);
            }
            sb.append(escapeRfc4180(cells.get(i)));
        }
        sb.append(LINE_SEPARATOR);
    }

    /**
     * Aplica escape segundo a norma RFC 4180:
     * - Se o campo contiver vírgula, aspas duplas ou quebras de linha (\r ou \n), é encapsulado por aspas.
     * - Qualquer aspa dupla interna é duplicada ("" -> ").
     *
     * @param cell Valor original do campo
     * @return Valor devidamente sanitizado e escapado
     */
    public static String escapeRfc4180(String cell) {
        if (cell == null) {
            return "";
        }
        if (cell.contains("\"") || cell.contains(",") || cell.contains("\n") || cell.contains("\r")) {
            return "\"" + cell.replace("\"", "\"\"") + "\"";
        }
        return cell;
    }
}
