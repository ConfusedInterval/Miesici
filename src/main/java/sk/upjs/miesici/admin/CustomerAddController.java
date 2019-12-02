package sk.upjs.miesici.admin;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.apache.commons.lang3.RandomStringUtils;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static sk.upjs.miesici.admin.MySQLCustomerDao.errorCheck;

public class CustomerAddController {

    private CustomerDao customerDao = DaoFactory.INSTANCE.getCustomerDao();
    private CustomerFxModel customerModel;
    private Customer savedCustomer;

    public CustomerAddController() {
        customerModel = new CustomerFxModel();
    }

    public CustomerAddController(Customer customer) {
        customerModel = new CustomerFxModel();
        customerModel.load(customer);
    }

    @FXML
    private Button saveButton;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField surnameTextField;

    @FXML
    private TextField addressTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private CheckBox isAdminCheckBox;

    @FXML
    private TextField creditTextField;

    @FXML
    private TextField expireTextField;

    @FXML
    private TextField loginTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    void generatePasswordButtonClick(ActionEvent event) {
        passwordTextField.clear();
        passwordTextField.appendText(generateText());
    }

    @FXML
    void saveCustomerButtonClick(ActionEvent event) {
        Customer customer = new Customer();
        customer.setName(nameTextField.getText());
        customer.setSurname(surnameTextField.getText());
        customer.setAddress(addressTextField.getText());
        customer.setEmail(emailTextField.getText());
        try {
            customer.setCredit(Double.parseDouble(creditTextField.getText()));
        } catch (NumberFormatException e) {
        }
        String date1 = expireTextField.getText();
        try {
            Date date = new SimpleDateFormat("yyyy-mm-dd").parse(date1);
            customer.setMembershipExp(date);
        } catch (ParseException e) {
        }
        customer.setLogin(loginTextField.getText());
        customer.setPassword(passwordTextField.getText());
        customer.setAdmin(isAdminCheckBox.isSelected());
        customer.setSalt(generateText());
        customerModel.getCustomers().add(customer);
        customerModel.load(customer);
        if (customerModel.getName() == null || customerModel.getSurname() == null || customerModel.getAddress() == null || customerModel.getEmail() == null || customerModel.getMembershipExp() == null ||
                customerModel.getLogin() == null || customerModel.getPassword() == null || customerModel.getName().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Neplatný formulár");
            alert.setHeaderText("Údaje nie sú vyplnené správne.");
            alert.setContentText("Prosím vyplňte všetky údaje!");
            alert.show();
        } else {
            this.savedCustomer = customerDao.save(customerModel.getCustomer());
            if (errorCheck == 0) {
                saveButton.getScene().getWindow().hide();
            } else {
                errorCheck = 0;
            }
        }
    }

    @FXML
    void initialize() {
    }

    public Customer getSavedCustomer() {
        return savedCustomer;
    }

    private String generateText() {
        char[] possibleCharacters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?").toCharArray();
        String randomStr = RandomStringUtils.random(32, 0, possibleCharacters.length - 1, true, true, possibleCharacters, new SecureRandom());
        return randomStr;
    }
}