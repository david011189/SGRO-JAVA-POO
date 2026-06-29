package gui;

import estructura.ConexionBD;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.sql.*;

/**
 * Gestión de usuarios del sistema: recepcionistas y administradores.
 * Solo accesible para el administrador.
 */
public class FrmMantenimientoUsuarios extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextField      txtDni      = crearCampoDni();
    private JTextField      txtNombre   = new JTextField();
    private JTextField      txtCorreo   = new JTextField();
    private JPasswordField  txtPassword = new JPasswordField();
    private JComboBox<String> cmbRol    = new JComboBox<>(new String[]{"recepcionista", "admin"});

    private JTable            tabla;
    private DefaultTableModel modelo;

    private JButton btnNuevo     = new JButton("Nuevo");
    private JButton btnGuardar   = new JButton("Guardar");
    private JButton btnModificar = new JButton("Modificar");
    private JButton btnEliminar  = new JButton("Eliminar");
    private JButton btnLimpiar   = new JButton("Limpiar");

    private String dniSeleccionado = null;

    private static JTextField crearCampoDni() {
        JTextField campo = new JTextField();
        campo.setDocument(new PlainDocument() {
            @Override
            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                if (str == null) return;
                String soloNumeros = str.replaceAll("[^0-9]", "");
                if (getLength() + soloNumeros.length() <= 8)
                    super.insertString(offs, soloNumeros, a);
            }
        });
        return campo;
    }

    // ---------------------------------------------------------------
    public FrmMantenimientoUsuarios() {
        setTitle("Mantenimiento de Usuarios del Sistema");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 530);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(root);

        root.add(panelFormulario(), BorderLayout.NORTH);
        root.add(panelTabla(),      BorderLayout.CENTER);
        root.add(panelBotones(),    BorderLayout.SOUTH);

        configurarEventos();
        habilitarCampos(false);
        actualizarBotones(false);
        refrescarTabla();
    }

    // ---------------------------------------------------------------
    private JPanel panelFormulario() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Datos del Usuario",
                TitledBorder.LEFT, TitledBorder.TOP));

        GridBagConstraints lc = new GridBagConstraints();
        lc.insets  = new Insets(8, 10, 8, 4);
        lc.anchor  = GridBagConstraints.EAST;

        GridBagConstraints fc = new GridBagConstraints();
        fc.insets  = new Insets(8, 0, 8, 10);
        fc.fill    = GridBagConstraints.HORIZONTAL;
        fc.weightx = 1.0;

        // Fila 0
        lc.gridx = 0; lc.gridy = 0; p.add(new JLabel("DNI:"), lc);
        fc.gridx = 1; fc.gridy = 0; p.add(txtDni, fc);

        lc.gridx = 2; lc.gridy = 0; p.add(new JLabel("Nombre:"), lc);
        fc.gridx = 3; fc.gridy = 0; p.add(txtNombre, fc);

        // Fila 1
        lc.gridx = 0; lc.gridy = 1; p.add(new JLabel("Correo:"), lc);
        fc.gridx = 1; fc.gridy = 1; p.add(txtCorreo, fc);

        lc.gridx = 2; lc.gridy = 1; p.add(new JLabel("Password:"), lc);
        fc.gridx = 3; fc.gridy = 1; p.add(txtPassword, fc);

        // Fila 2 — Rol con badge visual
        lc.gridx = 0; lc.gridy = 2; p.add(new JLabel("Rol:"), lc);

        JPanel rolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        rolPanel.setOpaque(false);
        cmbRol.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rolPanel.add(cmbRol);

        JLabel lblInfo = new JLabel("  ← Define los permisos del usuario en el sistema");
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblInfo.setForeground(new Color(100, 100, 100));
        rolPanel.add(lblInfo);

        fc.gridx = 1; fc.gridy = 2; fc.gridwidth = 3;
        p.add(rolPanel, fc);
        fc.gridwidth = 1;

        return p;
    }

    // ---------------------------------------------------------------
    private JScrollPane panelTabla() {
        modelo = new DefaultTableModel(
                new String[]{"DNI", "Nombre", "Correo", "Rol"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabla = new JTable(modelo);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setRowHeight(26);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabla.getTableHeader().setReorderingAllowed(false);

        // Ancho de columnas
        int[] anchos = {90, 180, 220, 100};
        for (int i = 0; i < anchos.length; i++)
            tabla.getColumnModel().getColumn(i).setPreferredWidth(anchos[i]);

        // Colorear filas según rol
        tabla.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (sel) {
                    setBackground(new Color(0, 123, 255));
                    setForeground(Color.WHITE);
                } else {
                    String rol = modelo.getValueAt(row, 3) != null
                            ? modelo.getValueAt(row, 3).toString() : "";
                    if (rol.equals("admin")) {
                        setBackground(new Color(237, 231, 246));
                        setForeground(new Color(70, 0, 130));
                    } else {
                        setBackground(row % 2 == 0 ? Color.WHITE : new Color(232, 244, 255));
                        setForeground(Color.DARK_GRAY);
                    }
                }
                return this;
            }
        });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarFila();
        });

        JScrollPane sc = new JScrollPane(tabla);
        sc.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Usuarios del Sistema (recepcionistas y administradores)",
                TitledBorder.LEFT, TitledBorder.TOP));
        return sc;
    }

    // ---------------------------------------------------------------
    private JPanel panelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        Dimension tam = new Dimension(120, 32);
        for (JButton b : new JButton[]{btnNuevo, btnGuardar, btnModificar, btnEliminar, btnLimpiar}) {
            b.setPreferredSize(tam);
            p.add(b);
        }
        return p;
    }

    // ---------------------------------------------------------------
    private void configurarEventos() {

        btnNuevo.addActionListener(e -> {
            limpiar();
            habilitarCampos(true);
            actualizarBotones(true);
            dniSeleccionado = null;
            tabla.clearSelection();
            txtDni.requestFocusInWindow();
        });

        btnGuardar.addActionListener(e -> {
            if (!validar()) return;
            String err = registrarUsuario();
            if (err != null) { error(err); return; }
            info("Usuario registrado correctamente.");
            refrescarTabla();
            limpiar();
            habilitarCampos(false);
            actualizarBotones(false);
        });

        btnModificar.addActionListener(e -> {
            if (dniSeleccionado == null) { error("Seleccione un usuario."); return; }
            if (!validar()) return;
            String err = modificarUsuario();
            if (err != null) { error(err); return; }
            info("Usuario modificado correctamente.");
            refrescarTabla();
            limpiar();
            habilitarCampos(false);
            actualizarBotones(false);
            dniSeleccionado = null;
        });

        btnEliminar.addActionListener(e -> {
            if (dniSeleccionado == null) { error("Seleccione un usuario."); return; }
            int ok = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar el usuario con DNI " + dniSeleccionado + "?\n" +
                    "Esta acción no se puede deshacer.",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (ok != JOptionPane.YES_OPTION) return;
            eliminarUsuario();
            refrescarTabla();
            limpiar();
            habilitarCampos(false);
            actualizarBotones(false);
            dniSeleccionado = null;
        });

        btnLimpiar.addActionListener(e -> {
            limpiar();
            habilitarCampos(false);
            actualizarBotones(false);
            tabla.clearSelection();
            dniSeleccionado = null;
        });
    }

    // ---------------------------------------------------------------
    private String registrarUsuario() {
        String sql = "INSERT INTO clientes (dni, nombre, correo, password, rol) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, txtDni.getText().trim());
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtCorreo.getText().trim());
            ps.setString(4, new String(txtPassword.getPassword()).trim());
            ps.setString(5, cmbRol.getSelectedItem().toString());
            ps.executeUpdate();
            return null;
        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("dni"))    return "El DNI ya está registrado.";
            if (e.getMessage().contains("correo")) return "El correo ya está registrado.";
            return "Dato duplicado: " + e.getMessage();
        } catch (Exception e) {
            return "Error al registrar: " + e.getMessage();
        }
    }

    private String modificarUsuario() {
        String sql = "UPDATE clientes SET dni=?, nombre=?, correo=?, password=?, rol=? WHERE dni=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, txtDni.getText().trim());
            ps.setString(2, txtNombre.getText().trim());
            ps.setString(3, txtCorreo.getText().trim());
            ps.setString(4, new String(txtPassword.getPassword()).trim());
            ps.setString(5, cmbRol.getSelectedItem().toString());
            ps.setString(6, dniSeleccionado);
            int filas = ps.executeUpdate();
            return filas == 0 ? "Usuario no encontrado." : null;
        } catch (SQLIntegrityConstraintViolationException e) {
            if (e.getMessage().contains("dni"))    return "El DNI ya está en uso.";
            if (e.getMessage().contains("correo")) return "El correo ya está en uso.";
            return "Dato duplicado: " + e.getMessage();
        } catch (Exception e) {
            return "Error al modificar: " + e.getMessage();
        }
    }

    private void eliminarUsuario() {
        String sql = "DELETE FROM clientes WHERE dni=?";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, dniSeleccionado);
            ps.executeUpdate();
        } catch (Exception e) {
            error("Error al eliminar: " + e.getMessage());
        }
    }

    private void cargarFila() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) return;
        dniSeleccionado = modelo.getValueAt(fila, 0).toString();
        txtDni.setText(dniSeleccionado);
        txtNombre.setText(modelo.getValueAt(fila, 1).toString());
        txtCorreo.setText(modelo.getValueAt(fila, 2).toString());
        txtPassword.setText("");
        cmbRol.setSelectedItem(modelo.getValueAt(fila, 3).toString());
        habilitarCampos(true);
        btnGuardar.setEnabled(false);
        btnModificar.setEnabled(true);
        btnEliminar.setEnabled(true);
    }

    private void refrescarTabla() {
        modelo.setRowCount(0);
        String sql = "SELECT dni, nombre, correo, rol FROM clientes " +
                     "WHERE rol IN ('recepcionista','admin') ORDER BY rol, nombre";
        try (Connection con = ConexionBD.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                modelo.addRow(new Object[]{
                    rs.getString("dni"),
                    rs.getString("nombre"),
                    rs.getString("correo"),
                    rs.getString("rol")
                });
            }
        } catch (Exception e) {
            error("Error al cargar usuarios: " + e.getMessage());
        }
    }

    private boolean validar() {
        String dni = txtDni.getText().trim();
        if (dni.isEmpty() || !dni.matches("\\d{8}"))
            { error("El DNI debe tener exactamente 8 dígitos."); txtDni.requestFocusInWindow(); return false; }
        if (txtNombre.getText().trim().isEmpty())
            { error("El nombre es obligatorio."); txtNombre.requestFocusInWindow(); return false; }
        if (txtCorreo.getText().trim().isEmpty() || !txtCorreo.getText().contains("@"))
            { error("Ingrese un correo válido."); txtCorreo.requestFocusInWindow(); return false; }
        // Password requerido solo al crear; al modificar puede dejarse en blanco para no cambiarlo
        if (dniSeleccionado == null && new String(txtPassword.getPassword()).trim().isEmpty())
            { error("La contraseña es obligatoria."); txtPassword.requestFocusInWindow(); return false; }
        return true;
    }

    private void limpiar() {
        txtDni.setText(""); txtNombre.setText("");
        txtCorreo.setText(""); txtPassword.setText("");
        cmbRol.setSelectedIndex(0);
    }

    private void habilitarCampos(boolean on) {
        Color bg = on ? Color.WHITE : UIManager.getColor("Panel.background");
        for (JComponent f : new JComponent[]{txtDni, txtNombre, txtCorreo, txtPassword}) {
            if (f instanceof JTextField) { ((JTextField)f).setEditable(on); f.setBackground(bg); }
        }
        cmbRol.setEnabled(on);
    }

    private void actualizarBotones(boolean modoNuevo) {
        btnGuardar.setEnabled(modoNuevo);
        btnModificar.setEnabled(!modoNuevo && dniSeleccionado != null);
        btnEliminar.setEnabled(!modoNuevo && dniSeleccionado != null);
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
