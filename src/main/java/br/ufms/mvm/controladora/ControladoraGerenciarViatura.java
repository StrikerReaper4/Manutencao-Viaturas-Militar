package br.ufms.mvm.controladora;

import br.ufms.mvm.modelo.Modelo;
import br.ufms.mvm.modelo.TipoViatura;
import br.ufms.mvm.modelo.Viatura;
import br.ufms.mvm.persistencia.ModeloDAO;
import br.ufms.mvm.persistencia.TipoViaturaDAO;
import br.ufms.mvm.persistencia.ViaturaDAO;

import java.sql.SQLException;
import java.util.List;

// Caso de uso Gerenciar viatura (RF01), privativo do Operador
public class ControladoraGerenciarViatura {

    private static final int KM_PADRAO = 5000;

    private final ViaturaDAO viaturaDAO = new ViaturaDAO();
    private final ModeloDAO modeloDAO = new ModeloDAO();
    private final TipoViaturaDAO tipoDAO = new TipoViaturaDAO();

    public ControladoraGerenciarViatura() {
        if (!Sessao.isOperador()) {
            throw new IllegalStateException(MensagensCadastro.SEM_PERMISSAO);
        }
    }

    public List<Viatura> listarViaturas() throws SQLException {
        return viaturaDAO.listarTodas();
    }

    public List<Modelo> listarModelos() throws SQLException {
        return modeloDAO.listar();
    }

    public List<TipoViatura> listarTipos() throws SQLException {
        return tipoDAO.listar();
    }

    // novo = true inclui, senao altera a viatura de mesmo EB
    public void gravarViatura(boolean novo, String eb, String placa, Modelo modelo, TipoViatura tipo,
                              String kmPreventiva, String odometro, String situacao) throws SQLException {
        eb = eb == null ? "" : eb.trim().toUpperCase();
        if (eb.isEmpty()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("EB"));
        }
        if (modelo == null) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Modelo"));
        }
        if (tipo == null) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Tipo de viatura"));
        }
        if (situacao == null) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Situação da viatura"));
        }
        // RF11: se nao informar, adota 5.000 km
        int km = kmPreventiva == null || kmPreventiva.isBlank() ? KM_PADRAO : numero(kmPreventiva, "Km para manutenção preventiva");
        if (km == 0) {
            throw new IllegalArgumentException(MensagensCadastro.numeroInvalido("Km para manutenção preventiva"));
        }
        if (odometro == null || odometro.isBlank()) {
            throw new IllegalArgumentException(MensagensCadastro.obrigatorio("Odômetro atual"));
        }
        int odo = numero(odometro, "Odômetro atual");
        String placaLimpa = placa == null || placa.isBlank() ? null : placa.trim().toUpperCase();

        Viatura existente = viaturaDAO.buscarPorEB(eb);
        if (novo) {
            if (existente != null) {
                throw new IllegalArgumentException(MensagensCadastro.EB_REPETIDO);
            }
            viaturaDAO.inserir(new Viatura(eb, placaLimpa, km, odo, situacao, true, modelo, tipo));
        } else {
            if (existente == null) {
                throw new IllegalArgumentException(MensagensCadastro.SELECIONE);
            }
            // o odometro nao volta (mesma ideia da RNF11)
            if (odo < existente.getOdometro()) {
                throw new IllegalArgumentException(MensagensCadastro.odometroMenor(odo, existente.getOdometro()));
            }
            viaturaDAO.atualizar(new Viatura(eb, placaLimpa, km, odo, situacao, existente.isAtivo(), modelo, tipo));
        }
    }

    // RNF12 e RNF13: so remove se nao tiver vinculo
    public void removerViatura(String eb) throws SQLException {
        if (viaturaDAO.possuiVinculo(eb)) {
            throw new IllegalArgumentException(MensagensCadastro.VIATURA_COM_VINCULO);
        }
        viaturaDAO.remover(eb);
    }

    static int numero(String texto, String campo) {
        try {
            int n = Integer.parseInt(texto.trim());
            if (n < 0) {
                throw new NumberFormatException();
            }
            return n;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(MensagensCadastro.numeroInvalido(campo));
        }
    }
}
