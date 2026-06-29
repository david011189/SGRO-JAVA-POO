package gui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.print.*;
import java.util.Date;

/**
 * Paso 4 — Confirmación: comprobante de reserva pagada.
 */
public class FrmConfirmacion extends JFrame {

    private static final long serialVersionUID = 1L;

    private final String clienteLogueado;
    private final int    idReserva;
    private final int    numHab;
    private final String tipo;
    private final Date   fechaIngreso;
    private final Date   fechaSalida;
    private final int    noches;
    private final double total;
    private final String metodo;

    // ---------------------------------------------------------------
    public FrmConfirmacion(String clienteLogueado, int idReserva, int numHab, String tipo,
                           Date fechaIngreso, Date fechaSalida, int noches,
                           double total, String metodo) {
        this.clienteLogueado = clienteLogueado;
        this.idReserva       = idReserva;
        this.numHab          = numHab;
        this.tipo            = tipo;
        this.fechaIngreso    = fechaIngreso;
        this.fechaSalida     = fechaSalida;
        this.noches          = noches;
        this.total           = total;
        this.metodo          = metodo;

        setTitle("SGRO — Confirmación de Reserva (Paso 4 de 4)");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(560, 620);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(8, 8));
        root.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        root.setBackground(new Color(240, 248, 255));
        setContentPane(root);

        root.add(panelTitulo(),      BorderLayout.NORTH);
        root.add(panelComprobante(), BorderLayout.CENTER);
        root.add(panelBotones(),     BorderLayout.SOUTH);
    }

    // ---------------------------------------------------------------
    private JPanel panelTitulo() {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);

        JLabel lbl = new JLabel("¡Reserva Confirmada!", SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lbl.setForeground(new Color(0, 128, 0));

        JLabel pasos = new JLabel("1. Cotización  →  2. Carrito  →  3. Pago  →  [ 4. Confirmación ]", SwingConstants.CENTER);
        pasos.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        pasos.setForeground(new Color(100, 100, 100));

        JLabel icon = new JLabel("✔", SwingConstants.CENTER);
        icon.setFont(new Font("Segoe UI", Font.BOLD, 36));
        icon.setForeground(new Color(0, 128, 0));

        p.add(icon,  BorderLayout.NORTH);
        p.add(lbl,   BorderLayout.CENTER);
        p.add(pasos, BorderLayout.SOUTH);
        return p;
    }

    private JPanel panelComprobante() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
        java.text.SimpleDateFormat ahora = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 128, 0), 2),
                "Comprobante de Reserva — Hotel Barlen",
                TitledBorder.CENTER, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13), new Color(0, 80, 0)));

        GridBagConstraints lc = new GridBagConstraints();
        lc.anchor = GridBagConstraints.WEST;
        lc.insets = new Insets(5, 16, 5, 8);

        GridBagConstraints vc = new GridBagConstraints();
        vc.anchor  = GridBagConstraints.WEST;
        vc.insets  = new Insets(5, 0, 5, 16);
        vc.fill    = GridBagConstraints.HORIZONTAL;
        vc.weightx = 1.0;

        Object[][] filas = {
            {"N° Reserva:",    String.format("RES-%03d", idReserva)},
            {"Fecha de pago:", ahora.format(new Date())},
            {"Cliente:",       clienteLogueado},
            {"Habitación:",    "N° " + numHab + " — " + tipo},
            {"Ingreso:",       sdf.format(fechaIngreso)},
            {"Salida:",        sdf.format(fechaSalida)},
            {"Noches:",        noches + " noche(s)"},
            {"Método de pago:", metodo.toUpperCase()},
            {"Estado:",        "PAGADO ✔"},
            {"TOTAL PAGADO:",  "S/ " + String.format("%.2f", total)},
        };

        for (int i = 0; i < filas.length; i++) {
            lc.gridx = 0; lc.gridy = i;
            JLabel lbl = new JLabel(filas[i][0].toString());
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            p.add(lbl, lc);

            vc.gridx = 1; vc.gridy = i;
            JLabel val = new JLabel(filas[i][1].toString());
            val.setFont(new Font("Segoe UI", Font.PLAIN, 13));

            if (filas[i][0].toString().startsWith("TOTAL") || filas[i][0].toString().equals("Estado:")) {
                val.setFont(new Font("Segoe UI", Font.BOLD, 14));
                val.setForeground(new Color(0, 100, 0));
                lbl.setForeground(new Color(0, 100, 0));
            }
            if (filas[i][0].toString().startsWith("N° Reserva")) {
                val.setFont(new Font("Segoe UI", Font.BOLD, 14));
                val.setForeground(new Color(0, 51, 102));
            }
            p.add(val, vc);
        }
        return p;
    }

    private JPanel panelBotones() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
        p.setOpaque(false);

        JButton btnImprimir  = new JButton("Imprimir comprobante");
        JButton btnMisReservas = new JButton("Ver mis reservas");
        JButton btnMenu      = new JButton("Ir al menú principal");

        btnImprimir.setBackground(new Color(0, 123, 255));
        btnImprimir.setForeground(Color.WHITE);
        btnImprimir.setFocusPainted(false);

        btnMisReservas.setBackground(new Color(0, 150, 136));
        btnMisReservas.setForeground(Color.WHITE);
        btnMisReservas.setFocusPainted(false);

        btnMenu.setBackground(new Color(108, 117, 125));
        btnMenu.setForeground(Color.WHITE);
        btnMenu.setFocusPainted(false);

        btnImprimir.addActionListener(e -> imprimir());

        btnMisReservas.addActionListener(e -> {
            dispose();
            new FrmMisReservas(clienteLogueado).setVisible(true);
        });

        btnMenu.addActionListener(e -> dispose());

        p.add(btnImprimir);
        p.add(btnMisReservas);
        p.add(btnMenu);
        return p;
    }

    // ---------------------------------------------------------------
    private void imprimir() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            java.text.SimpleDateFormat sdf  = new java.text.SimpleDateFormat("dd/MM/yyyy");
            java.text.SimpleDateFormat ahora = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

            String[] lineas = {
                "================================================",
                "         HOTEL BARLEN — Anco Huallo, Apurímac",
                "================================================",
                "COMPROBANTE DE RESERVA",
                "",
                "N° Reserva:    RES-" + String.format("%03d", idReserva),
                "Fecha pago:    " + ahora.format(new Date()),
                "Cliente:       " + clienteLogueado,
                "Habitación:    N° " + numHab + " — " + tipo,
                "Ingreso:       " + sdf.format(fechaIngreso),
                "Salida:        " + sdf.format(fechaSalida),
                "Noches:        " + noches,
                "Método pago:   " + metodo.toUpperCase(),
                "Estado:        PAGADO",
                "------------------------------------------------",
                "TOTAL PAGADO:  S/ " + String.format("%.2f", total),
                "================================================",
                "   Gracias por elegir Hotel Barlen.",
                "================================================"
            };

            g2.setFont(new Font("Monospaced", Font.PLAIN, 12));
            int y = 40;
            for (String l : lineas) {
                g2.drawString(l, 30, y);
                y += 18;
            }
            return Printable.PAGE_EXISTS;
        });

        if (job.printDialog()) {
            try { job.print(); }
            catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Error al imprimir: " + ex.getMessage());
            }
        }
    }
}
