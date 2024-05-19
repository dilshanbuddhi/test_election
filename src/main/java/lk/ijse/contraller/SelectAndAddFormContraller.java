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
import lk.ijse.model.Election_voter_detail;
import lk.ijse.model.Voter;
import lk.ijse.model.tm.AlredyAddedIds;
import lk.ijse.model.tm.VoterTm;
import lk.ijse.repo.ElectionRepo;
import lk.ijse.repo.Election_voter_detailRepo;
import lk.ijse.repo.VoterRepo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SelectAndAddFormContraller {

    public TextField idSearchtxt;
    public ComboBox<String> cmbElection;
    public Label searchingname;
    public Label searchingage;
    public Label searchingaddress;
    public Label searchingDOB;
    public TableView <VoterTm>votertable;
    public TableColumn<?,?> idclm;
    public TableColumn <?,?>nameclm;
    public TableColumn <?,?>ageclm;
    public TableColumn <?,?>dobclm;
    public TableColumn <?,?>addressclm;
    public TableView <AlredyAddedIds>addedidtable;
    public TableColumn <?,?> idcolumn;
    public AnchorPane anchorepane;


    public void initialize() throws SQLException {
        System.out.println(cmbElection.getValue());
        setElectionCombo();
        setCellValueFactory();
        setCellValueFactory2();
        loadtbl();
    }
    public void getVIdlistAsElection() throws SQLException {
        String ename = cmbElection.getValue();
        String eid = ElectionRepo.getID(ename);
        List <String> vidlist = Election_voter_detailRepo.getVlist(eid);
        loadtableAselection(vidlist);
    }

    private void loadtableAselection(List<String> vidlist) throws SQLException {
        ObservableList<AlredyAddedIds> obList = FXCollections.observableArrayList();
        List<Voter>voterdetail =new ArrayList<>();
        for (String vid : vidlist){
            Voter voter = VoterRepo.SearchData(vid);
            voterdetail.add(voter);
        }

        for (Voter voter : voterdetail){
            AlredyAddedIds alredyAddedIds = new AlredyAddedIds(voter.getId());
            obList.add(alredyAddedIds);
        }
         addedidtable.setItems(obList);
    }

    private void setCellValueFactory2() {
        idcolumn.setCellValueFactory(new PropertyValueFactory<>("id"));
    }
    private void setCellValueFactory() {
        idclm.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameclm.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageclm.setCellValueFactory(new PropertyValueFactory<>("age"));
        dobclm.setCellValueFactory(new PropertyValueFactory<>("dob"));
        addressclm.setCellValueFactory(new PropertyValueFactory<>("address"));
    }

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

            obList.addAll(codeList);
            cmbElection.setItems(obList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void SearchabdDelectOnEnterButtun() throws SQLException {
        try {
            String eid = idSearchtxt.getText();
            Voter voter = VoterRepo.SearchData(eid);
            if (voter != null) {
                searchingname.setText(voter.getName());
                searchingage.setText(voter.getAge());
                searchingaddress.setText(voter.getAddress());
                searchingDOB.setText(voter.getDOB());
            } else {
                new Alert(Alert.AlertType.ERROR, "Voter not found").show();
            }
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }
    public void SearchOnButtonAction(ActionEvent actionEvent) throws SQLException {
        SearchabdDelectOnEnterButtun();
    }


    public void backOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/voter_form.fxml"));
        Parent rootNode = loader.load();
        anchorepane.getChildren().clear();
        anchorepane.getChildren().add(rootNode);
    }


    public void AcVoterAddonAction(ActionEvent actionEvent) throws SQLException {
        try {
            String electionName = cmbElection.getValue();
            String vid = idSearchtxt.getText();

            String eid = ElectionRepo.getID(electionName);
            Election_voter_detail vdtail = Election_voter_detailRepo.searchDuplicate(vid,eid);
            if (vdtail == null) {
                boolean issaved2 = Election_voter_detailRepo.fillAssociate(vid, eid);
                if (issaved2) {
                    getVIdlistAsElection();
                    new Alert(Alert.AlertType.INFORMATION, "all data detail saved").show();
                }
            } else {
                new Alert(Alert.AlertType.ERROR, "Already added").show();
            }
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void electioncmbOnAction(ActionEvent actionEvent) throws SQLException {
        getVIdlistAsElection();
    }

    public void vidOnKeyrelesed(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.NIC,idSearchtxt);
    }
}
