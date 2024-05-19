package lk.ijse.repo;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Candidate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CandidateRepo {
    public static boolean saveData(Candidate candidate) throws SQLException {
        String sql = "insert into candidate values (?,?,?,?,?,?)";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1,candidate.getCid());
        pstm.setObject(2,candidate.getCname());
        pstm.setObject(3,candidate.getAddress());
        pstm.setObject(4,candidate.getPid());
        pstm.setObject(5,candidate.getAid());
        pstm.setObject(6,candidate.getStatus());

        return pstm.executeUpdate() > 0;
    }

    public static int getCandidateCount() throws SQLException {
        String sql = "select count(*) from candidate where status = 'active'";
        Connection connection =DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        ResultSet resultSet = pstm.executeQuery();

        if(resultSet.next()) {
            return resultSet.getInt(1);
        }
        return 0;
    }

    public static boolean delete(String deCid) throws SQLException {
        String sql = "update candidate set status = 'deactivated' where c_id = ?";

        Connection connection =DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,deCid);

        return pstm.executeUpdate() > 0;

    }

    public static boolean update(String upid, String upcname, String upaddress, String pid) throws SQLException {

        String sql = "update candidate set c_name = ?,  address = ? ,  p_id = ? where c_id = ? ";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,upcname);
        pstm.setObject(2,upaddress);
        pstm.setObject(3,pid);
        pstm.setObject(4,upid);

        return  pstm.executeUpdate() > 0 ;
    }

    public static Candidate searchData(String searchingid) throws SQLException {
        String sql = "select * from candidate where c_id = ? ";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,searchingid);

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            String cid = resultSet.getString(1);
            String cname = resultSet.getString(2);
            String caddress = resultSet.getString(3);
            String pid = resultSet.getString(4);
            String aid = resultSet.getString(5);
            String status = resultSet.getString(6);

            Candidate candidate = new Candidate(cid, cname, caddress, pid, aid,status);
            return candidate;
        }
        return null;
    }

    public static List<String> getCnameASparty(String pid) throws SQLException {

            String sql = "SELECT c_name FROM candidate where p_id = ? ";
            PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
            pstm.setObject(1, pid);

            ResultSet resultSet = pstm.executeQuery();
            List<String> candidateList = new ArrayList<>();
            while (resultSet.next()) {
                candidateList.add(resultSet.getString(1));
            }
            return candidateList;

    }

    public static String getcandidateId(String cname) throws SQLException {
        String sql = "select c_id from candidate where c_name = ? ";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,cname);

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()) {
            String cid = resultSet.getString(1);
            return cid;
        }
        return null;
    }

    public static List<Candidate> getAllData(String pid) throws SQLException {
        String sql = "select * from candidate where p_id = ? and status = 'active'";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setObject(1,pid);

        ResultSet resultSet =pstm.executeQuery();

        List<Candidate> cList = new ArrayList<>();

        while(resultSet.next()){
            String id = resultSet.getString(1);
            String name = resultSet.getString(2);
            String address = resultSet.getString(3);
            String paid = resultSet.getString(4);
            String aid = resultSet.getString(5);
            String status = resultSet.getString(6);

            Candidate candidate = new Candidate(id, name, address, pid, aid, status);
            cList.add(candidate);
        }
        return cList;
    }

    public static Candidate ifhavecandidatte(String deleteId) throws SQLException {
        String sql = "select * from candidate where p_id = ? and status ='active' limit 1";

        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
        pstm.setObject(1,deleteId);

        ResultSet resultSet =pstm.executeQuery();
            if (resultSet.next()){
                String id = resultSet.getString(1);
                String name = resultSet.getString(2);
                String address = resultSet.getString(3);
                String paid = resultSet.getString(4);
                String aid = resultSet.getString(5);
                String status = resultSet.getString(6);

                Candidate candidate = new Candidate(id, name, address, paid, aid, status);
                return candidate;
            }
            return null;
    }

    public static boolean deletefromPid(String deleteId) throws SQLException {
        String sql = "update candidate set status = 'deactivated' where p_id = ?";

        Connection connection =DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,deleteId);

        return pstm.executeUpdate() > 0;
    }

    public static String getCandidateName(String name) throws SQLException {
        String sql = "select c_name from candidate where c_id = ? ";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,name);

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            String cname = resultSet.getString(1);
           return cname;
        }
        return null;
    }


}
