import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class MovieCRUD extends JFrame {
    private JPanel customerPanel;
    private JTable movieTable;
    private JTextField idField;
    private JTextField titleField;
    private JTextField durationField;
    private JTextField genreField;
    private JTextField synopsisField;
    private JButton backButton;
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
            String query = "SELECT film_ID, judul, durasi, genre, sinopsis FROM film ORDER BY judul";
            ResultSet res = st.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) movieTable.getModel();
            String[] colNames = {"ID", "Title", "Duration", "Genre", "Synopsis"};
            model.setColumnIdentifiers(colNames);

            while (res.next()) {
                String id = res.getString("film_ID");
                String title = res.getString("judul");
                String duration = res.getString("durasi");
                String genre = res.getString("genre");
                String synopsis = res.getString("sinopsis");
                String[] row = {id, title, duration, genre, synopsis};
                model.addRow(row);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    public MovieCRUD() {
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

        movieTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int row = movieTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) movieTable.getModel();

                idField.setText(model.getValueAt(row, 0).toString());
                titleField.setText(model.getValueAt(row, 1).toString());
                durationField.setText(model.getValueAt(row, 2).toString());
                genreField.setText(model.getValueAt(row, 3).toString());
                synopsisField.setText(model.getValueAt(row, 4).toString());
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement ps;

                    if (searchInput.getText().equals("")) {
                        ps = con.prepareStatement("SELECT * FROM film ORDER BY judul");
                    } else {
                        ps = con.prepareStatement("SELECT * FROM film WHERE judul LIKE CONCAT('%', ?, '%') OR genre LIKE CONCAT('%', ?, '%') ORDER BY judul");
                        ps.setString(1, searchInput.getText());
                        ps.setString(2, searchInput.getText());
                    }

                    ResultSet res = ps.executeQuery();

                    DefaultTableModel model = (DefaultTableModel) movieTable.getModel();

                    for (int i = model.getRowCount() - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }

                    while (res.next()) {
                        String id = res.getString("film_ID");
                        String title = res.getString("judul");
                        String duration = res.getString("durasi");
                        String genre = res.getString("genre");
                        String synopsis = res.getString("sinopsis");
                        String[] row = {id, title, duration, genre, synopsis};
                        model.addRow(row);
                    }

                } catch (SQLException err) {
                    err.printStackTrace();
                }
            }
        });


        actionComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(actionComboBox.getSelectedItem().equals("Create")) {
                    idField.setEditable(false);
                    titleField.setEditable(true);
                    genreField.setEditable(true);
                    durationField.setEditable(true);
                    synopsisField.setEditable(true);

                    createButton.setEnabled(true);
                    deleteButton.setEnabled(false);
                    updateButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Update")) {
                    idField.setEditable(false);
                    titleField.setEditable(true);
                    genreField.setEditable(true);
                    durationField.setEditable(true);
                    synopsisField.setEditable(true);

                    updateButton.setEnabled(true);
                    createButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                } else if (actionComboBox.getSelectedItem().equals("Delete")) {
                    idField.setEditable(false);
                    titleField.setEditable(false);
                    genreField.setEditable(false);
                    durationField.setEditable(false);
                    synopsisField.setEditable(false);

                    deleteButton.setEnabled(true);
                    updateButton.setEnabled(false);
                    createButton.setEnabled(false);
                };
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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to delete " + titleField.getText() + "?"), "Delete Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("DELETE FROM film WHERE film_ID = ?");
                        ps.setString(1, idField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Deleted!");

                        DefaultTableModel model = (DefaultTableModel) movieTable.getModel();

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
                int response = JOptionPane.showConfirmDialog(null, ("Are you sure want to insert " + titleField.getText() + "?"), "Create Record", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

                if (response == JOptionPane.YES_OPTION) {
                    try {
                        PreparedStatement ps = con.prepareStatement("INSERT INTO film(film_ID, judul, durasi, genre, sinopsis) VALUES (?, ?, ?, ?, ?)");

                        String randId = new Utils().generateRandom();

                        PreparedStatement idCheck = con.prepareStatement("SELECT film_ID FROM film WHERE film_ID=?");
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
                        ps.setString(2, titleField.getText());
                        ps.setString(3, durationField.getText());
                        ps.setString(4, genreField.getText());
                        ps.setString(5, synopsisField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Inserted!");

                        DefaultTableModel model = (DefaultTableModel) movieTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        titleField.setText("");
                        durationField.setText("");
                        genreField.setText("");
                        synopsisField.setText("");
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
                        PreparedStatement ps = con.prepareStatement("UPDATE film SET judul=?, durasi=?, genre=?, sinopsis=? WHERE film_ID=?");

                        ps.setString(1, titleField.getText());
                        ps.setString(2, durationField.getText());
                        ps.setString(3, genreField.getText());
                        ps.setString(4, synopsisField.getText());
                        ps.setString(5, idField.getText());

                        ps.executeUpdate();

                        JOptionPane.showMessageDialog(null, "Updated!");

                        DefaultTableModel model = (DefaultTableModel) movieTable.getModel();

                        for (int i = model.getRowCount() - 1; i >= 0; i--) {
                            model.removeRow(i);
                        }

                        populateTable();

                        idField.setText("");
                        titleField.setText("");
                        durationField.setText("");
                        genreField.setText("");
                        synopsisField.setText("");
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
                titleField.setText("");
                durationField.setText("");
                genreField.setText("");
                synopsisField.setText("");
            }
        });
    }

}
