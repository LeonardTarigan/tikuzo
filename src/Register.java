import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Register extends JFrame {
    private JPanel registerPanel;
    private JTextField usernameInput;
    private JButton registerButton;
    private JPasswordField passwordInput;
    private JPasswordField confirmPassInput;
    private JButton loginButton;
    private JTextField emailInput;
    private JTextField phoneInput;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel phoneLabel;
    private JLabel emailLabel;
    private JLabel confirmLabel;

    public static void main(String[] args) {
        Register register = new Register();
    }

    public Register() {
        //get db connection
        DBConnection dbCon = new DBConnection();
        Connection con = dbCon.getCon();

        //initial ui
        setContentPane(registerPanel);
        setTitle("Tikuzo");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);


        //back to log in
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login().setVisible(true);
                setVisible(false);
            }
        });

        //register
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameInput.getText();
                String email = emailInput.getText();
                String phone = phoneInput.getText();
                String password = new String(passwordInput.getPassword());
                String confirmPass = new String(confirmPassInput.getPassword());

                try {
                    if (username.equals("") || email.equals("") || phone.equals("") || password.equals("") || confirmPass.equals("")) {
                        JOptionPane.showMessageDialog(null, "Please fill all fields");
                    } else {
                        if (password.equals(confirmPass)) {
                            PreparedStatement ps = con.prepareStatement("INSERT INTO customer (customer_ID, nama, email, no_hp, password) VALUES (?, ?, ?, ?, ?)");

                            PreparedStatement idCheck = con.prepareStatement("SELECT customer_ID FROM customer WHERE customer_ID=?");
                            idCheck.setString(1, password);
                            ResultSet idRes = idCheck.executeQuery();

                            String randId = new Utils().generateRandom();

                            while(idRes.next()) {
                                if (idRes.getString("password").equals(password)) {
                                    randId = new Utils().generateRandom();
                                }
                            }

                            ps.setString(1, randId);
                            ps.setString(2, username);
                            ps.setString(3, email);
                            ps.setString(4, phone);
                            ps.setString(5, password);

                            ps.executeUpdate();

                            UIManager.put("OptionPane.okButtonText", "Start");
                            JOptionPane.showMessageDialog(null, "Registered");
                            setVisible(false);
                            new ClientPanel(randId, username).setVisible(true);
                        } else {
                            UIManager.put("OptionPane.okButtonText", "Retry");
                            JOptionPane.showMessageDialog(null, "Password doesn't match");
                        }
                    }
                } catch (SQLException err) {
                    UIManager.put("OptionPane.okButtonText", "Retry");
                    JOptionPane.showMessageDialog(null, "Email has already been used! Try to log in instead.");
                }
            }
        });
    }

}
