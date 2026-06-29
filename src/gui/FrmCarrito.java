package gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Date;

/**
 * Paso 2 — Carrito: el cliente revisa los detalles antes de pagar.
 */
public class FrmCarrito extends JFrame {

    private static final long serialVersionUID = 1L;

    private final String clienteLogueado;
    private final int    numHab;
    private final String tipo;
    private final String descripcion;
    private final double tarifa;
    private final Date   fechaIngreso;
    private final Date   fechaSalida;
    private final int    noches;
    private final double total;

    // ---------------------------------------------------------------
    public FrmCarrito(String clienteLogueado, int numHab, String tipo, String descripcion,
                      double tarifa, Date fechaIngreso, Date fechaSalida, int noches, double total) {
        this.clienteLogueado = clienteLogueado;
        this.numHab          = numHab;
        this.tipo            = tipo;
        this.descripcion     = descripcion;
        this.tarifa          = tarifa;
        this.fechaIngreso    = fechaIngreso;
        this.fechaSalida     = fechaSalida;
        this.noches          = noches;
        this.total           = total;

        setTitle("SGRO — Carrito de Reserva (Paso 2 de 4)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(750, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        root.setBackground(new Color(240, 248, 255));
        setContentPane(root);

        root.add(panelTitulo(),  BorderLayout.NORTH);
        root.add(panelCentro(),  BorderLayout.CENTER);
        root.add(panelBotones(), BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------
    private JPanel panelTitulo() {
        JPanel p = new JPanel(new BorderLayout(0, 2));
        p.setOpaque(false);

        JLabel lbl = new JLabel("Paso 2: Carrito de Reserva", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbl.setForeground(new Color(0, 51, 102));

        JLabel pasos = new JLabel("1. Cotización  →  [ 2. Carrito ]  →  3. Pago  →  4. Confirmación", SwingConstants.CENTER);
        pasos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pasos.setForeground(new Color(100, 100, 100));

        p.add(lbl,   BorderLayout.CENTER);
        p.add(pasos, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelCentro() {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setOpaque(false);
        p.add(panelDetalle(), BorderLayout.CENTER);
        p.add(panelTotal(),   BorderLayout.SOUTH);
        return p;
    }

    // ---------------------------------------------------------------
    private JPanel panelDetalle() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");

        // Layout de 4 columnas: label | valor | label | valor
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), "Resumen de su reserva",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 12)));

        GridBagConstraints gl = new GridBagConstraints(); // label
        gl.anchor = GridBagConstraints.NORTHWEST;
        gl.insets = new Insets(7, 14, 7, 6);

        GridBagConstraints gv = new GridBagConstraints(); // valor
        gv.anchor  = GridBagConstraints.NORTHWEST;
        gv.insets  = new Insets(7, 4, 7, 16);
        gv.fill    = GridBagConstraints.HORIZONTAL;
        gv.weightx = 1.0;

        // Columna izquierda
        Object[][] col1 = {
            {"Cliente:",       clienteLogueado},
            {"Habitación N°:", String.valueOf(numHab)},
            {"Tipo:",          tipo},
            {"Descripción:",   descripcion},
        };

        // Columna derecha
        Object[][] col2 = {
            {"Fecha ingreso:",  sdf.format(fechaIngreso)},
            {"Fecha salida:",   sdf.format(fechaSalida)},
            {"Noches:",         noches + " noche(s)"},
            {"Tarifa/noche:",   "S/ " + String.format("%.2f", tarifa)},
        };

        for (int i = 0; i < col1.length; i++) {
            // Etiqueta izquierda
            gl.gridx = 0; gl.gridy = i;
            JLabel lbl1 = bold(col1[i][0].toString());
            p.add(lbl1, gl);

            // Valor izquierdo
            gv.gridx = 1; gv.gridy = i;
            p.add(normal(col1[i][1].toString()), gv);

            // Etiqueta derecha
            gl.gridx = 2;
            JLabel lbl2 = bold(col2[i][0].toString());
            p.add(lbl2, gl);

            // Valor derecho
            gv.gridx = 3;
            p.add(normal(col2[i][1].toString()), gv);
        }

        return p;
    }

    private JPanel panelTotal() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(0, 100, 0));
        p.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel lbl = new JLabel("TOTAL A PAGAR");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);

        JLabel val = new JLabel("S/ " + String.format("%.2f", total), SwingConstants.RIGHT);
        val.setFont(new Font("Segoe UI", Font.BOLD, 22));
        val.setForeground(Color.WHITE);

        JLabel det = new JLabel(noches + " noche(s)  ×  S/ " + String.format("%.2f", tarifa) + "/noche", SwingConstants.RIGHT);
        det.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        det.setForeground(new Color(180, 230, 180));

        JPanel derecha = new JPanel(new BorderLayout(0, 2));
        derecha.setOpaque(false);
        derecha.add(val, BorderLayout.CENTER);
        derecha.add(det, BorderLayout.SOUTH);

        p.add(lbl,    BorderLayout.WEST);
        p.add(derecha, BorderLayout.EAST);
        return p;
    }

    // ---------------------------------------------------------------
    private JPanel panelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        p.setOpaque(false);

        JButton btnVolver = new JButton("← Volver");
        JButton btnPagar  = new JButton("Proceder al pago →");

        estilo(btnVolver, new Color(108, 117, 125));
        estilo(btnPagar,  new Color(0, 123, 255));
        btnPagar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnPagar.setPreferredSize(new Dimension(180, 36));

        btnVolver.addActionListener(e -> {
            dispose();
            new FrmCotizacion(clienteLogueado).setVisible(true);
        });

        btnPagar.addActionListener(e -> {
            dispose();
            new FrmPago(clienteLogueado, numHab, tipo, tarifa,
                        fechaIngreso, fechaSalida, noches, total).setVisible(true);
        });

        p.add(btnVolver);
        p.add(btnPagar);
        return p;
    }

    // ---------------------------------------------------------------
    private JLabel bold(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return l;
    }

    private JLabel normal(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private void estilo(JButton btn, Color color) {
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 36));
    }
}
