import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class PaymentCRUD extends JFrame {
    private JPanel customerPanel;
    private JTable paymentTable;
    private JTextField paymentIdField;
    private JTextField statusField;
    private JTextField methodField;
    private JTextField orderIdField;
    private JButton backButton;
    private JComboBox actionComboBox;
    private JButton createButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JTextField searchInput;
    private JButton resetButton;
    private JTextField timeField;

    public static void main(String[] args) {

    }

    Connection con;

    void populateTable() {
        try {
            Statement st = con.createStatement();
            String query = "SELECT * FROM pembayaran";
            ResultSet res = st.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) paymentTable.getModel();
            String[] colNames = {"Payment ID", "Status", "Time", "Method", "Order ID"};
            model.setColumnIdentifiers(colNames);

            while (res.next()) {
                String payId = res.getString("pembayaran_ID");
                String status = res.getString("status");
                String time = res.getString("waktu_pembayaran") == null ? "NULL" : res.getString("waktu_pembayaran");
                String method = res.getString("metode");
                String orderId = res.getString("pesanan_ID");
                String[] row = {payId, status, time, method, orderId };
                model.addRow(row);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public PaymentCRUD() {
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


       paymentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int row = paymentTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) paymentTable.getModel();

                paymentIdField.setText(model.getValueAt(row, 0).toString());
                statusField.setText(model.getValueAt(row, 1).toString());
                timeField.setText(model.getValueAt(row, 2).toString());
                methodField.setText(model.getValueAt(row, 3).toString());
                orderIdField.setText(model.getValueAt(row, 4).toString());
            }
       });


        actionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(actionComboBox.getSelectedItem().equals("Create")) {
                    paymentIdField.setEditable(false);
                    statusField.setEditable(true);
                    timeField.setEditable(true);
                    methodField.setEditable(true);
                    orderIdField.setEditable(true);

                    createButton.setEnabled(true);
                    deleteButton.setEnabled(false);
                    updateButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Update")) {
                    paymentIdField.setEditable(false);
                    statusField.setEditable(true);
                    timeField.setEditable(true);
                    methodField.setEditable(true);
                    orderIdField.setEditable(true);

                    updateButton.setEnabled(true);
                    createButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Delete")) {
                    paymentIdField.setEditable(false);
                    statusField.setEditable(false);
                    timeField.setEditable(true);
                    methodField.setEditable(false);
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
                        ps = con.prepareStatement("SELECT * FROM pembayaran ");
                    } else {
                        ps = con.prepareStatement("SELECT * FROM pembayaran WHERE status LIKE CONCAT('%', ?, '%') OR pesanan_ID LIKE CONCAT('%', ?, '%') OR metode LIKE CONCAT('%', ?, '%') OR pembayaran_ID LIKE CONCAT('%', ?, '%')");
                        ps.setString(1, searchInput.getText());
                        ps.setString(2, searchInput.getText());
                        ps.setString(3, searchInput.getText());
                        ps.setString(4, searchInput.getText());
                    }

                    ResultSet res = ps.executeQuery();

                    DefaultTableModel model = (DefaultTableModel) paymentTable.getModel();

                    for (int i = model.getRowCount() - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }

                    while (res.next()) {
                        String payId = res.getString("pembayaran_ID");
                        String status = res.getString("status");
                        String time = res.getString("waktu_pembayaran") == null ? "NULL" : res.getString("waktu_pembayaran");
                        String method = res.getString("metode");
                        String orderId = res.getString("pesanan_ID");
                        String[] row = {payId, status, time, method, orderId };
                        model.addRow(row);
                    }

                } catch (SQLException err) {
                    err.printStackTrace();
                }
            }
        });


        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to delete " + paymentIdField.getText() + "?"), "Delete Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("DELETE FROM pembayaran WHERE pembayaran_ID = ?");
                        ps.setString(1, paymentIdField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Deleted!");

                        DefaultTableModel model = (DefaultTableModel) paymentTable.getModel();

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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to insert new payment?"), "Create Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO pembayaran(pembayaran_ID, status, metode, pesanan_ID) VALUES (?, ?, ?, ?)");

                        String randId = new Utils().generateRandom();

                        PreparedStatement idCheck = con.prepareStatement("SELECT pembayaran_ID FROM pembayaran WHERE pembayaran_ID=?");
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
                        ps.setString(2, statusField.getText());
                        ps.setString(3, methodField.getText());
                        ps.setString(4, orderIdField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Inserted!");

                        DefaultTableModel model = (DefaultTableModel) paymentTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        paymentIdField.setText("");
                        statusField.setText("");
                        timeField.setText("");
                        methodField.setText("");
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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to update " + paymentIdField.getText() + "?"), "Update Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("UPDATE pembayaran SET status=?, waktu_pembayaran=CURRENT_TIMESTAMP, metode=?, pesanan_ID=? WHERE pembayaran_ID=?");

                        ps.setString(1, statusField.getText());
                        ps.setString(2, methodField.getText());
                        ps.setString(3, orderIdField.getText());
                        ps.setString(4, paymentIdField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Updated!");

                        DefaultTableModel model = (DefaultTableModel) paymentTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        paymentIdField.setText("");
                        statusField.setText("");
                        timeField.setText("");
                        methodField.setText("");
                        orderIdField.setText("");
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, err);
                    }
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


        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paymentIdField.setText("");
                statusField.setText("");
                timeField.setText("");
                methodField.setText("");
                orderIdField.setText("");
            }
        });
    }
}
