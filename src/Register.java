import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        setContentPane(registerPanel);
        setTitle("Tikuzo");
        setSize(450, 300);
        setVisible(true);

        //back to login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login().setVisible(true);
                setVisible(false);
            }
        });
    }

}
