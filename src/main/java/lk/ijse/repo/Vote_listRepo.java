package lk.ijse.repo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lk.ijse.db.DbConnection;
import lk.ijse.model.Result;
import lk.ijse.model.Vote_list;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Vote_listRepo {

    public static boolean saveVote(Vote_list voteList) throws SQLException {
        String sql = "insert into vote_list values (?,?,?)";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,voteList.getP_id());
        pstm.setObject(2,voteList.getC_id());
        pstm.setObject(3,voteList.getE_id());

        return pstm.executeUpdate()>0;
    }



        public static ObservableList<Result> getCount(String electionid) throws SQLException {
            String sql = "SELECT c_id, COUNT(*) AS count FROM vote_list WHERE e_id = ? GROUP BY c_id ORDER BY count DESC";
            ObservableList<Result> resultList = FXCollections.observableArrayList();

            Connection connection = DbConnection.getInstance().getConnection();
                 PreparedStatement pstm = connection.prepareStatement(sql);

                pstm.setString(1, electionid);
                ResultSet resultSet = pstm.executeQuery();
                    while (resultSet.next()) {
                        String cid = resultSet.getString(1);
                        String votecount = resultSet.getString(2);
                        Result result = new Result(electionid,votecount, cid );
                        resultList.add(result);
                    }

            return resultList;
        }
    }


