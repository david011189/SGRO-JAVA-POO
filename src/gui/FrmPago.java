package gui;

import estructura.ConexionBD;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.*;
import java.util.Date;

/**
 * Paso 3 — Pago: el cliente elige el método y confirma la transacción.
 */
public class FrmPago extends JFrame {

    private static final long serialVersionUID = 1L;

    private final String clienteLogueado;
    private final int    numHab;
    private final String tipo;
    private final double tarifa;
    private final Date   fechaIngreso;
    private final Date   fechaSalida;
    private final int    noches;
    private final double total;

    private JRadioButton rbEfectivo     = new JRadioButton("Efectivo");
    private JRadioButton rbYape         = new JRadioButton("Yape");
    private JRadioButton rbTransferencia = new JRadioButton("Transferencia bancaria");
    private JLabel       lblInfo        = new JLabel(" ");

    // ---------------------------------------------------------------
    public FrmPago(String clienteLogueado, int numHab, String tipo, double tarifa,
                   Date fechaIngreso, Date fechaSalida, int noches, double total) {
        this.clienteLogueado = clienteLogueado;
        this.numHab          = numHab;
        this.tipo            = tipo;
        this.tarifa          = tarifa;
        this.fechaIngreso    = fechaIngreso;
        this.fechaSalida     = fechaSalida;
        this.noches          = noches;
        this.total           = total;

        setTitle("SGRO — Pago (Paso 3 de 4)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(480, 440);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        root.setBackground(new Color(240, 248, 255));
        setContentPane(root);

        root.add(panelTitulo(),  BorderLayout.NORTH);
        root.add(panelCentro(),  BorderLayout.CENTER);
        root.add(panelBotones(), BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------
    private JPanel panelTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JLabel lbl = new JLabel("Paso 3: Pago de Reserva", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setForeground(new Color(0, 51, 102));

        JLabel pasos = new JLabel("1. Cotización  →  2. Carrito  →  [ 3. Pago ]  →  4. Confirmación", SwingConstants.CENTER);
        pasos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pasos.setForeground(new Color(100, 100, 100));

        p.add(lbl,   BorderLayout.CENTER);
        p.add(pasos, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelCentro() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setOpaque(false);
        p.add(panelResumen(), BorderLayout.NORTH);
        p.add(panelMetodo(),  BorderLayout.CENTER);
        return p;
    }

    private JPanel panelResumen() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

        JPanel p = new JPanel(new GridLayout(4, 2, 6, 6));
        p.setBackground(new Color(230, 245, 255));
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Resumen",
                TitledBorder.LEFT, TitledBorder.TOP));

        fila(p, "Habitación:",   "N° " + numHab + " — " + tipo);
        fila(p, "Ingreso:",      sdf.format(fechaIngreso));
        fila(p, "Salida:",       sdf.format(fechaSalida) + "  (" + noches + " noche(s))");
        fila(p, "TOTAL A PAGAR:", "S/ " + String.format("%.2f", total));
        return p;
    }

    private void fila(JPanel p, String etiqueta, String valor) {
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        JLabel val = new JLabel(valor);
        val.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        if (etiqueta.startsWith("TOTAL")) {
            lbl.setForeground(new Color(0, 100, 0));
            val.setFont(new Font("Segoe UI", Font.BOLD, 14));
            val.setForeground(new Color(0, 100, 0));
        }
        p.add(lbl); p.add(val);
    }

    private JPanel panelMetodo() {
        JPanel p = new JPanel(new GridLayout(4, 1, 6, 6));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Seleccione método de pago",
                TitledBorder.LEFT, TitledBorder.TOP));

        ButtonGroup grupo = new ButtonGroup();
        rbEfectivo.setSelected(true);
        for (JRadioButton rb : new JRadioButton[]{rbEfectivo, rbYape, rbTransferencia}) {
            rb.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            rb.setBackground(Color.WHITE);
            grupo.add(rb);
            p.add(rb);
        }

        lblInfo.setForeground(new Color(180, 0, 0));
        lblInfo.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblInfo.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        p.add(lblInfo);
        return p;
    }

    private JPanel panelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 6));
        p.setOpaque(false);

        JButton btnVolver  = new JButton("← Volver");
        JButton btnConfirmar = new JButton("Confirmar pago →");

        btnVolver.setBackground(new Color(108, 117, 125));
        btnVolver.setForeground(Color.WHITE);
        btnVolver.setFocusPainted(false);

        btnConfirmar.setBackground(new Color(40, 167, 69));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 13));

        btnVolver.addActionListener(e -> {
            dispose();
            new FrmCarrito(clienteLogueado, numHab, tipo, "",
                           tarifa, fechaIngreso, fechaSalida, noches, total).setVisible(true);
        });

        btnConfirmar.addActionListener(e -> procesarPago());

        p.add(btnVolver);
        p.add(btnConfirmar);
        return p;
    }

    // ---------------------------------------------------------------
    private void procesarPago() {
        String metodo = rbEfectivo.isSelected()      ? "efectivo"      :
                        rbYape.isSelected()           ? "yape"          :
                                                        "transferencia";

        java.text.SimpleDateFormat sdfBD = new java.text.SimpleDateFormat("yyyy-MM-dd");
        String ingBD = sdfBD.format(fechaIngreso);
        String salBD = sdfBD.format(fechaSalida);

        try (Connection con = ConexionBD.conectar()) {
            // 1. Verificar disponibilidad una vez más (RF-32)
            String sqlCheck = "SELECT COUNT(*) FROM reserva WHERE habitacion=? " +
                              "AND NOT (fecha_salida <= ? OR fecha_ingreso >= ?)";
            try (PreparedStatement ps = con.prepareStatement(sqlCheck)) {
                ps.setInt(1, numHab);
                ps.setString(2, ingBD);
                ps.setString(3, salBD);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    lblInfo.setText("La habitación ya fue reservada en esas fechas. Vuelva a cotizar.");
                    return;
                }
            }

            // 2. Insertar reserva
            String sqlIns = "INSERT INTO reserva (cliente, fecha_ingreso, fecha_salida, habitacion, costo, dias, metodo_pago, estado_reserva) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, 'confirmada')";
            int idGenerado;
            try (PreparedStatement ps = con.prepareStatement(sqlIns, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, clienteLogueado);
                ps.setString(2, ingBD);
                ps.setString(3, salBD);
                ps.setInt(4, numHab);
                ps.setDouble(5, total);
                ps.setInt(6, noches);
                ps.setString(7, metodo);
                ps.executeUpdate();
                ResultSet k = ps.getGeneratedKeys();
                k.next();
                idGenerado = k.getInt(1);
            }

            // 3. Marcar habitación como ocupada
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE habitaciones SET estado='ocupada' WHERE numero=?")) {
                ps.setInt(1, numHab);
                ps.executeUpdate();
            }

            dispose();
            new FrmConfirmacion(clienteLogueado, idGenerado, numHab, tipo,
                                fechaIngreso, fechaSalida, noches, total, metodo).setVisible(true);

        } catch (Exception ex) {
            lblInfo.setText("Error al procesar pago: " + ex.getMessage());
        }
    }
}
