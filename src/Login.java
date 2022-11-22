import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private JTextField usernameInput;
    private JPasswordField passwordInput;
    private JButton loginButton;
    private JPanel loginPanel;
    private JButton registerButton;

    public static void main(String[] args) {
        Login login = new Login();
    }

    public Login() {
        setContentPane(loginPanel);
        setTitle("Tikuzo");
        setSize(450, 300);
        setVisible(true);

        //submit login
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameInput.getText();
                String password = passwordInput.getText();

                if (password.equals("admin") && username.equals("admin")) {
                    new AdminPanel().setVisible(true);
                } else {
                    new ClientPanel().setVisible(true);
                }
                    setVisible(false);
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
