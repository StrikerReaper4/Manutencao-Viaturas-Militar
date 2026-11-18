package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.ItemUtilizado;
import br.ufms.mvm.persistencia.RelatorioDAO;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;

// Caso de uso Emitir relatorio de itens utilizados (RF13), privativo do Operador.
// Filtra por periodo e nome do item; sem periodo informado, usa o dia de hoje.
public class ControladoraEmitirRelatorioItens {

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final RelatorioDAO relatorioDAO = new RelatorioDAO();

    public ControladoraEmitirRelatorioItens() {
        if (!Sessao.isOperador()) {
            throw new IllegalStateException(MensagensCadastro.SEM_PERMISSAO);
        }
    }

    public List<ItemUtilizado> emitir(String dataInicial, String dataFinal, String nomeItem, LocalDate hoje)
            throws SQLException {
        // regra do RF13: campo de data em branco vira o dia atual
        LocalDate inicio = ler(dataInicial, "Data inicial", hoje);
        LocalDate fim = ler(dataFinal, "Data final", hoje);
        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException(MensagensRelatorio.PERIODO_INVERTIDO);
        }
        ZoneId zona = ZoneId.systemDefault();
        long de = inicio.atStartOfDay(zona).toInstant().toEpochMilli();
        long ate = fim.plusDays(1).atStartOfDay(zona).toInstant().toEpochMilli() - 1; // fim do dia
        return relatorioDAO.listarItensUtilizados(de, ate, nomeItem);
    }

    static LocalDate ler(String texto, String campo, LocalDate padrao) {
        if (texto == null || texto.isBlank()) {
            return padrao;
        }
        try {
            return LocalDate.parse(texto.trim(), FORMATO);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(MensagensRelatorio.dataInvalida(campo));
        }
    }
}
