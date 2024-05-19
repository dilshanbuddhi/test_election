package lk.ijse.repo;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Voter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VoterRepo {
    public static boolean save(Voter voter) throws SQLException {
    String sql = "insert into voter values(?,?,?,?,?,?,?)";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1, voter.getId());
        pstm.setObject(2, voter.getName());
        pstm.setObject(3, voter.getAge());
        pstm.setObject(4, voter.getDOB());
        pstm.setObject(5, voter.getAddress());
        pstm.setString(6,"active");
        pstm.setObject(7,voter.getGmail());


        return pstm.executeUpdate() > 0;
    }

    public static Voter SearchData(String id) throws SQLException {
        String sql = "SELECT * from voter where v_id = ?";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1,id);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            String v_id = resultSet.getString(1);
            String v_name = resultSet.getString(2);
            String v_age = resultSet.getString(3);
            String v_dob = resultSet.getString(4);
            String addressv= resultSet.getString(5);
            String gmail = resultSet.getString(7);

            Voter voter = new Voter(v_id, v_name, v_age, v_dob, addressv,gmail);
            return voter;
        }
        return null;
    }

    public static String SearchActive(String id) throws SQLException {
        String sql = "SELECT * from voter where v_id = ?";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1,id);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){

            String status= resultSet.getString(6);

            return status;
        }
        return "no";
    }

    public static boolean Update(Voter voter) throws SQLException {
        String sql = "UPDATE voter\n" +
                "SET name = ?, age = ?, DOB = ?, address = ?, gmail = ?\n" +
                "WHERE v_id = ? ";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,voter.getName());
        pstm.setObject(2,voter.getAge());
        pstm.setObject(3,voter.getDOB());
        pstm.setObject(4,voter.getAddress());
        pstm.setObject(5,voter.getGmail());
        pstm.setObject(6,voter.getId());

        return pstm.executeUpdate() > 0;
    }

    public static boolean Delete(String id) throws SQLException {
        String sql = "update voter set status = 'deactivated' where v_id = ?;";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,id);

        return pstm.executeUpdate() > 0;
    }


    public static boolean regiVoter(Voter voter, String eid) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        try {
            boolean isSavedVoter = VoterRepo.save(voter);
            if(isSavedVoter){
                boolean isSaved2=Election_voter_detailRepo.fillAssociate(voter.getId(),eid);
                if(isSaved2){
                    connection.commit();
                    return true;
                }
            }
            connection.rollback();
            return false;
        }catch (Exception e){
            connection.rollback();
            return false;
        }finally {
            connection.setAutoCommit(true);

        }
    }


    public static boolean deleteTables(String id) throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);

           try{
            boolean IsSaved = VoterRepo.Delete(id);
            if (IsSaved){
                boolean isSaved2 = Election_voter_detailRepo.updateAsc(id);
                if (isSaved2) {
                    connection.commit();
                    return true;
                }
                }
                connection.rollback();
                return false;
        }catch (SQLException e){
               connection.rollback();
               return false;
        }finally {
               connection.setAutoCommit(true);
           }
    }

    public static int getVoterCount() throws SQLException {
        String sql ="select count(*) from voter where status = 'active'";
        Connection connection =DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        if(resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }

    public static List<Voter> getAllData() throws SQLException {
        String sql = "select * from voter where status = 'active' ";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();
        List<Voter> voterList = new ArrayList<>();
        while (resultSet.next()){
            String id = resultSet.getString(1);
            String name = resultSet.getString(2);
            String age = resultSet.getString(3);
            String dob = resultSet.getString(4);
            String address = resultSet.getString(5);
            String gmail = resultSet.getString(7);

            Voter voter = new Voter(id, name, age, dob, address,gmail);
            voterList.add(voter);
        }
        return voterList;
    }
}
