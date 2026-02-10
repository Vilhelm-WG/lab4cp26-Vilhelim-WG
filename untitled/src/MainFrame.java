import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainFrame extends JFrame {
    // Поля введення (згідно з варіантом 3) 
    private JTextField txtName, txtCPU, txtFreq, txtVideo, txtCD, txtSound, txtHDD;
    private JTable table;
    private DefaultTableModel tableModel;

    public MainFrame() {
        setTitle("Облік комп'ютерів (Варіант 3)"); [cite: 12]
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Панель введення даних (Аналог форми введення) [cite: 10] ---
        JPanel inputPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Дані комп'ютера"));

        inputPanel.add(new JLabel("Назва комп'ютера:"));
        txtName = new JTextField(); inputPanel.add(txtName);

        inputPanel.add(new JLabel("Тип процесора:"));
        txtCPU = new JTextField(); inputPanel.add(txtCPU);

        inputPanel.add(new JLabel("Тактова частота (GHz):"));
        txtFreq = new JTextField(); inputPanel.add(txtFreq);

        inputPanel.add(new JLabel("Відеокарта:"));
        txtVideo = new JTextField(); inputPanel.add(txtVideo);

        inputPanel.add(new JLabel("CDROM/CDRW:"));
        txtCD = new JTextField(); inputPanel.add(txtCD);

        inputPanel.add(new JLabel("Звукова карта:"));
        txtSound = new JTextField(); inputPanel.add(txtSound);

        inputPanel.add(new JLabel("Обсяг вінчестера (GB):"));
        txtHDD = new JTextField(); inputPanel.add(txtHDD);

        JButton btnAdd = new JButton("Додати запис");
        inputPanel.add(btnAdd);

        add(inputPanel, BorderLayout.WEST);

        // --- Таблиця для відображення (Аналог головної форми) [cite: 10] ---
        tableModel = new DefaultTableModel(new String[]{
                "ID", "Назва", "Процесор", "Частота", "Відео", "CD", "Звук", "HDD"
        }, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // --- Логіка кнопки "Додати" (Аналог джерела [cite: 109-121]) ---
        btnAdd.addActionListener(e -> {
            // Перевірка на порожні поля [cite: 110-121]
            if (txtName.getText().isEmpty() || txtCPU.getText().isEmpty() || txtFreq.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Помилка: Необхідно заповнити обов'язкові поля!", "Error", JOptionPane.ERROR_MESSAGE); [cite: 119]
                return; [cite: 120]
            }
            saveToMySQL();
            loadData(); // Оновити таблицю
        });

        loadData(); // Завантажити дані при запуску
    }

    private Connection connect() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/ComputerShop";
        String user = "root"; // замініть на ваш
        String pass = "password"; // замініть на ваш
        return DriverManager.getConnection(url, user, pass); [cite: 123-125]
    }

    private void saveToMySQL() {
        // SQL запит на вставку (Аналог джерела [cite: 126-129])
        String sql = "INSERT INTO Computers (name, cpu_type, frequency, video_card, cd_type, sound_card, hdd_volume) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, txtName.getText());
            pstmt.setString(2, txtCPU.getText());
            pstmt.setDouble(3, Double.parseDouble(txtFreq.getText()));
            pstmt.setString(4, txtVideo.getText());
            pstmt.setString(5, txtCD.getText());
            pstmt.setString(6, txtSound.getText());
            pstmt.setInt(7, Integer.parseInt(txtHDD.getText()));

            pstmt.executeUpdate(); [cite: 131]
            JOptionPane.showMessageDialog(this, "Запис додано успішно!"); [cite: 132]
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Помилка БД: " + ex.getMessage(), "bad", JOptionPane.ERROR_MESSAGE); [cite: 135-136]
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection con = connect(); Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Computers")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"), rs.getString("name"), rs.getString("cpu_type"),
                        rs.getFloat("frequency"), rs.getString("video_card"),
                        rs.getString("cd_type"), rs.getString("sound_card"), rs.getInt("hdd_volume")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}