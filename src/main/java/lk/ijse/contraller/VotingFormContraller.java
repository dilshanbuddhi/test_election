package lk.ijse.contraller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lk.ijse.Utill.Regex;
import lk.ijse.db.DbConnection;
import lk.ijse.model.*;
import lk.ijse.repo.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;


public class VotingFormContraller {

    public TextField voteridtxt;

    public Text electionNametxt;
    public ComboBox<String> partycmb;
    public ComboBox <String> candidatecmb;

    static String electionid ;
    public Text nametxt;
    public Text agetxt;
    public Text addresstxt;
    public Text partytxt2;
    public Text vidtxt2;
    public Text cnametxt2;
    public Text choicetxt;
    public Text enametxt2;


    public static void setEid(String eid) {
        electionid = eid;
        System.out.println(electionid);
        printdata();
    }

    private static void printdata() {
        System.out.println(electionid+"meka awe static eken");
    }

    public void initialize() throws SQLException {
        String ename =ElectionRepo.getElectionName(electionid);
        enametxt2.setText("**"+ename+"..");
        //getCmbPartyList();
        electionNametxt.setText(ElectionRepo.getElectionName(electionid));
    }

    private void getCmbPartyList() throws SQLException {
        ObservableList<String> obList = FXCollections.observableArrayList();
        try {

            List<String> pidList = Election_party_detailRepo.getpartyID(electionid);

            for (String pids : pidList) {

                obList.add(pids);
            }
            setPartyCmb(obList);
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    private void setPartyCmb(ObservableList<String> obList) throws SQLException {
        ObservableList<String> partyNameList = FXCollections.observableArrayList();
        //List<String> partyNameList = new ArrayList<>();
        try {
            for (String partyid : obList) {
                String name = PartyRepo.getPartyName(partyid);
                partyNameList.add(name);
            }
            partycmb.setItems(partyNameList);
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    /*public VotingFormContraller(String eid) {
        System.out.println(eid);
    }*/

    public void votingOnAction(ActionEvent actionEvent) throws SQLException {

        Connection connection = DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        try{
            String voteid = voteridtxt.getText();
            String active = VoterRepo.SearchActive(voteid);
            Election_voter_detail vdtail = Election_voter_detailRepo.searchDuplicate(voteid,electionid);

            if (vdtail != null){
                Vote vote = VoteRepo.getdata(voteid,electionid);

                if (vote == null){
                    String pname = partycmb.getValue();
                    String cname = candidatecmb.getValue();
                    if (pname != null & cname != null ){
                        String pid = PartyRepo.getpartyId(pname);
                        String cid = CandidateRepo.getcandidateId(cname);
                        Vote_list voteList = new Vote_list(pid, cid,electionid);
                        boolean isvoted = Vote_listRepo.saveVote(voteList);
                        if (isvoted){
                            Vote vote1 = new Vote(voteid, electionid);
                            boolean voted = VoteRepo.savedata(vote1);
                            if (voted){
                                clearField();
                                connection.commit();
                                new Alert(Alert.AlertType.CONFIRMATION,"CONGATULATIONS !,,vote successfully").show();
                            }else {
                                connection.rollback();
                            }

                        }else {
                            connection.rollback();
                            new Alert(Alert.AlertType.ERROR,"vote failed").show();
                            clearField();
                        }
                    }else{
                        new Alert(Alert.AlertType.ERROR,"You cant vote this election").show();
                        clearField();
                    }
                    }else{
                    new Alert(Alert.AlertType.ERROR,"please select your choice").show();
                }

            }else {
                new Alert(Alert.AlertType.ERROR,"you are not eligible voter go home").show();
                clearField();
            }
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }finally {
            connection.setAutoCommit(true);
        }

    }

    private void clearField() {
        voteridtxt.setText(null);
        candidatecmb.setValue(null);
        partycmb.setValue(null);
        partytxt2.setText(null);
        vidtxt2.setText(null);
        cnametxt2.setText(null);
        choicetxt.setText(null);
        //enametxt2.setText(null);
        nametxt.setText(null);
        agetxt.setText(null);
        addresstxt.setText(null);

    }

    public void loadCandidatecmb(ActionEvent actionEvent) throws SQLException {
        try {
            String partyName = partycmb.getValue();

            choicetxt.setText("Your choice >>>>>>>>>>>");
            partytxt2.setText("//"+partyName);
            String pid = PartyRepo.getpartyId(partyName);

            ObservableList<String> obList = FXCollections.observableArrayList();
            List<String> candidateList = CandidateRepo.getCnameASparty(pid);

            for (String candidate : candidateList) {
                obList.add(candidate);
            }
            candidatecmb.setItems(obList);
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void searchOnAction(ActionEvent actionEvent) throws SQLException {
        try {
            String vId = voteridtxt.getText();
            Voter voter = VoterRepo.SearchData(vId);
            String active = VoterRepo.SearchActive(vId);

            if (active.equals("active")) {
                  Vote vote = VoteRepo.getdata(vId,electionid);

                if (vote == null) {
                       nametxt.setText(voter.getName());
                addresstxt.setText(voter.getAddress());
                agetxt.setText(voter.getAge());
                vidtxt2.setText("ID Num : "+vId);
                getCmbPartyList();
                new Alert(Alert.AlertType.CONFIRMATION, "you can vote").show();

                }else{
                    new Alert(Alert.AlertType.ERROR,"you already voted this election").show();
                    clearField();
                    voteridtxt.setText(null);
                }
               // nametxt.setText(voter.getName());
               // addresstxt.setText(voter.getAddress());
                //agetxt.setText(voter.getAge());
                //vidtxt2.setText(vId);

              //  new Alert(Alert.AlertType.CONFIRMATION, "you can vote").show();
            } else {
                new Alert(Alert.AlertType.ERROR, "you are not eligible voter go home").show();
                clearField();
            }
        }catch (Exception e){
            new Alert(Alert.AlertType.ERROR,e.getMessage()).show();
        }
    }

    public void EndOnAction(ActionEvent actionEvent) throws IOException, SQLException {
        ObservableList<Result> obList = FXCollections.observableArrayList();
        List<Result> resultList = Vote_listRepo.getCount(electionid);
        System.out.println(resultList);
         ElectionEndPassFormContraller.sendData(electionid, resultList);
         //ElectionEndPassFormContraller.sendData(electionid, resultList);
        checkLegal();

    }

    private void checkLegal() throws IOException {
        Parent rootNode = FXMLLoader.load(this.getClass().getResource("/view/electionEndPass_form.fxml"));

        Scene scene = new Scene(rootNode);
        Stage stage = new Stage();
        stage.setScene(scene);

        stage.setTitle("Registration Form");

        stage.show();
    }


    public void settxtonCmbAction(ActionEvent actionEvent) {
        String cname = candidatecmb.getValue();
        cnametxt2.setText("// "+cname);
    }

    public void NICOnReleasedAction(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.NIC,voteridtxt);
    }
}
