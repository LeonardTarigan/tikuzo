import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Login extends JFrame {
    private JTextField emailInput;
    private JPasswordField passwordInput;
    private JButton loginButton;
    private JPanel loginPanel;
    private JButton registerButton;
    private JLabel emailLabel;

    public static void main(String[] args) {
        Login login = new Login();
    }

    Connection con;

    public Login() {
        //get db connection
        DBConnection dbCon = new DBConnection();
        con = dbCon.getCon();

        //initial ui
        setContentPane(loginPanel);
        setTitle("Tikuzo");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        //submit login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailInput.getText();
                String password = new String(passwordInput.getPassword());

                try {
                    PreparedStatement ps = con.prepareStatement("SELECT * FROM customer WHERE email=? AND password=?");

                    ps.setString(1, email);
                    ps.setString(2, password);

                    ResultSet res = ps.executeQuery();

                    if (password.equals("admin") && email.equals("admin@tkz.com")) {
                        setVisible(false);
                        new AdminPanel().setVisible(true);
                    } else {
                        boolean isFound = false;
                        while (res.next()) {
                            if (email.equals(res.getString("email")) && password.equals(res.getString("password"))) {
                                isFound = true;
                                setVisible(false);
                                new ClientPanel(res.getString("customer_ID"), res.getString("nama")).setVisible(true);
                            }
                        }

                        if (!isFound) {
                            UIManager.put("OptionPane.okButtonText", "Retry");
                            JOptionPane.showMessageDialog(null, "Wrong email or password");
                        }
                    }
                } catch (SQLException err) {
                    err.printStackTrace();
                }
            }
        });

        //go to register
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Register().setVisible(true);
                setVisible(false);
            }
        });
    }
}
