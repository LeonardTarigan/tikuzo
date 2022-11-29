import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class OrderCRUD extends JFrame {
    private JPanel customerPanel;
    private JTable orderTable;
    private JTextField orderIdField;
    private JTextField customerIdField;
    private JButton backButton;
    private JComboBox actionComboBox;
    private JButton createButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton resetButton;
    private JButton searchButton;
    private JTextField searchInput;

    public static void main(String[] args) {

    }

    Connection con;

    void populateTable() {
        try {
            Statement st = con.createStatement();
            String query = "SELECT * FROM pesanan";
            ResultSet res = st.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) orderTable.getModel();
            String[] colNames = {"Order ID", "Customer ID"};
            model.setColumnIdentifiers(colNames);

            while (res.next()) {
                String orderId = res.getString("pesanan_ID");
                String customerId = res.getString("customer_ID");
                String[] row = {orderId, customerId};
                model.addRow(row);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public OrderCRUD() {
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

        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int row = orderTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) orderTable.getModel();

                orderIdField.setText(model.getValueAt(row, 0).toString());
                customerIdField.setText(model.getValueAt(row, 1).toString());
            }
        });


        actionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(actionComboBox.getSelectedItem().equals("Create")) {
                    customerIdField.setEditable(true);

                    createButton.setEnabled(true);
                    deleteButton.setEnabled(false);
                    updateButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Update")) {
                    customerIdField.setEditable(true);

                    updateButton.setEnabled(true);
                    createButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Delete")) {
                    customerIdField.setEditable(false);

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
                        ps = con.prepareStatement("SELECT * FROM pesanan");
                    } else {
                        ps = con.prepareStatement("SELECT * FROM pesanan WHERE pesanan_ID LIKE CONCAT('%', ?, '%') OR customer_ID LIKE CONCAT('%', ?, '%')");
                        ps.setString(1, searchInput.getText());
                        ps.setString(2, searchInput.getText());
                    }

                    ResultSet res = ps.executeQuery();

                    DefaultTableModel model = (DefaultTableModel) orderTable.getModel();

                    for (int i = model.getRowCount() - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }

                    while (res.next()) {
                        String orderId = res.getString("pesanan_ID");
                        String customerId = res.getString("customer_ID");
                        String[] row = {orderId, customerId};
                        model.addRow(row);
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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to delete " + orderIdField.getText() + "?"), "Delete Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("DELETE FROM pesanan WHERE pesanan_ID = ?");
                        ps.setString(1, orderIdField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Deleted!");

                        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();

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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to insert " + orderIdField.getText() + "?"), "Create Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO pesanan(pesanan_ID, customer_ID) VALUES (?, ?,)");

                        String randId = new Utils().generateRandom();

                        PreparedStatement idCheck = con.prepareStatement("SELECT pesanan_ID FROM pesanan WHERE pesanan_ID=?");
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
                        ps.setString(2, customerIdField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Inserted!");

                        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        orderIdField.setText("");
                        customerIdField.setText("");
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, String.valueOf(err));
                    }
                }
            }
        });


        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to update " + orderIdField.getText() + "?"), "Update Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("UPDATE pesanan SET pesanan_ID=?, customer_ID=? WHERE pesanan_ID=?");

                        ps.setString(1, orderIdField.getText());
                        ps.setString(2, customerIdField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Updated!");

                        DefaultTableModel model = (DefaultTableModel) orderTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        orderIdField.setText("");
                        customerIdField.setText("");
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, err);
                    }
                }
            }
        });


        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                orderIdField.setText("");
                customerIdField.setText("");
            }
        });
    }
}
