import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class TicketCRUD extends JFrame {
    private JPanel customerPanel;
    private JTable ticketTable;
    private JTextField ticketIdField;
    private JTextField priceField;
    private JTextField scheduleIdField;
    private JButton backButton;
    private JComboBox actionComboBox;
    private JButton createButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton resetButton;
    private JButton searchButton;
    private JTextField searchInput;
    private JTextField orderIdField;

    public static void main(String[] args) {

    }

    Connection con;

    void populateTable() {
        try {
            Statement st = con.createStatement();
            String query = "SELECT * FROM tiket";
            ResultSet res = st.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) ticketTable.getModel();
            String[] colNames = {"Ticket ID", "Price", "Schedule ID", "Order ID"};
            model.setColumnIdentifiers(colNames);

            while (res.next()) {
                String ticketId = res.getString("tiket_ID");
                String price = res.getString("harga");
                String scheduleId = res.getString("jadwal_ID");
                String orderId = res.getString("pesanan_ID") == null ? "NULL" : res.getString("pesanan_ID");
                String[] row = {ticketId, price, scheduleId, orderId};
                model.addRow(row);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public TicketCRUD() {
        //get db connection
        DBConnection dbCon = new DBConnection();
        con = dbCon.getCon();

        //initial ui
        setContentPane(customerPanel);
        setTitle("Tikuzo");
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        createButton.setEnabled(false);
        deleteButton.setEnabled(false);
        updateButton.setEnabled(false);

        //pupulate table
        populateTable();

        ticketTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int row = ticketTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) ticketTable.getModel();

                ticketIdField.setText(model.getValueAt(row, 0).toString());
                priceField.setText(model.getValueAt(row, 1).toString());
                scheduleIdField.setText(model.getValueAt(row, 2).toString());
                orderIdField.setText(model.getValueAt(row, 3).toString());
            }
        });


        actionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(actionComboBox.getSelectedItem().equals("Create")) {
                    priceField.setEditable(true);
                    scheduleIdField.setEditable(true);
                    orderIdField.setEditable(false);

                    createButton.setEnabled(true);
                    deleteButton.setEnabled(false);
                    updateButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Update")) {
                    priceField.setEditable(true);
                    scheduleIdField.setEditable(true);
                    orderIdField.setEditable(true);

                    updateButton.setEnabled(true);
                    createButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Delete")) {
                    priceField.setEditable(false);
                    scheduleIdField.setEditable(false);
                    orderIdField.setEditable(false);

                    deleteButton.setEnabled(true);
                    updateButton.setEnabled(false);
                    createButton.setEnabled(false);
                };
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement ps;

                    if (searchInput.getText().equals("")) {
                        ps = con.prepareStatement("SELECT * FROM tiket");
                    } else {
                        ps = con.prepareStatement("SELECT * FROM tiket WHERE harga LIKE CONCAT('%', ?, '%') OR jadwal_ID LIKE CONCAT('%', ?, '%')");
                        ps.setString(1, searchInput.getText());
                        ps.setString(2, searchInput.getText());
                    }

                    ResultSet res = ps.executeQuery();

                    DefaultTableModel model = (DefaultTableModel) ticketTable.getModel();

                    for (int i = model.getRowCount() - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }

                    while (res.next()) {
                        String ticketId = res.getString("tiket_ID");
                        String price = res.getString("harga");
                        String scheduleId = res.getString("jadwal_ID");
                        String orderId = res.getString("pesanan_ID") == null ? "NULL" : res.getString("pesanan_ID");
                        String[] row = {ticketId, price, scheduleId, orderId};
                    }

                } catch (SQLException err) {
                    err.printStackTrace();
                }
            }
        });


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AdminPanel();
                setVisible(false);
            }
        });


        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to delete " + ticketIdField.getText() + "?"), "Delete Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("DELETE FROM tiket WHERE tiket_ID = ?");
                        ps.setString(1, ticketIdField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Deleted!");

                        DefaultTableModel model = (DefaultTableModel) ticketTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, String.valueOf(err));
                    }
                }
            }
        });


        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to insert new ticket?"), "Create Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO tiket(tiket_ID, harga, jadwal_ID, pesanan_ID) VALUES (?, ?, ?, NULL)");

                        String randId = new Utils().generateRandom();

                        PreparedStatement idCheck = con.prepareStatement("SELECT tiket_ID FROM tiket WHERE tiket_ID=?");
                        idCheck.setString(1, randId);
                        ResultSet idRes = idCheck.executeQuery();

                        while (true) {
                            if (idRes == null) {
                                randId = new Utils().generateRandom();
                                idCheck.setString(1, randId);
                                idRes = idCheck.executeQuery();
                            } else {
                                break;
                            }
                        }

                        ps.setString(1, randId);
                        ps.setString(2, priceField.getText());
                        ps.setString(3, scheduleIdField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Inserted!");

                        DefaultTableModel model = (DefaultTableModel) ticketTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        ticketIdField.setText("");
                        priceField.setText("");
                        scheduleIdField.setText("");
                        orderIdField.setText("");
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, String.valueOf(err));
                    }
                }
            }
        });


        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to update " + ticketIdField.getText() + "?"), "Update Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("UPDATE tiket SET harga=?, jadwal_ID=?, pesanan_ID=? WHERE tiket_ID=?");

                        ps.setString(1, priceField.getText());
                        ps.setString(2, scheduleIdField.getText());
                        ps.setString(3, orderIdField.getText());
                        ps.setString(4, ticketIdField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Updated!");

                        DefaultTableModel model = (DefaultTableModel) ticketTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        ticketIdField.setText("");
                        priceField.setText("");
                        scheduleIdField.setText("");
                        orderIdField.setText("");
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, err);
                    }
                }
            }
        });


        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ticketIdField.setText("");
                priceField.setText("");
                scheduleIdField.setText("");
                orderIdField.setText("");
            }
        });
    }
}
