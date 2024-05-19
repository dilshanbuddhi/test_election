package lk.ijse.repo;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Election_voter_detail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Election_voter_detailRepo {


    public static boolean fillAssociate(String id, String eid) throws SQLException {
        String sql = "insert into voter_election_detail values (?,?,?)";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1, id);
        pstm.setObject(2, eid);
        pstm.setObject(3, "active");

        return pstm.executeUpdate() > 0;
    }

    public static boolean updateAsc(String id) throws SQLException {
        String sql = "UPDATE Voter_election_detail SET status = 'deactivated' WHERE v_id = ?";

        Connection connection = DbConnection.getInstance().getConnection();
             PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setObject(1, id);
            return pstm.executeUpdate() > 0;
        }

    public static List<String> getVlist(String eid) throws SQLException {
        String sql = "select v_id from voter_election_detail where e_id = ? and status = 'active'";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,eid);
        List<String> vidlist = new ArrayList<>();
        ResultSet resultSet = pstm.executeQuery();

        while(resultSet.next()){
            String vid = resultSet.getString(1);
            vidlist.add(vid);
        }
        return vidlist;
    }

    public static Election_voter_detail searchDuplicate(String vid, String eid) throws SQLException {
        String sql = "select * from  voter_election_detail where v_id = ? and e_id =?";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setObject(1,vid);
        pstm.setObject(2,eid);
        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()){
            String v_id = resultSet.getString(1);
            String e_id = resultSet.getString(2);
            String status = resultSet.getString(3);
            Election_voter_detail electionVoterDetail = new Election_voter_detail(v_id, e_id, status);
            return electionVoterDetail;
        }
        return null;

    }

    public static Election_voter_detail ifHavedata(String id) throws SQLException {
        String sql =" select * from voter_election_detail where e_id = ? limit 1";

        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,id);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()){
           String eid = resultSet.getString(1);
           String vid = resultSet.getString(2);
           String status = resultSet.getString(3);
            Election_voter_detail electionVoterDetail = new Election_voter_detail(eid, vid, status);
            return electionVoterDetail;
        }
        return null;

    }

    public static boolean deletedata(String id) throws SQLException {
        String sql = " update voter_election_detail set status = 'deactivated' where e_id = ?;";

        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,id);

        return pstm.executeUpdate()>0;
    }
}
