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
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lk.ijse.Utill.Regex;
import lk.ijse.model.Party;
import lk.ijse.model.tm.Partytm;
import lk.ijse.model.tm.Pidtm;
import lk.ijse.repo.ElectionRepo;
import lk.ijse.repo.Election_party_detailRepo;
import lk.ijse.repo.PartyRepo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static lk.ijse.repo.PartyRepo.getData;

public class SelectAndAddPartyformcontraller {

    public TextField partyIdtxt;
    public Text partynametxt;
    public Text partyLeadertxt;
    public ComboBox <String> electioncmb;
    public TableView <Pidtm>patryIdlistTable;
    public TableView<Partytm> partyListTbl;
    public TableColumn<?,?> partyIdClm;
    public TableColumn <?,?>pNameClm;
    public TableColumn<?,?> pLeaderClm;
    public TableColumn <?,?>pIdlistclm;
    public AnchorPane anchorepane;

    public void initialize() throws SQLException {
        setCellValueFactory1();
        loadIdtable();
        setCombo();

    }
 public void loadIdtable() throws SQLException {
     ObservableList<Pidtm> olist = FXCollections.observableArrayList();
     List<Pidtm> partyids = PartyRepo.getAllPartyid();
     for (Pidtm pidtm : partyids){
         Pidtm pidtm1 = new Pidtm(pidtm.getPartyId());
         olist.add(pidtm1);
     }
     patryIdlistTable.setItems(olist);
 }

    private void setCellValueFactory() {
        partyIdClm.setCellValueFactory(new PropertyValueFactory<>("pid"));
        pNameClm.setCellValueFactory(new PropertyValueFactory<>("pname"));
        pLeaderClm.setCellValueFactory(new PropertyValueFactory<>("pLeader"));
    }

    private void setCellValueFactory1() {
        pIdlistclm.setCellValueFactory(new PropertyValueFactory<>("partyId"));
    }

    public void loadTableAsElection(ActionEvent actionEvent) throws SQLException {
        String ename = electioncmb.getValue();
        String eid = ElectionRepo.getID(ename);
        ObservableList<String> obList = FXCollections.observableArrayList();

        List<String> pidList = Election_party_detailRepo.getpartyID(eid);

        for (String pids : pidList) {

            obList.add(pids);
        }
        //System.out.println(obList);
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
            //System.out.println(partytm);
        }
        partyListTbl.setItems(obListP);
        System.out.println(obListParty);
    }


    private void setCombo() {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {
            List<String> codeList = ElectionRepo.getElection();

            for (String code : codeList) {
                obList.add(code);
            }
            electioncmb.setItems(obList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void searchOnEnterButtonAction(ActionEvent actionEvent) throws SQLException {
        String pid = partyIdtxt.getText();
        Party party = PartyRepo.SearchpData(pid);
    try {
        if (party != null) {
            partyIdtxt.setText(party.getPid());
            partynametxt.setText(party.getPName());
            partyLeadertxt.setText(party.getPLeader());
        } else {
            new Alert(Alert.AlertType.ERROR, "cant find this party !!!!").show();
        }
    }catch (Exception e){
        new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
    }
    }

    public void addOnAction(ActionEvent actionEvent) throws SQLException {
        try {
            String pid = partyIdtxt.getText();
            String electionName = electioncmb.getValue();
            String eid = ElectionRepo.getID(electionName);
            if (electionName != null) {
                if(Election_party_detailRepo.checkalreadyAdded(eid,pid)){
                    boolean issaved = Election_party_detailRepo.fillAssociate(pid, eid);
                    if (issaved) {
                        initialize();
                        new Alert(Alert.AlertType.CONFIRMATION, "party detail Saved").show();
                    } else {
                        new Alert(Alert.AlertType.ERROR, "party detail not saved").show();
                    }
                }else {
                    new Alert(Alert.AlertType.ERROR,"you alredy added this party").show();
                }

            } else {
                new Alert(Alert.AlertType.ERROR, "please Select Election ").show();
            }
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void backTopartyFormOnAction(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/party_form.fxml"));
        Parent rootNode = loader.load();
        anchorepane.getChildren().clear();
        anchorepane.getChildren().add(rootNode);
    }

    public void loadtbleOncmbAction(ActionEvent actionEvent) throws SQLException {
        loadtable();
    }
    public void loadtable() throws SQLException {
        setCellValueFactory();
        loadTableAsElection(new ActionEvent());
    }

    public void pidOnKeyReleasedAction(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.PID,partyIdtxt);
    }
}
