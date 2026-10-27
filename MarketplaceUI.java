import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Properties;

public class MarketplaceUI extends JFrame {
    // Connection details extracted from your FreeSQL panel
    private static final String DB_URL = "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcps)(HOST=db.freesql.com)(PORT=2484))(CONNECT_DATA=(SERVICE_NAME=26ai_un3c1)))";
    private static final String DB_USER = "MUNTAHATOQEER585_SCHEMA_86GKX";
    private static final String DB_PASSWORD = "WC8259g5SCYAKXTU3KP32G!OB5LX1H";

    private JTable productTable;
    private JTable orderTable;
    private DefaultTableModel productModel;
    private DefaultTableModel orderModel;

    public MarketplaceUI() {
        setTitle("E-Commerce Marketplace & Delivery Dashboard");
        setSize(850, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Product Catalog", createProductPanel());
        tabbedPane.addTab("Orders & Delivery", createOrderPanel());

        add(tabbedPane);

        loadProductData();
        loadOrderData();
    }

    private Connection getConnection() throws SQLException {
        Properties props = new Properties();
        props.put("user", DB_USER);
        props.put("password", DB_PASSWORD);
        // TCPS requires wallet/encryption settings or trustStore, but for thin driver
        // with TCPS,
        // we can pass wallet or let Oracle handle trust if supported, or use standard
        // properties.
        // Let's connect using DriverManager directly with the complex URL:
        return DriverManager.getConnection(DB_URL, props);
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = { "Product ID", "Name", "Description", "Base Price ($)" };
        productModel = new DefaultTableModel(columns, 0);
        productTable = new JTable(productModel);

        JButton refreshBtn = new JButton("Refresh Products");
        refreshBtn.addActionListener(e -> loadProductData());

        panel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] columns = { "Order ID", "Buyer ID", "Order Date", "Delivery Status", "Total Amount ($)" };
        orderModel = new DefaultTableModel(columns, 0);
        orderTable = new JTable(orderModel);

        JButton refreshBtn = new JButton("Refresh Orders");
        refreshBtn.addActionListener(e -> loadOrderData());

        panel.add(new JScrollPane(orderTable), BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        return panel;
    }

    private void loadProductData() {
        productModel.setRowCount(0);
        String query = "SELECT ProductID, Name, Description, BasePrice FROM PRODUCT";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("ProductID"),
                        rs.getString("Name"),
                        rs.getString("Description"),
                        rs.getDouble("BasePrice")
                };
                productModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOrderData() {
        orderModel.setRowCount(0);
        String query = "SELECT OrderID, BuyerID, OrderDate, DeliveryStatus, TotalAmount FROM ORDER_TABLE";

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("OrderID"),
                        rs.getInt("BuyerID"),
                        rs.getTimestamp("OrderDate"),
                        rs.getString("DeliveryStatus"),
                        rs.getDouble("TotalAmount")
                };
                orderModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> {
            new MarketplaceUI().setVisible(true);
        });
    }
}