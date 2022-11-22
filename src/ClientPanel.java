import javax.swing.*;
import java.sql.*;

public class ClientPanel extends JFrame{
    private JPanel clientPanel;
    private JButton backButton;

    public static void main(String[] args) {
        ClientPanel client = new ClientPanel();
    }

    public ClientPanel() {
        setContentPane(clientPanel);
        setTitle("Tikuzo");
        setSize(450, 300);
        setVisible(true);
    }
}
