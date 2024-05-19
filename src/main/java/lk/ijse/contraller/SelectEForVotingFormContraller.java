package lk.ijse.contraller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lk.ijse.model.Party;
import lk.ijse.model.tm.Partytm;
import lk.ijse.repo.ElectionRepo;
import lk.ijse.repo.Election_party_detailRepo;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static lk.ijse.repo.PartyRepo.getData;

public class SelectEForVotingFormContraller {

    public TableColumn<?,?> partyIDtbl;
    public TableColumn<?,?> partyNametbl;
    public ComboBox <String> electioncmb;
    public TableColumn<?,?> partyLeadertbl;
    public TableView <Partytm> tableParty;
    public Text nametxt;
    public Text enametxt;


    public void initialize(){
        setCmbElection();
        setCellValueFactory();
    }


    private void setCellValueFactory() {
        partyIDtbl.setCellValueFactory(new PropertyValueFactory<>("pid"));
        partyNametbl.setCellValueFactory(new PropertyValueFactory<>("pname"));
        partyLeadertbl.setCellValueFactory(new PropertyValueFactory<>("pLeader"));
    }

    private void setCmbElection() {
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

    public void loadTableAsElection(ActionEvent actionEvent) throws SQLException {
            String ename = electioncmb.getValue();
            enametxt.setText(ename);
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
      tableParty.setItems(obListP);
        System.out.println(obListParty);
    }

    public void startVotingOnAction(ActionEvent actionEvent) throws SQLException, IOException {
        try {
            String electionName = electioncmb.getValue();
            if (electionName != null) {
                String eid = ElectionRepo.getID(electionName);

                //VotingFormContraller votingFormContraller = new VotingFormContraller(eid);
                VotingFormContraller.setEid(eid);
                gotoVotingForm();
            } else {
                new Alert(Alert.AlertType.ERROR, "please select election !!").show();
            }
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    private void gotoVotingForm() throws IOException {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Voting_form.fxml"));
            AnchorPane rootNode = loader.load();

            Scene scene = new Scene(rootNode);

            Stage stage = (Stage) nametxt.getScene().getWindow();
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setTitle("Voter Form");
        }


    public void backToDashboard(ActionEvent actionEvent) throws IOException {
        AnchorPane rootNode = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) electioncmb.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Dashboard Form");
    }
}
