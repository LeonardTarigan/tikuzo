import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class CustomerCRUD extends JFrame {
    private JPanel customerPanel;
    private JTable customerTable;
    private JButton backButton;
    private JTextField idField;
    private JTextField nameField;
    private JTextField passwordField;
    private JTextField phoneNumField;
    private JTextField emailField;
    private JComboBox actionComboBox;
    private JButton createButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JTextField searchInput;
    private JButton resetButton;

    public static void main(String[] args) {

    }

    Connection con;

    void populateTable() {
        try {
            Statement st = con.createStatement();
            String query = "SELECT * FROM customer ORDER BY nama";
            ResultSet res = st.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) customerTable.getModel();
            String[] colNames = {"ID", "Name", "Password", "Phone Number", "Email"};
            model.setColumnIdentifiers(colNames);

            while (res.next()) {
                String id = res.getString("customer_ID");
                String name = res.getString("nama");
                String password = res.getString("password");
                String phone = res.getString("no_hp");
                String email = res.getString("email");
                String[] row = {id, name, password, phone, email};
                model.addRow(row);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public CustomerCRUD() {
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

        customerTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int row = customerTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) customerTable.getModel();

                idField.setText(model.getValueAt(row, 0).toString());
                nameField.setText(model.getValueAt(row, 1).toString());
                passwordField.setText(model.getValueAt(row, 2).toString());
                phoneNumField.setText(model.getValueAt(row, 3).toString());
                emailField.setText(model.getValueAt(row, 4).toString());
            }
        });


        actionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(actionComboBox.getSelectedItem().equals("Create")) {
                    nameField.setEditable(true);
                    passwordField.setEditable(true);
                    phoneNumField.setEditable(true);
                    emailField.setEditable(true);

                    createButton.setEnabled(true);
                    deleteButton.setEnabled(false);
                    updateButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Update")) {
                    nameField.setEditable(true);
                    passwordField.setEditable(true);
                    phoneNumField.setEditable(true);
                    emailField.setEditable(true);

                    updateButton.setEnabled(true);
                    createButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Delete")) {
                    nameField.setEditable(false);
                    passwordField.setEditable(false);
                    phoneNumField.setEditable(false);
                    emailField.setEditable(false);

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
                        ps = con.prepareStatement("SELECT * FROM customer ORDER BY nama");
                    } else {
                        ps = con.prepareStatement("SELECT * FROM customer WHERE nama LIKE CONCAT('%', ?, '%') OR email LIKE CONCAT('%', ?, '%') ORDER BY nama");
                        ps.setString(1, searchInput.getText());
                        ps.setString(2, searchInput.getText());
                    }

                    ResultSet res = ps.executeQuery();

                    DefaultTableModel model = (DefaultTableModel) customerTable.getModel();

                    for (int i = model.getRowCount() - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }

                    while (res.next()) {
                        String id = res.getString("customer_ID");
                        String name = res.getString("nama");
                        String password = res.getString("password");
                        String phone = res.getString("no_hp");
                        String email = res.getString("email");
                        String[] row = {id, name, password, phone, email};
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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to delete " + idField.getText() + "?"), "Delete Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("DELETE FROM customer WHERE customer_ID = ?");
                        ps.setString(1, idField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Deleted!");

                        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();

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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to insert " + nameField.getText() + "?"), "Create Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO customer(customer_ID, nama, password, no_hp, email) VALUES (?, ?, ?, ?, ?)");

                        String randId = new Utils().generateRandom();

                        PreparedStatement idCheck = con.prepareStatement("SELECT customer_ID FROM customer WHERE customer_ID=?");
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
                        ps.setString(2, nameField.getText());
                        ps.setString(3, passwordField.getText());
                        ps.setString(4, phoneNumField.getText());
                        ps.setString(5, emailField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Inserted!");

                        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        nameField.setText("");
                        passwordField.setText("");
                        phoneNumField.setText("");
                        emailField.setText("");
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, String.valueOf(err));
                    }
                }
            }
        });


        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to update " + idField.getText() + "?"), "Update Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("UPDATE customer SET nama=?, password=?, no_hp=?, email=? WHERE customer_ID=?");

                        ps.setString(1, nameField.getText());
                        ps.setString(2, passwordField.getText());
                        ps.setString(3, phoneNumField.getText());
                        ps.setString(4, emailField.getText());
                        ps.setString(5, idField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Updated!");

                        DefaultTableModel model = (DefaultTableModel) customerTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        idField.setText("");
                        nameField.setText("");
                        passwordField.setText("");
                        phoneNumField.setText("");
                        emailField.setText("");
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, err);
                    }
                }
            }
        });


        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                idField.setText("");
                nameField.setText("");
                passwordField.setText("");
                phoneNumField.setText("");
                emailField.setText("");
            }
        });

    }
}
