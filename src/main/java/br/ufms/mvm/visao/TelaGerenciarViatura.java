package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraGerenciarViatura;
import br.ufms.mvm.controladora.MensagensCadastro;
import br.ufms.mvm.modelo.Modelo;
import br.ufms.mvm.modelo.TipoViatura;
import br.ufms.mvm.modelo.Viatura;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import java.sql.SQLException;
import java.util.List;

public class TelaGerenciarViatura extends TelaCadastro<Viatura> {

    private final ControladoraGerenciarViatura controladora = new ControladoraGerenciarViatura();

    private final JTextField txtEb = new JTextField();
    private final JTextField txtPlaca = new JTextField();
    private final JComboBox<Modelo> cbModelo = new JComboBox<>();
    private final JComboBox<TipoViatura> cbTipo = new JComboBox<>();
    private final JTextField txtKm = new JTextField();
    private final JTextField txtOdometro = new JTextField();
    private final JComboBox<String> cbSituacao = new JComboBox<>(
            new String[]{Viatura.DISPONIVEL, Viatura.INDISPONIVEL, Viatura.DISPONIVEL_RESTRICAO});

    public TelaGerenciarViatura() {
        super("Gerenciar viatura",
                "Inclusão, alteração e remoção de viaturas. Os campos assinalados com (*) são de preenchimento obrigatório.",
                "Dados da viatura",
                new String[]{"EB", "Placa", "Modelo", "Tipo", "Km preventiva", "Odômetro", "Situação", "Ativa"},
                true);
        try {
            controladora.listarModelos().forEach(cbModelo::addItem);
            controladora.listarTipos().forEach(cbTipo::addItem);
        } catch (SQLException e) {
            erro("Erro ao consultar o banco de dados: " + e.getMessage());
        }
        Estilo.empilhar(formulario, Estilo.linha(
                Estilo.campo("EB", true, txtEb),
                Estilo.campo("Placa", false, txtPlaca),
                Estilo.campo("Modelo", true, cbModelo),
                Estilo.campo("Tipo de viatura", true, cbTipo)));
        Estilo.empilhar(formulario, Estilo.linha(
                Estilo.campo("Km para manutenção preventiva (padrão 5000)", false, txtKm),
                Estilo.campo("Odômetro atual", true, txtOdometro),
                Estilo.campo("Situação da viatura", true, cbSituacao)));
        iniciar();
    }

    @Override
    protected List<Viatura> listar() throws SQLException {
        return controladora.listarViaturas();
    }

    @Override
    protected Object[] linha(Viatura v) {
        return new Object[]{v.getEb(), v.getPlaca() == null ? "" : v.getPlaca(), v.getModelo(), v.getTipoViatura(),
                v.getKmManutencaoPreventiva(), v.getOdometro(), v.getSituacao(), v.isAtivo() ? "Sim" : "Não"};
    }

    @Override
    protected void preencherFormulario(Viatura v) {
        txtEb.setText(v.getEb());
        txtEb.setEditable(false); // EB eh a chave, nao muda
        txtPlaca.setText(v.getPlaca() == null ? "" : v.getPlaca());
        selecionar(cbModelo, v.getModelo() == null ? -1 : v.getModelo().getIdModelo(), true);
        selecionar(cbTipo, v.getTipoViatura() == null ? -1 : v.getTipoViatura().getIdTipoViatura(), false);
        txtKm.setText(String.valueOf(v.getKmManutencaoPreventiva()));
        txtOdometro.setText(String.valueOf(v.getOdometro()));
        cbSituacao.setSelectedItem(v.getSituacao());
    }

    private void selecionar(JComboBox<?> combo, int id, boolean modelo) {
        combo.setSelectedIndex(-1);
        for (int i = 0; i < combo.getItemCount(); i++) {
            Object o = combo.getItemAt(i);
            int atual = modelo ? ((Modelo) o).getIdModelo() : ((TipoViatura) o).getIdTipoViatura();
            if (atual == id) {
                combo.setSelectedIndex(i);
            }
        }
    }

    @Override
    protected void limparFormulario() {
        txtEb.setText("");
        txtEb.setEditable(true);
        txtPlaca.setText("");
        cbModelo.setSelectedIndex(-1);
        cbTipo.setSelectedIndex(-1);
        txtKm.setText("");
        txtOdometro.setText("");
        cbSituacao.setSelectedItem(Viatura.DISPONIVEL);
    }

    @Override
    protected String gravar() throws SQLException {
        controladora.gravarViatura(selecionado == null, txtEb.getText(), txtPlaca.getText(),
                (Modelo) cbModelo.getSelectedItem(), (TipoViatura) cbTipo.getSelectedItem(),
                txtKm.getText(), txtOdometro.getText(), (String) cbSituacao.getSelectedItem());
        return MensagensCadastro.gravado("Viatura EB " + txtEb.getText().trim().toUpperCase());
    }

    @Override
    protected String excluir(Viatura v) throws SQLException {
        controladora.removerViatura(v.getEb());
        return MensagensCadastro.removido("Viatura EB " + v.getEb());
    }

    @Override
    protected String descrever(Viatura v) {
        return "a viatura EB " + v.getEb();
    }
}
