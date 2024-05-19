package lk.ijse.contraller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import javafx.scene.chart.PieChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lk.ijse.Utill.Regex;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Candidate;
import lk.ijse.model.Result;
import lk.ijse.model.tm.ResultTm;
import lk.ijse.repo.CandidateRepo;
import lk.ijse.repo.PartyRepo;
import lk.ijse.repo.ResultRepo;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.awt.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class ResultFormContraller {
    private final Set<Color> usedColors = new HashSet<>();

    public TableView <ResultTm> resulttbl;
    public TableColumn <?, ?>candidateIDlbl;
    public TextField searchEidtxt;
    public TableColumn<?, ?> partyNamelbl;
    public TableColumn<?, ?> votrcountLbl;
    public Text winnerNametxt;
    public Text winnerPartyTxt;
    public Text voteCountTxt;
    public PieChart pieChartId;
    public VBox legendBox;

    public void initialize() throws SQLException {
        setcellvalues();
    }

    public void setPieChart() throws SQLException {
        String id = searchEidtxt.getText();

        ObservableList<PieChart.Data> pieChartData = ResultRepo.getResult(id);

        pieChartId.setData(pieChartData);
        pieChartId.setTitle("Vote Counts");


        legendBox.getChildren().clear();

        for (PieChart.Data data : pieChartData) {
            String c_name = CandidateRepo.getCandidateName(data.getName());
            System.out.println("Candidate Name: " + c_name);

            Color color = getRandomColor();
            applyColorToData(data, color);
            addLegendItem(c_name, color);
        }
    }

    private Color getRandomColor() {
        Random rand = new Random();
        Color randomColor;
        do {
            randomColor = Color.rgb(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256));
        } while (usedColors.contains(randomColor));
        usedColors.add(randomColor);
        return randomColor;
    }

    private void applyColorToData(PieChart.Data data, Color color) {
        Node node = data.getNode();
        String rgb = String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
        node.setStyle("-fx-pie-color: " + rgb + ";");
    }

    private void addLegendItem(String c_id, Color color) {
        HBox legendItem = new HBox(10);
        Rectangle colorBox = new Rectangle(15, 15, color);
        Label label = new Label(c_id);
        legendItem.getChildren().addAll(colorBox, label);
        legendBox.getChildren().add(legendItem);
    }




    private void setcellvalues() {
        partyNamelbl.setCellValueFactory(new PropertyValueFactory<>("pname"));
        candidateIDlbl.setCellValueFactory(new PropertyValueFactory<>("cname"));
        votrcountLbl.setCellValueFactory(new PropertyValueFactory<>("vcount"));
    }

    public void searchOnAction(ActionEvent actionEvent) throws SQLException, JRException {

            setPieChart();

            loadResulttable();
            loadFirstRowResult();
    }

    public void setTxt(String c_name, String pname, String votecount){
        winnerNametxt.setText("Congratulations  :  "+c_name);
        winnerPartyTxt.setText("Winning Party : \""+pname+"\"....");
        voteCountTxt.setText(votecount);
    }

    public void loadResulttable(){
        String eid = searchEidtxt.getText();
        ObservableList<ResultTm> rList = FXCollections.observableArrayList();
        try {
            List<Result> resultList = ResultRepo.getAll(eid);
            for (Result result : resultList) {
                Candidate candidate = CandidateRepo.searchData(result.getCid());
                String c_name = candidate.getCname();
                String pname = PartyRepo.getPartyName(candidate.getPid());
                String votecount = result.getVotecount();
                ResultTm resultTm = new ResultTm(pname, c_name, votecount);
                System.out.println(resultTm);
                rList.add(resultTm);
            }
            resulttbl.setItems(rList);
        }catch (Exception e){
            System.out.println(">>>>>"+e.getMessage());
        }
    }

    public void loadFirstRowResult() {
        String eid = searchEidtxt.getText();
        try {
            List<Result> resultList = ResultRepo.getAll(eid);
            if (!resultList.isEmpty()) {
                Result result = resultList.get(0);
                Candidate candidate = CandidateRepo.searchData(result.getCid());
                String c_name = candidate.getCname();
                String pname = PartyRepo.getPartyName(candidate.getPid());
                String votecount = result.getVotecount();
                setTxt(c_name,pname,votecount);
            }
        } catch (Exception e){
            System.out.println(">>>>>"+e.getMessage());
        }
    }


    public void backOnAction(ActionEvent actionEvent) throws IOException {
        AnchorPane rootNode = FXMLLoader.load(getClass().getResource("/view/dashboard_form.fxml"));

        Scene scene = new Scene(rootNode);

        Stage stage = (Stage) winnerPartyTxt.getScene().getWindow();
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.setTitle("Dashboard Form");
    }

    public void eidOnKeyReleasedAction(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.Utill.TextField.EID,searchEidtxt);
    }

    public void genarateReportOnAntion(ActionEvent actionEvent) throws JRException, SQLException {
        JasperDesign jasperDesign = JRXmlLoader.load("src/main/resources/Report/resultElection.jrxml");
        JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);
        Map<String,Object> dataa = new HashMap<>();
        dataa.put("electionId",searchEidtxt.getText());


        JasperPrint jasperPrint =
                JasperFillManager.fillReport(jasperReport, dataa, DbConnection.getInstance().getConnection());
        JasperViewer.viewReport(jasperPrint,false);
    }
}
