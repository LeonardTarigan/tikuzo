import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.Vector;

public class AdminPanel extends JFrame {
    private JPanel adminPanel;
    private JTabbedPane tabbedPane1;
    private JButton backButton;
    private JButton moviesButton;
    private JButton scheduleButton;

    public static void main(String[] args) {
        AdminPanel adm = new AdminPanel();

        String url = "jdbc:sqlserver://LEONARDS-ACER\\SQLEXPRESS;database=University;integratedSecurity=true;encrypt=true;trustServerCertificate=true;";

        try {
            Connection con = DriverManager.getConnection(url);
            Statement st = con.createStatement();
            String q = "SELECT TOP 5 * FROM instructor";
            ResultSet res = st.executeQuery(q);

            while(res.next()) {
                System.out.println(res.getInt(1) + " " + res.getString(2));
            }

            resToTableModel(res);


        }catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
        }
    }

    public AdminPanel() {
        setContentPane(adminPanel);
        setTitle("Tikuzo");
        setSize(450, 300);
        setVisible(true);

        //back to login
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Login().setVisible(true);
                setVisible(false);
            }
        });


    }

    public static TableModel resToTableModel(ResultSet rs) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int numOfColumns = metaData.getColumnCount();
            Vector columnNames = new Vector();

            for (int column = 0; column < numOfColumns;  column++) {
                columnNames.addElement(metaData.getColumnLabel(column + 1));
            }

            Vector rows = new Vector();

            while (rs.next()) {
                Vector newRow = new Vector();

                for (int i = 1; i <= numOfColumns; i++) {
                    newRow.addElement(rs.getObject(i));
                }

                rows.addElement(newRow);
            }

            return new DefaultTableModel(rows, columnNames);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
