/*package lk.ijse.contraller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPageContraller {
    public TextField usernametxt;
    public TextField passwordtxt;
    private AnchorPane rootNode;

    public void loginOnAction(ActionEvent actionEvent) throws IOException {
        final String username = "dilshan";
        final String password = "1234";
        String uname = usernametxt.getText();
        String pass = passwordtxt.getText();

        if(username.equals(uname)){
            if (password.equals(pass)){
                System.out.println("wade goda !!!");
                NavigateDAshboard();
            }
            else{
                new Alert(Alert.AlertType.ERROR, "oops Password is wrong !!!!").show();
            }
        }
        else {
            new Alert(Alert.AlertType.ERROR, "oops UserName is wrong !!!!").show();
        }
    }

    private void NavigateDAshboard() throws IOException {
        AnchorPane rootNode = FXMLLoader.load(this.getClass().getResource("/view/dashboard_form.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) this.rootNode.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Dashboard Form");
    }
}*/

package lk.ijse.contraller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
public class LoginPageContraller {
    public TextField usernametxt;
    public TextField passwordtxt;
    public AnchorPane rootPane;

    public void loginOnAction(ActionEvent actionEvent) throws IOException {
        final String username = "dilshan";
        final String password = "1234";
        String uname = usernametxt.getText();
        String pass = passwordtxt.getText();

        if (username.equals(uname) && password.equals(pass)) {
            System.out.println("wade goda !!!");
            navigateDashboard();
        } else {
            if (!username.equals(uname)) {
                usernametxt.setStyle("-fx-text-box-border: red; -fx-text-inner-color: red;");
            } else {
                usernametxt.setStyle("-fx-text-box-border: green; -fx-text-inner-color: green;");
            }

            if (!password.equals(pass)) {
                passwordtxt.setStyle("-fx-text-box-border: red; -fx-text-inner-color: red;");
            } else {
                passwordtxt.setStyle("");
            }

        }
    }

    private void navigateDashboard() throws IOException {
        AnchorPane rootNode = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) usernametxt.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Dashboard Form");
    }
}
