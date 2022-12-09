import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class ClientPanel extends JFrame{
    private JPanel clientPanel;
    private JButton logoutButton;
    private JTabbedPane tabbedPane1;
    private JButton addToCartButton;
    private JTextField searchInput;
    private JButton searchButton;
    private JLabel welcomeLabel;
    private JTable movieTable;
    private JTextField movieTitle;
    private JComboBox cityComboBox;
    private JComboBox cinemaComboBox;
    private JComboBox seatComboBox;
    private JComboBox typeComboBox;
    private JTable cartTable;
    private JTextField totalField;
    private JButton checkOutButton;
    private JComboBox comboBox1;

    public static void main(String[] args) {
        ClientPanel client = new ClientPanel("UX012931", "Toyama Nao");
    }

    PreparedStatement ps;
    Connection con;
    ResultSet res;


    public ClientPanel(String id, String name) {
        DBConnection dbCon = new DBConnection();
        con = dbCon.getCon();

        setContentPane(clientPanel);
        setTitle("Tikuzo");
        setSize(800, 670);
        setLocationRelativeTo(null);
        setVisible(true);
        welcomeLabel.setText("Welcome, " + name);
        movieTable.setDefaultEditor(Object.class, null);

        //populate combobox
        try {
            Statement st = con.createStatement();
            String query = "SELECT DISTINCT kota FROM bioskop ORDER BY kota";
            ResultSet res = st.executeQuery(query);

            while(res.next()) {
                String city = res.getString("kota");
                cityComboBox.addItem(city);
            }
            cinemaComboBox.setEnabled(false);
            typeComboBox.setEnabled(false);
            seatComboBox.setEnabled(false);
        } catch(SQLException e){
            e.printStackTrace();
        }

        //populate movie table
        try {
            Statement st = con.createStatement();
            String query = "SELECT judul, genre, durasi, sinopsis FROM film ORDER BY judul";
            ResultSet res = st.executeQuery(query);

            DefaultTableModel model = (DefaultTableModel) movieTable.getModel();
            String[] colNames = {"Title", "Genre", "Duration", "Synopsis"};
            model.setColumnIdentifiers(colNames);

            String title, genre, duration, synopsis;

            while (res.next()) {
                title = res.getString("judul");
                genre = res.getString("genre");
                duration = res.getString("durasi");
                synopsis = res.getString("sinopsis");
                String[] row = {title, genre, duration, synopsis};
                model.addRow(row);
            }

        } catch(SQLException e){
            e.printStackTrace();
        }

        //populate cart table
        try {
            Statement st = con.createStatement();
            PreparedStatement ps = con.prepareStatement("SELECT tiket_ID, harga FROM tiket t, pesanan p WHERE t.pesanan_ID = p.pesanan_ID AND customer_ID = ?");
            ps.setString(1, id);
            ResultSet res = ps.executeQuery();

            DefaultTableModel cartModel = (DefaultTableModel) cartTable.getModel();
            String[] colNames = {"ID", "Price"};
            cartModel.setColumnIdentifiers(colNames);

            int totalPrice = 0;

            while (res.next()) {
                String ticketId = res.getString("tiket_ID");
                int price = res.getInt("harga");
                totalPrice += price;

                String[] row = {ticketId, new Utils().formatMoney(price)};

                cartModel.addRow(row);
            }

            totalField.setText(new Utils().formatMoney(totalPrice));

        } catch(SQLException e) {
            e.printStackTrace();
        }

        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login().setVisible(true);
                setVisible(false);
            }
        });

        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Added to Cart");
            }
        });

        //movie
        movieTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                int row = movieTable.getSelectedRow();
                DefaultTableModel model = (DefaultTableModel) movieTable.getModel();

                movieTitle.setText(model.getValueAt(row, 0).toString());
            }
        });

        //search
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    PreparedStatement ps;

                    if (searchInput.getText().equals("")) {
                        ps = con.prepareStatement("SELECT judul, genre, durasi, sinopsis FROM film ORDER BY judul");
                    } else {
                        ps = con.prepareStatement("SELECT * FROM film WHERE judul LIKE CONCAT('%', ?, '%') OR genre LIKE CONCAT('%', ?, '%') ORDER BY judul");
                        ps.setString(1, searchInput.getText());
                        ps.setString(2, searchInput.getText());
                    }

                    ResultSet res = ps.executeQuery();

                    String title, genre, duration, synopsis;

                    DefaultTableModel model = (DefaultTableModel) movieTable.getModel();

                    for (int i = model.getRowCount() - 1; i >= 0; i--) {
                        model.removeRow(i);
                    }

                    while (res.next()) {
                        title = res.getString("judul");
                        genre = res.getString("genre");
                        duration = res.getString("durasi");
                        synopsis = res.getString("sinopsis");
                        String[] row = {title, genre, duration, synopsis};
                        model.addRow(row);
                    }

                } catch (SQLException err) {
                    err.printStackTrace();
                }
            }
        });

        //city state change
        cityComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cinemaComboBox.removeAllItems();

                try {
                    PreparedStatement ps = con.prepareStatement("SELECT DISTINCT brand FROM bioskop WHERE kota = ?");
                    ps.setString(1, cityComboBox.getSelectedItem().toString());

                    ResultSet res = ps.executeQuery();

                    if (res != null) {
                        cinemaComboBox.setEnabled(true);
                        while (res.next()) {
                            cinemaComboBox.addItem(res.getString("brand"));
                        }
                    }
                } catch (SQLException err) {
                    err.printStackTrace();
                }
            }
        });


        cinemaComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                typeComboBox.removeAllItems();

                try {
                    String cinemaName = "";
                    if (cityComboBox.getSelectedItem() != null && cinemaComboBox.getSelectedItem() != null) {
                        cinemaName = cinemaComboBox.getSelectedItem().toString() + " " + cityComboBox.getSelectedItem().toString();
                    }

                    PreparedStatement ps = con.prepareStatement("SELECT DISTINCT tipe FROM studio WHERE nama = ?");

                    ps.setString(1, cinemaName);

                    ResultSet res = ps.executeQuery();

                    if (res != null) {
                        typeComboBox.setEnabled(true);
                        while (res.next()) {
                            typeComboBox.addItem(res.getString(1));
                        }
                    }
                } catch (SQLException err) {
                    err.printStackTrace();
                }
            }
        });

        typeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                seatComboBox.removeAllItems();

                try {
                    String cinemaName = "";

                    if (cityComboBox.getSelectedItem() != null && cinemaComboBox.getSelectedItem() != null) {
                        cinemaName = cinemaComboBox.getSelectedItem().toString() + " " + cityComboBox.getSelectedItem().toString();
                    }

                    PreparedStatement ps = con.prepareStatement("SELECT no_kursi FROM kursi_studio\n" +
                            "WHERE studio_ID = (SELECT studio_ID FROM studio WHERE nama = ? ) AND status = 0");
                    ps.setString(1, cinemaName);

                    ResultSet res = ps.executeQuery();

                    if (res != null) {
                        seatComboBox.setEnabled(true);
                        while (res.next()) {
                            seatComboBox.addItem(res.getString("no_kursi"));
                        }
                    }
                } catch (SQLException err) {
                    err.printStackTrace();
                }
            }
        });

        checkOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UIManager.put("OptionPane.okButtonText", "Back");
                JOptionPane.showMessageDialog(null, "Payment success!");
            }
        });
    }
}
