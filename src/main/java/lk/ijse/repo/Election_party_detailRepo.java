package lk.ijse.repo;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Election_party_detail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Election_party_detailRepo {
    public static boolean fillAssociate(String pid, String eid) throws SQLException {
            String sql = "INSERT INTO Election_party_detail (p_id, e_id) VALUES (?, ?)";
            Connection connection = DbConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement(sql) ;
            pstm.setString(1, pid);
            pstm.setString(2, eid);

            return pstm.executeUpdate() > 0;

    }

    public static List<String> getpartyID(String eid) throws SQLException {
            String sql = "select p_id from election_party_detail where e_id = ?";
            Connection connection = DbConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement(sql);
            pstm.setObject(1,eid);
            ResultSet resultSet = pstm.executeQuery();
            List<String> pidList = new ArrayList<>();
            while (resultSet.next()) {
                pidList.add(resultSet.getString(1));
            }
            return pidList;


    }

    public static boolean checkalreadyAdded(String eid, String pid) throws SQLException {
        String sql = "select * from election_party_detail where p_id =? and e_id = ?";

        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setObject(1,pid);
        pstm.setObject(2,eid);

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            return false;
        }
        return true;
    }

    public static boolean deleteParty(String deleteId) throws SQLException {
        String sql = "delete from election_party_detail where p_id = ?";

        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setObject(1,deleteId);

        return pstm.executeUpdate() > 0;
    }

    public static Election_party_detail ifhavedata(String id) throws SQLException {
        String sql =" select * from election_party_detail where e_id = ? limit 1";

        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,id);

        ResultSet resultSet = pstm.executeQuery();

        if (resultSet.next()){
            String pid = resultSet.getString(1);
            String eid = resultSet.getString(2);
            Election_party_detail electionPartyDetail = new Election_party_detail(pid, eid);
        return electionPartyDetail;
        }
        return null;
    }

    public static boolean deleteElection(String id) throws SQLException {
        String sql = "delete from election_party_detail where e_id = ?";

        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setObject(1,id);

        return pstm.executeUpdate() > 0;
    }
}
