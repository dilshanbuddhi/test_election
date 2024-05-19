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
import lk.ijse.db.DbConnection;
import lk.ijse.model.Candidate;
import lk.ijse.model.Party;
import lk.ijse.model.tm.Partytm;
import lk.ijse.repo.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static lk.ijse.repo.PartyRepo.getData;

public class PartyFormContraller {

    public TextField partynametxt;
    public TextField partyleadertxt;
    public ComboBox <String> electioncmb;
    public Label partyIdtxt;
    public TextField searchingPID;
    public TableView<Partytm> partyListTbl;
    public TableColumn<?,?> partyIdClm;
    public TableColumn <?,?>pNameClm;
    public TableColumn<?,?> pLeaderClm;
    public AnchorPane anchorepane;

    public void initialize(){
        setElectionCombo();
        getCurrentepId();
    }
    private void getCurrentepId() {
        try {
            String currentId = PartyRepo.getCurrentId();

            String nextOrderId = generateNextOrderId(currentId);
            partyIdtxt.setText(nextOrderId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateNextOrderId(String currentId) {
        if(currentId != null) {
            String[] split = currentId.split("P");
            int idNum = Integer.parseInt(split[1]);
            return "P" + ++idNum;
        }
        return "P1";
    }
    private void setCellValueFactory() {
        partyIdClm.setCellValueFactory(new PropertyValueFactory<>("pid"));
        pNameClm.setCellValueFactory(new PropertyValueFactory<>("pname"));
        pLeaderClm.setCellValueFactory(new PropertyValueFactory<>("pLeader"));
    }

    public void loadTableAsElection(ActionEvent actionEvent) throws SQLException {
        String ename = electioncmb.getValue();
        String eid = ElectionRepo.getID(ename);
        ObservableList<String> obList = FXCollections.observableArrayList();

        List<String> pidList = Election_party_detailRepo.getpartyID(eid);

        for (String pids : pidList) {

            obList.add(pids);
        }
        getPartyList(obList);
    }

    private void getPartyList(ObservableList<String> obList) throws SQLException {
        ObservableList<Partytm> obListP = FXCollections.observableArrayList();
        List<Party> obListParty = new ArrayList<>();

        for (String pid : obList) {
            Party party = getData(pid);
            if (party != null) {
                obListParty.add(party);
            }
        }

        for (Party party : obListParty){
            Partytm partytm = new Partytm(
                    party.getPid(),
                    party.getPName(),
                    party.getPLeader()
            );
            obListP.add(partytm);

        }
        partyListTbl.setItems(obListP);
    }


    private void setElectionCombo() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<String> electionList = ElectionRepo.getElection();

            for (String election : electionList) {
                obList.add(election);
            }
            electioncmb.setItems(obList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void AddOnAction(ActionEvent actionEvent) throws SQLException {
        String pid = partyIdtxt.getText();
        String pname = partynametxt.getText();
        String pleader = partyleadertxt.getText();
        String ename = electioncmb.getValue();

        String eid = ElectionRepo.getID(ename);
        Party party = new Party(pid, pname, pleader);
        if (isValied()) {
            try {
                if (ename != null) {
                    if (Election_party_detailRepo.checkalreadyAdded(eid, pid)) {
                        boolean isSaved = PartyRepo.regiParty(party, eid);
                        if (isSaved) {
                            clearfields();
                            getCurrentepId();
                            loadtable();
                            new Alert(Alert.AlertType.CONFIRMATION, "party Saved").show();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "party not Added").show();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "alredy added this party ").show();
                    }

                } else {
                    new Alert(Alert.AlertType.ERROR, "please select election").show();
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    public void BackOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane rootNode = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) partyleadertxt.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Dashboard Form");
    }

    public void searchOnAction(ActionEvent actionEvent) throws SQLException {
        String pid = searchingPID.getText();
        Party party = PartyRepo.SearchpData(pid);
try {
    if (party != null) {
        partynametxt.setText(party.getPName());
        partyleadertxt.setText(party.getPLeader());
    } else {
        new Alert(Alert.AlertType.ERROR, "cant find this party !!!!").show();
    }
}catch (Exception e){
    new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
}
    }

    public void selectandaddElectionOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/selectandAddParty_form.fxml"));
        Parent rootNode = loader.load();
        anchorepane.getChildren().clear();
        anchorepane.getChildren().add(rootNode);
    }

    public void ClearOnAction(ActionEvent actionEvent) {
        electioncmb.setValue(null);
        clearfields();
    }

    private void clearfields() {
        partyleadertxt.setText("");
        partynametxt.setText("");

    }

    public void UpdateOnAction(ActionEvent actionEvent) throws SQLException {
        if (isValied()) {
            try {
                String id = searchingPID.getText();
                String upname = partynametxt.getText();
                String upleader = partyleadertxt.getText();
                String upelectionname = electioncmb.getValue();

                String upeid = ElectionRepo.getID(upelectionname);
                Party party = new Party(id, upname, upleader);
                boolean isSaved = PartyRepo.update(party);

                if (isSaved) {
                    new Alert(Alert.AlertType.CONFIRMATION, "party updated").show();
                    loadtable();
                } else {
                    new Alert(Alert.AlertType.ERROR, "party not updated").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }
    }

    private boolean isValied() {
        if (!Regex.setTextColor(lk.ijse.Utill.TextField.NAME,partynametxt)) return false;
        if (!Regex.setTextColor(lk.ijse.Utill.TextField.FULLNAME,partyleadertxt)) return false;
        return true;
    }

    public void deleteOnAction(ActionEvent actionEvent) throws SQLException {


        String deleteId =searchingPID.getText();
        Connection connection = DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        try{
            boolean isSaved1 = PartyRepo.deleteParty(deleteId);
            if (isSaved1) {
                boolean issaved2 = Election_party_detailRepo.deleteParty(deleteId);
                if (issaved2){

                    Candidate candidate = CandidateRepo.ifhavecandidatte(deleteId);
                    if (candidate!=null){
                        boolean isdelete = CandidateRepo.deletefromPid(deleteId);
                        if (isdelete){
                             loadtable();
                    new Alert(Alert.AlertType.CONFIRMATION,"party deleted").show();
            connection.commit();
                        }else{
                            connection.rollback();
                        }
                    }else {
                          loadtable();
                    new Alert(Alert.AlertType.CONFIRMATION,"party deleted").show();
            connection.commit();
                    }

                }else {
                    new Alert(Alert.AlertType.ERROR,"party delete failed").show();
                    connection.rollback();
                }
            }else {
                new Alert(Alert.AlertType.ERROR,"party delete failed").show();
                connection.rollback();
            }

        }catch (Exception e){
            connection.rollback();
        }finally {
            connection.setAutoCommit(true);
        }
    }

    public void loadTableOnCmbAction(ActionEvent actionEvent) throws SQLException {
     loadtable();
    }

    public void loadtable() throws SQLException {
        setCellValueFactory();
        loadTableAsElection(new ActionEvent());
    }


    public void pnameOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.NAME,partynametxt);
    }

    public void pLeaderOnKeyreleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.FULLNAME,partyleadertxt);
    }

    public void pidOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.PID,searchingPID);
    }
}
