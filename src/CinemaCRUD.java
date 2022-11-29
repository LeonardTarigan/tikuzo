import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class CinemaCRUD extends JFrame {
    private JPanel customerPanel;
    private JTable cinemaTable;
    private JTextField nameField;
    private JTextField brandField;
    private JTextField addressField;
    private JTextField cityField;
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
            String query = "SELECT * FROM bioskop ORDER BY nama";
            ResultSet res = st.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) cinemaTable.getModel();
            String[] colNames = {"Name", "Brand", "Address", "City"};
            model.setColumnIdentifiers(colNames);

            while (res.next()) {
                String name = res.getString("nama");
                String brand = res.getString("brand");
                String address = res.getString("jalan");
                String city = res.getString("kota");
                String[] row = {name, brand, address, city};
                model.addRow(row);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public CinemaCRUD() {
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

        cinemaTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int row = cinemaTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) cinemaTable.getModel();

                nameField.setText(model.getValueAt(row, 0).toString());
                brandField.setText(model.getValueAt(row, 1).toString());
                addressField.setText(model.getValueAt(row, 2).toString());
                cityField.setText(model.getValueAt(row, 3).toString());
            }
        });


        actionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(actionComboBox.getSelectedItem().equals("Create")) {
                    nameField.setEditable(true);
                    brandField.setEditable(true);
                    addressField.setEditable(true);
                    cityField.setEditable(true);

                    createButton.setEnabled(true);
                    deleteButton.setEnabled(false);
                    updateButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Update")) {
                    nameField.setEditable(true);
                    brandField.setEditable(true);
                    addressField.setEditable(true);
                    cityField.setEditable(true);

                    updateButton.setEnabled(true);
                    createButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Delete")) {
                    nameField.setEditable(false);
                    brandField.setEditable(false);
                    addressField.setEditable(false);
                    cityField.setEditable(false);

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
                        ps = con.prepareStatement("SELECT * FROM bioskop ORDER BY nama");
                    } else {
                        ps = con.prepareStatement("SELECT * FROM bioskop WHERE nama LIKE CONCAT('%', ?, '%') OR brand LIKE CONCAT('%', ?, '%') OR kota LIKE CONCAT('%', ?, '%') ORDER BY nama");
                        ps.setString(1, searchInput.getText());
                        ps.setString(2, searchInput.getText());
                    }

                    ResultSet res = ps.executeQuery();

                    DefaultTableModel model = (DefaultTableModel) cinemaTable.getModel();

                    for (int i = model.getRowCount() - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }

                    while (res.next()) {
                        String name = res.getString("nama");
                        String brand = res.getString("brand");
                        String address = res.getString("jalan");
                        String city = res.getString("kota");
                        String[] row = {name, brand, address, city};
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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to delete " + nameField.getText() + "?"), "Delete Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("DELETE FROM bioskop WHERE nama = ?");
                        ps.setString(1, nameField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Deleted!");

                        DefaultTableModel model = (DefaultTableModel) cinemaTable.getModel();

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
                        PreparedStatement ps = con.prepareStatement("INSERT INTO bioskop(nama, brand, jalan, kota) VALUES (?, ?, ?, ?)");

                        ps.setString(1, nameField.getText());
                        ps.setString(2, brandField.getText());
                        ps.setString(3, addressField.getText());
                        ps.setString(4, cityField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Inserted!");

                        DefaultTableModel model = (DefaultTableModel) cinemaTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        nameField.setText("");
                        brandField.setText("");
                        addressField.setText("");
                        cityField.setText("");
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, String.valueOf(err));
                    }
                }
            }
        });


        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to update " + nameField.getText() + "?"), "Update Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("UPDATE bioskop SET nama=?, brand=?, jalan=?, kota=? WHERE nama=?");

                        ps.setString(1, nameField.getText());
                        ps.setString(2, brandField.getText());
                        ps.setString(3, addressField.getText());
                        ps.setString(4, cityField.getText());
                        ps.setString(5, nameField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Updated!");

                        DefaultTableModel model = (DefaultTableModel) cinemaTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        nameField.setText("");
                        brandField.setText("");
                        addressField.setText("");
                        cityField.setText("");
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, err);
                    }
                }
            }
        });


        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.setText("");
                brandField.setText("");
                addressField.setText("");
                cityField.setText("");
            }
        });
    }
}
