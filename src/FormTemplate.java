import javax.swing.*;

public class FormTemplate extends JFrame {
    private JTextField textField1;
    private JTextField textField2;
    private JTextField textField3;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JPanel mainPanel;

    public static void main(String[] args) {
        FormTemplate f = new FormTemplate();
    }

    public FormTemplate() {
        setContentPane(mainPanel);

        setTitle("Tikuzo");
        setSize(450, 300);
        setVisible(true);
    }
}
