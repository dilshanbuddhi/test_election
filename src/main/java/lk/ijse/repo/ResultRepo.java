package lk.ijse.repo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Result;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultRepo {
    public static boolean saveData(Result result1) throws SQLException {
        String sql = "insert into result values (?,?,?)";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1, result1.getEid());
        pstm.setObject(2, result1.getVotecount());
        pstm.setObject(3, result1.getCid());

        return pstm.executeUpdate() > 0;
    }

    public static List<Result> getAll(String eid) throws SQLException {
        String sql = " SELECT * FROM result WHERE e_id = ? ORDER BY vote_count DESC ";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1, eid);

        ResultSet resultSet = pstm.executeQuery();
        List<Result> rList = new ArrayList<>();

        while (resultSet.next()) {
            String electionid = resultSet.getString(1);
            String votecount = resultSet.getString(2);
            String cid = resultSet.getString(3);
            Result result = new Result(electionid, votecount, cid);
            rList.add(result);
        }
        return rList;
    }

    public static ObservableList<PieChart.Data> getResult(String id) throws SQLException {

        DbConnection dbConnection = DbConnection.getInstance();
        Connection connection = dbConnection.getConnection();

        String query = "SELECT c_id, SUM(vote_count) AS total_votes FROM result WHERE e_id = ? GROUP BY c_id";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setObject(1,id);
        ResultSet resultSet = statement.executeQuery();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        while (resultSet.next()) {
            String c_id = resultSet.getString("c_id");
            int totalVotes = resultSet.getInt("total_votes");
            pieChartData.add(new PieChart.Data(c_id, totalVotes));
        }

        return pieChartData;
    }
}
