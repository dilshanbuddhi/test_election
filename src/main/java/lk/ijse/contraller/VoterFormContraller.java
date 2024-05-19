package lk.ijse.contraller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.Utill.Regex;
import lk.ijse.model.Voter;
import lk.ijse.model.tm.VoterTm;
import lk.ijse.repo.ElectionRepo;
import lk.ijse.repo.Election_voter_detailRepo;
import lk.ijse.repo.VoterRepo;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class VoterFormContraller {

    public TextField idtxt;
    public TextField nametxt;
    public TextField addresstxt;
    public TextField dobtxt;
    public TextField agetxt;
    public ComboBox <String>cmbElection;
    public DatePicker datepicker;
    public TableView <VoterTm>votertable;
    public TableColumn<?,?> idclm;
    public TableColumn <?,?>nameclm;
    public TableColumn <?,?>ageclm;
    public TableColumn <?,?>dobclm;
    public TableColumn <?,?>addressclm;
    public TextField gmailtxt;
    public AnchorPane voterAnchorpane;

    public void initialize() throws SQLException {
        setCellValueFactory();
        //loadtbl();
        getVIdlistAsElection();
        setElectionCombo();
    }

    private void setCellValueFactory() {
        idclm.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameclm.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageclm.setCellValueFactory(new PropertyValueFactory<>("age"));
        dobclm.setCellValueFactory(new PropertyValueFactory<>("dob"));
        addressclm.setCellValueFactory(new PropertyValueFactory<>("address"));
    }
//fs
    public void getVIdlistAsElection() throws SQLException {
        String ename = cmbElection.getValue();
        String eid = ElectionRepo.getID(ename);
        List <String> vidlist = Election_voter_detailRepo.getVlist(eid);
        loadtableAselection(vidlist);
    }

    private void loadtableAselection(List<String> vidlist) throws SQLException {
        ObservableList<VoterTm> obList = FXCollections.observableArrayList();
        List<Voter>voterdetail =new ArrayList<>();
        for (String vid : vidlist){
            Voter voter = VoterRepo.SearchData(vid);
            voterdetail.add(voter);
        }

        for (Voter voter : voterdetail){
            VoterTm voterTm = new VoterTm(voter.getId(), voter.getName(), voter.getAge(), voter.getDOB(), voter.getAddress());
            obList.add(voterTm);
        }
        votertable.setItems(obList);
    }
//fs
    public void loadtbl() throws SQLException {
        ObservableList<VoterTm> obList = FXCollections.observableArrayList();
        List<Voter> voterList = VoterRepo.getAllData();
        for (Voter voter : voterList){
            VoterTm voterTm = new VoterTm(voter.getId(), voter.getName(), voter.getAge(), voter.getDOB(), voter.getAddress());
            obList.add(voterTm);
        }
    votertable.setItems(obList);

    }

    private void setElectionCombo() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<String> codeList = ElectionRepo.getElection();

            for (String code : codeList) {
                obList.add(code);
            }
            cmbElection.setItems(obList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void VoterRegisteronAction(ActionEvent actionEvent) throws SQLException {
        String id = idtxt.getText();
        String name = nametxt.getText();
        String address = addresstxt.getText();
        String age = agetxt.getText();
        String dob = String.valueOf(datepicker.getValue());
        String electionName = cmbElection.getValue();
        String gmail = gmailtxt.getText();
        String eid = ElectionRepo.getID(electionName);
        Voter voter = new Voter(id, name, age, dob, address,gmail);
        if (electionName != null) {
            if (isValied()) {
                try {

                    boolean isSaved = VoterRepo.regiVoter(voter, eid);
                    if (isSaved) {
                        //loadtbl();
                        getVIdlistAsElection();
                        ClearField();
                        new Alert(Alert.AlertType.CONFIRMATION, "Voter Saved !!").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "voter not saved").show();
                    }

                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "please input right data").show();
            }

        } else {
            new Alert(Alert.AlertType.ERROR, "please select election!!!").show();
        }
    }



    public void VoterDeleteOnAction(ActionEvent actionEvent) throws SQLException {
        String id = idtxt.getText();


       try{
            boolean isSaved = VoterRepo.deleteTables(id);
            if (isSaved){
                //loadtbl();
                getVIdlistAsElection();
                new Alert(Alert.AlertType.CONFIRMATION,"voter deleted").show();
            }else {
                new Alert(Alert.AlertType.ERROR,"voter not deleted").show();
            }
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();

        }

    }

    public void VoterSearchOnAction(ActionEvent actionEvent) throws SQLException {
        String id = idtxt.getText();
        Voter voter = VoterRepo.SearchData(id);
        try {
            if (voter != null) {

                nametxt.setText(voter.getName());
                agetxt.setText(voter.getAge());
                addresstxt.setText(voter.getAddress());
                datepicker.setValue(LocalDate.parse(voter.getDOB()));
                gmailtxt.setText(voter.getGmail());

            } else {
                new Alert(Alert.AlertType.ERROR, "Voter not found").show();
            }
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void clearOnAction(ActionEvent actionEvent) {
       ClearField();
    }

    private void ClearField() {
        idtxt.setText(null);
        nametxt.setText(null);
        addresstxt.setText(null);
        datepicker.setValue(null);
        agetxt.setText(null);
       // cmbElection.setValue(null);
        gmailtxt.setText(null);
    }

    public void VoterUpdateOnAction(ActionEvent actionEvent) {
        String id = idtxt.getText();
        String name = nametxt.getText();
        String address = addresstxt.getText();
        String dob = String.valueOf(datepicker.getValue());
        String age = agetxt.getText();
        String gmail = gmailtxt.getText();

        Voter voter = new Voter(id, name, age, dob, address,gmail);
        if (idtxt != null & agetxt != null) {
            if (isValied()) {
                try {
                    boolean IsSaved = VoterRepo.Update(voter);
                    if (IsSaved) {
                        //initialize();
                        getVIdlistAsElection();
                        new Alert(Alert.AlertType.INFORMATION, "Voter Updated!!").show();
                    }
                } catch (SQLException e) {
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }
            }else {
                new Alert(Alert.AlertType.ERROR,"input right data").show();
            }
        }else {
            new Alert(Alert.AlertType.ERROR,"first!!!. you search voter for delete").show();
        }
    }

    public void BackOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane rootNode = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) idtxt.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Dashboard Form");
    }

    public void SearchOnEnterButtun(ActionEvent actionEvent) throws SQLException {
        VoterSearchOnAction(actionEvent);
    }

    public void cmbElectionOnAction(ActionEvent actionEvent) throws SQLException {
        getVIdlistAsElection();
    }

    public void AddExistingVoter(ActionEvent actionEvent) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/selectAndAdd_form.fxml"));
        Parent rootNode = loader.load();
        voterAnchorpane.getChildren().clear();
        voterAnchorpane.getChildren().add(rootNode);
    }

    public void nameTxtOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.FULLNAME,nametxt);
    }

    public void idTxtOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.NIC,idtxt);
    }

    public void addressTxtOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.NAME,addresstxt);
    }

    public void ageTxtOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.AGE,agetxt);
    }

    public void gmailTxtOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.EMAIL,gmailtxt);

    }
    public boolean isValied(){
        if (!Regex.setTextColor(lk.ijse.Utill.TextField.AGE,agetxt)) return false;
        if (!Regex.setTextColor(lk.ijse.Utill.TextField.EMAIL,gmailtxt)) return false;
        if (!Regex.setTextColor(lk.ijse.Utill.TextField.NIC,idtxt)) return false;
        if (!Regex.setTextColor(lk.ijse.Utill.TextField.NAME,addresstxt)) return false;
        if (!Regex.setTextColor(lk.ijse.Utill.TextField.FULLNAME,nametxt)) return false;

        return true;

    }
}
