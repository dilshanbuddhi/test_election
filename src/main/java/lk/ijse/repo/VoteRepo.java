package lk.ijse.repo;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Vote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class VoteRepo {

    public static Vote getdata(String vId, String electionid) throws SQLException {
        String sql = "SELECT * FROM vote WHERE v_id = ? AND e_id = ?";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,vId);
        pstm.setObject(2,electionid);

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            String v_id = resultSet.getString(1);
            String e_id = resultSet.getString(2);

            Vote vote = new Vote(v_id, e_id);
            return vote;
        }
        return null;
    }

    public static boolean savedata(Vote vote1) throws SQLException {
        String sql = "INSERT INTO vote (v_id, e_id) VALUES (?, ?)";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,vote1.getVID());
        pstm.setObject(2,vote1.getEid());

        return pstm.executeUpdate()>0;
    }
}
