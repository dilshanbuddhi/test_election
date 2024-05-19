package lk.ijse.contraller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.Utill.Regex;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Election;
import lk.ijse.model.Election_party_detail;
import lk.ijse.model.Election_voter_detail;
import lk.ijse.repo.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ElectionFormContraller {
    public TextField electionNametxt;
    public TextField electionTypetxt;



    public Label electionIdiot;
    public ComboBox<String> cmbLocation;
    public TextField searchFromId;
    public DatePicker stIdpicker;
    public DatePicker enddatePicker;
    public AnchorPane anchorepane;

    public void initialize(){
        getCurrenteeId();
        getLo();
    }

    private void getLo() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<String> codeList = LocationRepo.getLocatins();

            for (String code : codeList) {
                obList.add(code);
            }
            cmbLocation.setItems(obList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void getCurrenteeId() {
        try {
            String currentId = ElectionRepo.getCurrenteId();

            String nextOrderId = generateNextOrderId(currentId);
            electionIdiot.setText(nextOrderId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateNextOrderId(String currentId) {
        if(currentId != null) {
            String[] split = currentId.split("E");  //" ", "2"
            int idNum = Integer.parseInt(split[1]);
            return "E" + ++idNum;
        }
        return "E1";
    }

    public void updateOnAction(ActionEvent actionEvent) throws SQLException {
        String id = searchFromId.getText();
        String name = electionNametxt.getText();
        String type = electionTypetxt.getText();
        String stdate = String.valueOf(stIdpicker.getValue());
        String enddate = String.valueOf(enddatePicker.getValue());

        String status = "activated";
        String lname = cmbLocation.getValue();
        try {

            String lid = LocationRepo.getCmbId(lname);

            if (lname != null) {
                Election election = new Election(id, name, type, stdate, enddate, status, lid);

                boolean isSaved = ElectionRepo.updateData(election);
                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "election Updated").show();
                } else {
                    new Alert(Alert.AlertType.ERROR, "wrong data").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "please select location").show();
            }
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
}


    public void DeleteonAction(ActionEvent actionEvent) throws SQLException {
        if(Regex.setTextColor(lk.ijse.Utill.TextField.EID,searchFromId)){

            String id = searchFromId.getText();
            if(id != null) {
               Connection connection = DbConnection.getInstance().getConnection();
               connection.setAutoCommit(false);
                try {
                    boolean idDelated = ElectionRepo.deleteElection(id);
                    if (idDelated) {
                        Election_voter_detail electionVoterDetail = Election_voter_detailRepo.ifHavedata(id);
                        if (electionVoterDetail != null){
                            boolean isdeleteVoterandElection = Election_voter_detailRepo.deletedata(id);
                            if (isdeleteVoterandElection){
                                Election_party_detail electionPartyDetail = Election_party_detailRepo.ifhavedata(id);
                         if (electionPartyDetail != null){
                             boolean isdeleteElectionParty = Election_party_detailRepo.deleteElection(id);
                             if (isdeleteElectionParty){
                                 new Alert(Alert.AlertType.CONFIRMATION,"all data deleted").show();
                             }else {
                                 connection.rollback();
                             }
                         }
                            }else {
                                connection.rollback();
                            }
                        }else{
                            System.out.println("election ekata votersla na");
                            Election_party_detail electionPartyDetail = Election_party_detailRepo.ifhavedata(id);
                            if (electionPartyDetail != null) {
                                boolean isdeleteElectionParty = Election_party_detailRepo.deleteElection(id);
                                if (isdeleteElectionParty){
                                    connection.commit();
                                    new Alert(Alert.AlertType.CONFIRMATION,"all data deleted").show();
                                }else {
                                    connection.rollback();
                                }
                            }else {
                                new Alert(Alert.AlertType.CONFIRMATION,"all data deleted").show();
                                connection.commit();
                            }

                        }
                    }else {
                        new Alert(Alert.AlertType.ERROR,"cant find this electionID").show();
                        connection.rollback();
                    }

                } catch (SQLException e) {
                    connection.rollback();
                    new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
                }finally{
                    connection.setAutoCommit(true);
                }
            }else{
                new Alert(Alert.AlertType.ERROR,"check id and try again!!").show();
            }

        }else {
            new Alert(Alert.AlertType.ERROR,"select valid election id").show();
        }

    }



    public void ClearonAction(ActionEvent actionEvent) {
        searchFromId.setText(null);
        electionNametxt.setText(null);
        electionTypetxt.setText(null);
        cmbLocation.setValue(null);
        stIdpicker.setValue(null);
        enddatePicker.setValue(null);
    }

    public void AddOnAction(ActionEvent actionEvent) throws SQLException {
       String eid = electionIdiot.getText();
       String ename = electionNametxt.getText();
       String etype = electionTypetxt.getText();
        String sdate = String.valueOf(stIdpicker.getValue());
        String edate = String.valueOf(enddatePicker.getValue());
       String Lname = cmbLocation.getValue();
       String status = "active";
        String Lid = LocationRepo.getCmbId(Lname);
                Election election = new Election(eid, ename, etype, sdate, edate,status,Lid);
         if (isValied()) {
             try {
                 if (Lname != null) {
                     boolean IsSaved = ElectionRepo.savedata(election);
                     if (IsSaved) {
                         ClearonAction(actionEvent);
                         new Alert(Alert.AlertType.INFORMATION, "Election detail saved").show();
                         getCurrenteeId();
                     }
                 } else {
                     new Alert(Alert.AlertType.ERROR, "please select location").show();
                 }
             } catch (SQLException e) {
                 new Alert(Alert.AlertType.ERROR,"iput all data ").show();
             }
         }else {
             new Alert(Alert.AlertType.ERROR,"please input right data").show();
         }
    }



    public void newlLocation(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/addNewLocation_form.fxml"));
        Parent rootNode = loader.load();
        anchorepane.getChildren().clear();
        anchorepane.getChildren().add(rootNode);
    }

    public void backToDashboard(ActionEvent actionEvent) throws IOException {
        AnchorPane rootNode = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) stIdpicker.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Dashboard Form");
    }

    public void searchfromIdOnAction(ActionEvent actionEvent) throws SQLException {

        String id = searchFromId.getText();
try{
    Election election = ElectionRepo.searchData(id);
    String lname =LocationRepo.getLocationName(election.getLid());
    System.out.println(election.getLid());

    if (election != null){
        System.out.println(lname);
        electionNametxt.setText(election.getEname());
        electionTypetxt.setText(election.getEtype());

        cmbLocation.setValue(lname);
        stIdpicker.setValue(LocalDate.parse(election.getSdate()));
        enddatePicker.setValue(LocalDate.parse(election.getEdate()));
    }
    else {
        new Alert(Alert.AlertType.ERROR, "election not found").show();
    }
}catch (Exception e){
    new Alert(Alert.AlertType.ERROR,"can not find this election ...try again!!").show();
}

    }

    public boolean isValied(){
        if (!Regex.setTextColor(lk.ijse.Utill.TextField.FULLNAME,electionNametxt)) return false;
        if (!Regex.setTextColor(lk.ijse.Utill.TextField.NAME,electionTypetxt)) return false;
        return true;
    }

    public void enameOnKeyReleasedAction(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.FULLNAME,electionNametxt);
    }

    public void etypeOnKeyReleasedAction(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.NAME,electionTypetxt);
    }

    public void searchingIdOnKeyReleasedAction(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.EID,searchFromId);
    }
}
