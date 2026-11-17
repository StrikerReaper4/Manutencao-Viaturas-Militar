package br.ufms.mvm.visao;

import br.ufms.mvm.controladora.ControladoraGerenciarUsuario;
import br.ufms.mvm.controladora.MensagensCadastro;
import br.ufms.mvm.modelo.Usuario;

import javax.swing.JComboBox;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.sql.SQLException;
import java.util.List;

// RF02: so inclusao e alteracao, sem remocao
public class TelaGerenciarUsuario extends TelaCadastro<Usuario> {

    private final ControladoraGerenciarUsuario controladora = new ControladoraGerenciarUsuario();

    private final JTextField txtLogin = new JTextField();
    private final JPasswordField txtSenha = new JPasswordField();
    private final JComboBox<String> cbGraduacao = new JComboBox<>(new String[]{
            "Sd", "Cb", "3º Sgt", "2º Sgt", "1º Sgt", "ST", "Asp", "2º Ten", "1º Ten", "Cap", "Maj", "Ten Cel", "Cel"});
    private final JTextField txtNome = new JTextField();
    private final JComboBox<String> cbClasse = new JComboBox<>(
            new String[]{ControladoraGerenciarUsuario.MECANICO, ControladoraGerenciarUsuario.OPERADOR});

    public TelaGerenciarUsuario() {
        super("Gerenciar usuário",
                "Criação e alteração das contas de acesso. Na alteração, deixe a senha em branco para mantê-la.",
                "Dados do usuário",
                new String[]{"Login", "Graduação", "Nome do militar", "Classe", "Ativo"},
                false);
        cbGraduacao.setEditable(true); // aceita graduacao fora da lista
        Estilo.empilhar(formulario, Estilo.linha(
                Estilo.campo("Login", true, txtLogin),
                Estilo.campo("Senha (mínimo 6 caracteres)", true, txtSenha),
                Estilo.campo("Classe de usuário", true, cbClasse)));
        Estilo.empilhar(formulario, Estilo.linha(
                Estilo.campo("Graduação", true, cbGraduacao),
                Estilo.campo("Nome do militar", true, txtNome)));
        iniciar();
    }

    @Override
    protected List<Usuario> listar() throws SQLException {
        return controladora.listarUsuarios();
    }

    @Override
    protected Object[] linha(Usuario u) {
        return new Object[]{u.getLogin(), u.getGraduacao(), u.getNome(), ControladoraGerenciarUsuario.classe(u),
                u.isAtivo() ? "Sim" : "Não"};
    }

    @Override
    protected void preencherFormulario(Usuario u) {
        txtLogin.setText(u.getLogin());
        txtSenha.setText(""); // RNF06: a senha nunca volta pra tela
        cbGraduacao.setSelectedItem(u.getGraduacao());
        txtNome.setText(u.getNome());
        cbClasse.setSelectedItem(ControladoraGerenciarUsuario.classe(u));
    }

    @Override
    protected void limparFormulario() {
        txtLogin.setText("");
        txtSenha.setText("");
        cbGraduacao.setSelectedIndex(0);
        txtNome.setText("");
        cbClasse.setSelectedItem(ControladoraGerenciarUsuario.MECANICO);
    }

    @Override
    protected String gravar() throws SQLException {
        Object grad = cbGraduacao.getEditor().getItem();
        controladora.gravarUsuario(selecionado == null ? null : selecionado.getIdUsuario(),
                txtLogin.getText(), new String(txtSenha.getPassword()),
                grad == null ? null : grad.toString(), txtNome.getText(), (String) cbClasse.getSelectedItem());
        return MensagensCadastro.gravado("Usuário " + txtLogin.getText().trim().toLowerCase());
    }
}
