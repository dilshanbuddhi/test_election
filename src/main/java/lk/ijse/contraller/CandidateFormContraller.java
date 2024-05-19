package lk.ijse.contraller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.Utill.Regex;
import lk.ijse.model.Area;
import lk.ijse.model.Candidate;
import lk.ijse.model.tm.CandidateTm;
import lk.ijse.repo.AreaRepo;
import lk.ijse.repo.CandidateRepo;
import lk.ijse.repo.PartyRepo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CandidateFormContraller {
    public TextField cIdtxt;
    public TextField cNametxt;
    public TextField Caddresstxt;
    public ComboBox <String> cmbParty;

    public TextField territorryTxt;
    public ComboBox <String> cmbDistric;
    public TableView <CandidateTm>candidatetable;
    public TableColumn<?,?> idclm;
    public TableColumn<?,?> nameclm;
    public TableColumn <?,?>addressclm;
    public TableColumn<?,?> areaidclm;
    public ComboBox <String> divisionCmb;

    public void initialize(){
        setCmbParty();
        setCmbDistric();

    }



    private void setCellValueFactory() {
        idclm.setCellValueFactory(new PropertyValueFactory<>("cid"));
        nameclm.setCellValueFactory(new PropertyValueFactory<>("cname"));
        addressclm.setCellValueFactory(new PropertyValueFactory<>("address"));
        areaidclm.setCellValueFactory(new PropertyValueFactory<>("areaid"));

    }

    public void setTable() throws SQLException {
        String pname = cmbParty.getValue();
        String pid = PartyRepo.getpartyId(pname);

        ObservableList<CandidateTm> obList = FXCollections.observableArrayList();

        List<Candidate>candidatelist = CandidateRepo.getAllData(pid);
        for (Candidate candidate : candidatelist){
            CandidateTm candidateTm = new CandidateTm(candidate.getCid(), candidate.getCname(), candidate.getAddress(), candidate.getAid());
            obList.add(candidateTm);
        }
            candidatetable.setItems(obList);
    }

    public void loadTable() throws SQLException {
        setCellValueFactory();
        setTable();
    }

    private void setCmbDistric() {
        ObservableList<String> districtList = FXCollections.observableArrayList(
                "Ampara", "Anuradhapura", "Badulla", "Batticaloa", "Colombo",
                "Galle", "Gampaha", "Hambantota", "Jaffna", "Kalutara", "Kandy",
                "Kegalle", "Kilinochchi", "Kurunegala", "Mannar", "Matale",
                "Matara", "Monaragala", "Mullaitivu", "Nuwara Eliya", "Polonnaruwa",
                "Puttalam", "Ratnapura", "Trincomalee", "Vavuniya"
        );

        cmbDistric.setItems(districtList);
    }

    private void setCmbParty() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<String> partyList = PartyRepo.getParty();

            for (String party : partyList) {
                obList.add(party);
            }
            cmbParty.setItems(obList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addOnAction(ActionEvent actionEvent) throws SQLException {
        String status = "active";
        String currentId = AreaRepo.getCurrentId();
        String nextId = getNextId(currentId);
        String id = cIdtxt.getText();
        String name = cNametxt.getText();
        String address = Caddresstxt.getText();
        String pname = cmbParty.getValue();
        String distric = cmbDistric.getValue();
        String territorry = territorryTxt.getText();

        String pid = PartyRepo.getpartyId(pname);
        if (isValied()) {
            try {
                if (distric != null && territorry != null) {

                    Area area = new Area(nextId, distric, territorry);
                    boolean isSaved = AreaRepo.saveData(area);
                    if (isSaved) {
                        Candidate candidate = new Candidate(id, name, address, pid, nextId, status);
                        boolean isSaved2 = CandidateRepo.saveData(candidate);
                        if (isSaved2) {
                            loadTable();
                            clearField();
                            new Alert(Alert.AlertType.CONFIRMATION, "all data saved").show();
                        } else {
                            new Alert(Alert.AlertType.ERROR, "Information wrong").show();
                        }
                    } else {
                        new Alert(Alert.AlertType.ERROR, "incorrect data !!!").show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "please fill position detail").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }else {
            new Alert(Alert.AlertType.ERROR,"please input right data").show();
        }
    }


    private String getNextId(String currentId) {
        if(currentId != null) {
            //String[] split = currentId.split("O");  //" ", "2"
            //int idNum = Integer.parseInt(split[1]);
            int idNum = Integer.parseInt(currentId);
            return String.valueOf(++idNum);
        }
        return "1";
    }



    public void backtodashboard(ActionEvent actionEvent) throws IOException {
        AnchorPane rootNode = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) territorryTxt.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Dashboard Form");
    }

    public void updateOnAction(ActionEvent actionEvent) throws SQLException {
        if (isValied()) {
            try {
                String upid = cIdtxt.getText();
                String upcname = cNametxt.getText();
                String upaddress = Caddresstxt.getText();
                String pname = cmbParty.getValue();
                String pid = PartyRepo.getpartyId(pname);
                if (pname != null) {
                    boolean isSaved = CandidateRepo.update(upid, upcname, upaddress, pid);
                    if (isSaved) {
                        loadTable();
                        new Alert(Alert.AlertType.CONFIRMATION, "candidate updated").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "wrong data").show();
                    }
                } else {
                    new Alert(Alert.AlertType.ERROR, "please select party").show();
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
            }
        }else {
            new Alert(Alert.AlertType.ERROR,"please input right data").show();
        }
    }

    public void deleteOnAction(ActionEvent actionEvent) throws SQLException {
        String deCid = cIdtxt.getText();
try {
    boolean isdeleted = CandidateRepo.delete(deCid);

    if (isdeleted) {
        loadTable();
        new Alert(Alert.AlertType.CONFIRMATION, "party deleted").show();
    } else {
        new Alert(Alert.AlertType.ERROR, "party not deleted").show();
    }
}catch (Exception e){
    new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
}
    }

    public void clearOnAction(ActionEvent actionEvent) {
        clearField();
    }

    private void clearField() {
        cIdtxt.setText("");

        cNametxt.setText("");
        cmbDistric.setValue("");
        Caddresstxt.setText("");
        territorryTxt.setText("");
    }

    public void searchOnAction(ActionEvent actionEvent) throws SQLException {
        String searchingid = cIdtxt.getText();
        Candidate candidate = CandidateRepo.searchData(searchingid);

        if(candidate != null){
            String partyname = PartyRepo.getPartyName(candidate.getPid());

            cmbParty.setValue(partyname);
            cNametxt.setText(candidate.getCname());
            Caddresstxt.setText(candidate.getAddress());

        }
    }

    public void loadTablevaluesOnCmbAction(ActionEvent actionEvent) throws SQLException {
        loadTable();
    }

    public boolean isValied(){
        if(!Regex.setTextColor(lk.ijse.Utill.TextField.NIC,cIdtxt))return false;
        if(!Regex.setTextColor(lk.ijse.Utill.TextField.FULLNAME,cNametxt))return false;
        if(!Regex.setTextColor(lk.ijse.Utill.TextField.NAME,Caddresstxt))return false;
        if(!Regex.setTextColor(lk.ijse.Utill.TextField.NAME,territorryTxt))return false;
    return true;
    }

    public void cidOnKeyReleasedAction(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.NIC,cIdtxt);
    }

    public void nameOnKeyReleasedAction(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.FULLNAME,cNametxt);
    }

    public void addressOnKeyReleasedAction(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.NAME,Caddresstxt);
    }

    public void divisionOnKeyReleasedAction(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.NAME,territorryTxt);
    }
}
