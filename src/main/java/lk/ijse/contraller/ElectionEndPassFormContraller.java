package lk.ijse.contraller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.Window;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Result;
import lk.ijse.repo.ElectionRepo;
import lk.ijse.repo.ResultRepo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static lk.ijse.contraller.VotingFormContraller.electionid;

public class ElectionEndPassFormContraller {

    private static String electionId;
    private static List<Result> resultList;

    public TextField idtxt;
    public Button confirmid;

    public static void sendData(String electionid, List<Result> resultList) {
       ElectionEndPassFormContraller.electionId = electionid;
      ElectionEndPassFormContraller.resultList = resultList;
    }

    public void confirmOnAction(ActionEvent actionEvent) throws SQLException, IOException {

    String pw = "1234";
    String password = idtxt.getText();
    if (password != null) {
        if (pw.equals(password)) {
            System.out.println(electionId);
            Connection connection = DbConnection.getInstance().getConnection();
            connection.setAutoCommit(false);
           try {

               boolean isdeleted = ElectionRepo.deleteElection(electionid);
               if (isdeleted){
               for (Result result : resultList) {
                   String eid = result.getEid();
                   String votecount = result.getVotecount();
                   String cid = result.getCid();

                   System.out.println(eid + votecount + cid);

                   Result result1 = new Result(eid, votecount, cid);

                   boolean isSaved = ResultRepo.saveData(result1);

                   if (isSaved) {
                       connection.commit();
                   } else {
                       connection.rollback();
                   }
               }
               }else {
                   connection.rollback();
               }
           }catch (Exception e){
               System.out.println(">>>>>"+e);
           }finally {
               connection.setAutoCommit(true);
            }

            navigateDashboard();

        } else {
            new Alert(Alert.AlertType.ERROR, "oops password is wrong !!!!").show();
        }
    } else {
        new Alert(Alert.AlertType.ERROR, "please insert password ").show();
    }

}


    private void navigateDashboard() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/dashboard_form.fxml"));
        Parent rootNode = loader.load();
        Scene scene = new Scene(rootNode);
        Stage dashboardStage = (Stage) confirmid.getScene().getWindow();

        if (dashboardStage == null) {
            dashboardStage = new Stage();
            dashboardStage.setScene(scene);
            dashboardStage.centerOnScreen();
            dashboardStage.setTitle("Dashboard Form");
        } else {
            dashboardStage.setScene(scene);
            dashboardStage.centerOnScreen();
        }

        for (Window window : Stage.getWindows()) {
            if ((window instanceof Stage) && (window != dashboardStage)) {
                ((Stage) window).close();
            }
        }

        dashboardStage.show();
    }
}
