import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class ScheduleCRUD extends JFrame {
    private JPanel customerPanel;
    private JTable scheduleTable;
    private JTextField idField;
    private JTextField dateField;
    private JTextField startField;
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
            String query = "SELECT jadwal_ID, tanggal, CONVERT(VARCHAR(5), jam_mulai, 108) jam_mulai FROM jadwal ORDER BY tanggal, jam_mulai";
            ResultSet res = st.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
            String[] colNames = {"ID", "Date", "Start Time"};
            model.setColumnIdentifiers(colNames);

            while (res.next()) {
                String id = res.getString("jadwal_ID");
                String date = res.getString("tanggal");
                String startTime = res.getString("jam_mulai");
                String[] row = {id, date, startTime};
                model.addRow(row);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public ScheduleCRUD() {
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

        scheduleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int row = scheduleTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();

                idField.setText(model.getValueAt(row, 0).toString());
                dateField.setText(model.getValueAt(row, 1).toString());
                startField.setText(model.getValueAt(row, 2).toString());
            }
        });


        actionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(actionComboBox.getSelectedItem().equals("Create")) {
                    dateField.setEditable(true);
                    startField.setEditable(true);

                    createButton.setEnabled(true);
                    deleteButton.setEnabled(false);
                    updateButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Update")) {
                    dateField.setEditable(true);
                    startField.setEditable(true);

                    updateButton.setEnabled(true);
                    createButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Delete")) {
                    dateField.setEditable(false);
                    startField.setEditable(false);

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
                        ps = con.prepareStatement("SELECT jadwal_ID, tanggal, CONVERT(VARCHAR(5), jam_mulai, 108) jam_mulai FROM jadwal ORDER BY tanggal, jam_mulai");
                    } else {
                        ps = con.prepareStatement("SELECT jadwal_ID, tanggal, CONVERT(VARCHAR(5), jam_mulai, 108) jam_mulai FROM jadwal WHERE tanggal LIKE CONCAT('%', ?, '%') OR jadwal_ID LIKE CONCAT('%', ?, '%') ORDER BY tanggal, jam_mulai ");
                        ps.setString(1, searchInput.getText());
                        ps.setString(2, searchInput.getText());
                    }

                    ResultSet res = ps.executeQuery();

                    DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();

                    for (int i = model.getRowCount() - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }

                    while (res.next()) {
                        String id = res.getString("jadwal_ID");
                        String date = res.getString("tanggal");
                        String startTime = res.getString("jam_mulai");
                        String[] row = {id, date, startTime};
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
                        PreparedStatement ps = con.prepareStatement("DELETE FROM jadwal WHERE jadwal_ID = ?");
                        ps.setString(1, idField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Deleted!");

                        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();

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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to insert new schedule?"), "Create Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO jadwal(jadwal_ID, tanggal, jam_mulai) VALUES (?, ?, ?)");

                        String randId = new Utils().generateRandom();

                        PreparedStatement idCheck = con.prepareStatement("SELECT jadwal_ID FROM jadwal WHERE jadwal_ID=?");
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
                        ps.setString(2, dateField.getText());
                        ps.setString(3, startField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Inserted!");

                        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        idField.setText("");
                        dateField.setText("");
                        startField.setText("");
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
                        PreparedStatement ps = con.prepareStatement("UPDATE jadwal SET tanggal=?, jam_mulai=? WHERE jadwal_ID=?");

                        ps.setString(1, dateField.getText());
                        ps.setString(2, startField.getText());
                        ps.setString(3, idField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Updated!");

                        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        idField.setText("");
                        dateField.setText("");
                        startField.setText("");
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
                dateField.setText("");
                startField.setText("");
            }
        });
    }
}
