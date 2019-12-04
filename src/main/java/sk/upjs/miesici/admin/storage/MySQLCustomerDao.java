package sk.upjs.miesici.admin.storage;

import javafx.scene.control.Alert;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MySQLCustomerDao implements CustomerDao {
    private JdbcTemplate jdbcTemplate;
    public static int errorCheck = 0;

    public MySQLCustomerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Customer> getAll() {
        String sql = "SELECT klient_id, meno, priezvisko, adresa, email, kredit, permanentka, admin " +
                "FROM klient ";
        List<Customer> result = jdbcTemplate.query(sql, new CustomerResultSetExtractor());
        return result;
    }

    @Override
    public Customer save(Customer customer) {
        if (customer == null)
            return null;
        if (customer.getId() == null) {

            SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate);
            insert.withTableName("klient").usingGeneratedKeyColumns("klient_id");

            Map<String, Object> values = new HashMap<String, Object>();
            values.put("meno", customer.getName());
            values.put("priezvisko", customer.getSurname());
            values.put("adresa", customer.getAddress());
            values.put("email", customer.getEmail());
            values.put("kredit", customer.getCredit());
            values.put("permanentka", customer.getMembershipExp());
            values.put("login", customer.getLogin());
            values.put("heslo", customer.getPassword());
            values.put("sol", customer.getSalt());
            values.put("admin", customer.isAdmin());

            try {
                Number key = insert.executeAndReturnKey(new MapSqlParameterSource(values));
                customer.setId(key.longValue());

            } catch (DuplicateKeyException e) {
                errorCheck = 1;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Neúspešné pridanie");
                alert.setHeaderText("Login s rovnakým názvom už existuje.");
                alert.setContentText("Zvoľte iné prihlasovacie meno!");
                alert.show();
            }
        }
        return customer;
    }

    private Connection connect() {
        String url = "jdbc:mysql://localhost/mydb?serverTimezone=Europe/Bratislava";
        String name = "root";
        String password = "root";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, name, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    @Override
    public void edit(Customer customer) {
        if (customer.getPassword() != null) {
            String sql = "UPDATE klient SET meno = ? , "
                    + "priezvisko = ? , "
                    + "adresa = ? , "
                    + "email = ? , "
                    + "kredit = ? , "
                    + "permanentka = ? , "
                    + "heslo = ? , "
                    + "sol = ?, "
                    + "admin = ? "
                    + "WHERE klient_id = ?;";
            try (Connection conn = this.connect()) {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, customer.getName());
                pstmt.setString(2, customer.getSurname());
                pstmt.setString(3, customer.getAddress());
                pstmt.setString(4, customer.getEmail());
                pstmt.setDouble(5, customer.getCredit());
                pstmt.setDate(6, customer.getMembershipExp());
                pstmt.setString(7, customer.getPassword());
                pstmt.setString(8, customer.getSalt());
                pstmt.setBoolean(9, customer.isAdmin());
                pstmt.setLong(10, customer.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        } else {
            String sql = "UPDATE klient SET meno = ? , "
                    + "priezvisko = ? , "
                    + "adresa = ? , "
                    + "email = ? , "
                    + "kredit = ? , "
                    + "permanentka = ? , "
                    + "admin = ? "
                    + "WHERE klient_id = ?;";
            try (Connection conn = this.connect()) {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, customer.getName());
                pstmt.setString(2, customer.getSurname());
                pstmt.setString(3, customer.getAddress());
                pstmt.setString(4, customer.getEmail());
                pstmt.setDouble(5, customer.getCredit());
                pstmt.setDate(6, customer.getMembershipExp());
                pstmt.setBoolean(7, customer.isAdmin());
                pstmt.setLong(8, customer.getId());
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public Customer getBylogin(String login) {
        List<Customer> list = getAll();
        for (Customer customer : list) {
            if (customer.getName().equals(login)) {
                return customer;
            }
        }
        return null;
    }


// potom vymyslim :D
//    @Override
//    public boolean isAdmin(String login) {
//    	String sql = "SELECT admin FROM klient " + "WHERE login = " +login ;
//    	return false;
//    }

}