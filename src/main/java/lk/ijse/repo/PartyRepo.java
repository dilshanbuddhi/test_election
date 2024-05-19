package lk.ijse.repo;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Party;
import lk.ijse.model.tm.Pidtm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PartyRepo {
    public static String getCurrentId() throws SQLException {
        String sql = "SELECT CONCAT('P', MAX(CAST(SUBSTRING(p_id, 2) AS UNSIGNED))) AS max_p_id FROM party";
        PreparedStatement pstm = DbConnection.getInstance().getConnection()
                .prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();
        if(resultSet.next()) {
            String pid = resultSet.getString(1);
            return pid;
        }
        return null;
    }

    public static boolean saveData(Party party) throws SQLException {
        String sql = "INSERT INTO Party (p_id, p_name, p_leader, status) VALUES (?, ?, ?, ?)";
        Connection connection = DbConnection.getInstance().getConnection();
        try (PreparedStatement pstm = connection.prepareStatement(sql)) {
            pstm.setString(1, party.getPid());
            pstm.setString(2, party.getPName());
            pstm.setString(3, party.getPLeader());
            pstm.setString(4,"active");

            return pstm.executeUpdate() > 0;
        }
    }


    public static Party SearchpData(String pid) throws SQLException {
        String sql = "SELECT p_id, p_name, p_leader from party where p_id = ?";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1,pid);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            String p_id = resultSet.getString(1);
            String p_name = resultSet.getString(2);
            String p_leader = resultSet.getString(3);


            Party party = new Party(p_id, p_name, p_leader);
            return party;
        }
        return null;
    }

    public static List<String> getParty() throws SQLException {
        String sql = "SELECT p_name FROM party";
        ResultSet resultSet = DbConnection.getInstance()
                .getConnection()
                .prepareStatement(sql)
                .executeQuery();

        List<String> codeList = new ArrayList<>();
        while (resultSet.next()) {
            codeList.add(resultSet.getString(1));
        }
        return codeList;
    }

    public static String getpartyId(String pname) throws SQLException {
        String sql = "select p_id from party where p_name = ? ";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,pname);

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()) {
            String pid = resultSet.getString(1);
         return pid;
        }
        return null;
    }

    public static boolean regiParty(Party party, String eid) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

        try {
            boolean isSaved = PartyRepo.saveData(party);
            if (isSaved) {
                boolean isSaved2 = Election_party_detailRepo.fillAssociate(party.getPid(), eid);
                if (isSaved2) {
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
            return false;
        } catch (Exception e) {
            connection.rollback();
            return false;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public static boolean update(Party party) throws SQLException {
        String sql ="UPDATE party\n" +
                "SET p_name = ?, p_leader = ?\n" +
                "WHERE p_id = ?;\n";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,party.getPName());
        pstm.setObject(2,party.getPLeader());
        pstm.setObject(3,party.getPid());

        return pstm.executeUpdate()>0;
    }

    public static boolean deleteParty(String deleteId) throws SQLException {
        String sql = "update party set status = 'deactivated' where p_id = ?";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,deleteId);

        return pstm.executeUpdate()>0;
    }

    public static String getPartyName(String pid) throws SQLException {
        String sql = "select p_name from party where p_id = ? ";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,pid);

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()) {
            String pname= resultSet.getString(1);
            return pname;
        }
        return null;
    }

    public static Party getData(String pids) throws SQLException {
        String sql = "select * from party where p_id = ? and status = 'active'; ";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setObject(1, pids);

        ResultSet resultSet = pstm.executeQuery();

        Party party = null;
        while (resultSet.next()) {
            String id = resultSet.getString(1);
            String name = resultSet.getString(2);
            String leader = resultSet.getString(3);

            party = new Party(id, name, leader);
        }
        return party;

    }


    public static List<Pidtm> getAllPartyid() throws SQLException {
        String sql = "select p_id from party where status = 'active'";
        List<Pidtm> codeList = new ArrayList<>();
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
         ResultSet resultSet =pstm.executeQuery();

         while (resultSet.next()){
             String id = resultSet.getString(1);
             Pidtm pidtm = new Pidtm(id);
             codeList.add(pidtm);
         }
         return codeList;
    }
}
