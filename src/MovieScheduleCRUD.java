import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class MovieScheduleCRUD extends JFrame {
    private JPanel customerPanel;
    private JTable scheduleTable;
    private JTextField movieIdField;
    private JTextField scheduleIdField;
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
            String query = "SELECT * FROM jadwal_film ORDER BY film_ID";
            ResultSet res = st.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();
            String[] colNames = {"Movie ID", "Schedule ID"};
            model.setColumnIdentifiers(colNames);

            while (res.next()) {
                String movieId = res.getString("film_ID");
                String scheduleId = res.getString("jadwal_ID");
                String[] row = {movieId, scheduleId};
                model.addRow(row);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public MovieScheduleCRUD() {
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

                movieIdField.setText(model.getValueAt(row, 0).toString());
                scheduleIdField.setText(model.getValueAt(row, 1).toString());
            }
        });


        actionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(actionComboBox.getSelectedItem().equals("Create")) {
                    scheduleIdField.setEditable(true);

                    createButton.setEnabled(true);
                    deleteButton.setEnabled(false);
                    updateButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Update")) {
                    scheduleIdField.setEditable(true);

                    updateButton.setEnabled(true);
                    createButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Delete")) {
                    scheduleIdField.setEditable(false);

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
                        ps = con.prepareStatement("SELECT * FROM jadwal_film ORDER BY film_ID");
                    } else {
                        ps = con.prepareStatement("SELECT * FROM jadwal_film WHERE film_ID LIKE CONCAT('%', ?, '%') OR jadwal_ID LIKE CONCAT('%', ?, '%') ORDER BY film_ID");
                        ps.setString(1, searchInput.getText());
                        ps.setString(2, searchInput.getText());
                    }

                    ResultSet res = ps.executeQuery();

                    DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();

                    for (int i = model.getRowCount() - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }

                    while (res.next()) {
                        String movieId = res.getString("film_ID");
                        String scheduleId = res.getString("jadwal_ID");
                        String[] row = {movieId, scheduleId};
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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to delete?"), "Delete Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("DELETE FROM jadwal_film WHERE film_ID = ? AND jadwal_ID = ?");
                        ps.setString(1, movieIdField.getText());
                        ps.setString(2, scheduleIdField.getText());

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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to insert new movie schedule?"), "Create Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO jadwal_film(film_ID, jadwal_ID) VALUES (?, ?)");

                        ps.setString(1, movieIdField.getText());
                        ps.setString(2, scheduleIdField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Inserted!");

                        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        movieIdField.setText("");
                        scheduleIdField.setText("");
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, String.valueOf(err));
                    }
                }
            }
        });


        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to update?"), "Update Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("UPDATE jadwal_film SET jadwal_ID=? WHERE film_ID=?");

                        ps.setString(1, scheduleIdField.getText());
                        ps.setString(2, movieIdField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Updated!");

                        DefaultTableModel model = (DefaultTableModel) scheduleTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        movieIdField.setText("");
                        scheduleIdField.setText("");
                    } catch (SQLException err) {
                        JOptionPane.showMessageDialog(null, err);
                    }
                }
            }
        });


        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movieIdField.setText("");
                scheduleIdField.setText("");
            }
        });
    }
}
