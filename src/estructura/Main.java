package estructura;

public class Main {

    public static void main(String[] args) {
        // Punto de entrada de la aplicación: lanzar el login
        javax.swing.SwingUtilities.invokeLater(() -> new gui.FrmLogin().setVisible(true));
    }
}
