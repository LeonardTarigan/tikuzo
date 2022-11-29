import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminPanel extends JFrame {
    private JPanel adminPanel;
    private JButton backButton;
    private JButton customerButton;
    private JButton movieButton;
    private JButton paymentButton;
    private JButton orderButton;
    private JButton provinceButton;
    private JButton cinemaButton;
    private JButton studioButton;
    private JButton seatButton;
    private JButton scheduleButton;
    private JButton studioScheduleButton;
    private JButton movieScheduleButton;
    private JButton ticketButton;

    public static void main(String[] args) {
        AdminPanel adm = new AdminPanel();
    }

    public AdminPanel() {
        setContentPane(adminPanel);
        setTitle("Tikuzo");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        //back to log in
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login();
                setVisible(false);
            }
        });


        customerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CustomerCRUD();
                setVisible(false);
            }
        });


        movieButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MovieCRUD();
                setVisible(false);
            }
        });

        paymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new PaymentCRUD();
                setVisible(false);
            }
        });

        orderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OrderCRUD();
                setVisible(false);
            }
        });

        provinceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProvinceCRUD();
                setVisible(false);
            }
        });

        cinemaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CinemaCRUD();
                setVisible(false);
            }
        });

        studioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StudioCRUD();
                setVisible(false);
            }
        });

        seatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SeatCRUD();
                setVisible(false);
            }
        });

        scheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ScheduleCRUD();
                setVisible(false);
            }
        });

        studioScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new StudioScheduleCRUD();
                setVisible(false);
            }
        });

        movieScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MovieScheduleCRUD();
                setVisible(false);
            }
        });

        ticketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TicketCRUD();
                setVisible(false);
            }
        });
    }
}
