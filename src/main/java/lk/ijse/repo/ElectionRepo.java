package lk.ijse.repo;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Election;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ElectionRepo {
    public static boolean savedata(Election election) throws SQLException {
        String sql = "insert into election values (?,?,?,?,?,?,?)";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1,election.getEId());
        pstm.setObject(2,election.getEname());
        pstm.setObject(3,election.getEtype());
        pstm.setObject(4,election.getSdate());
        pstm.setObject(5,election.getEdate());
        pstm.setObject(6,election.getStatus());
        pstm.setObject(7,election.getLid());


        return pstm.executeUpdate() > 0;

    }

    public static boolean fillAssociate(String eid, String lid) throws SQLException {
        String sql ="insert into location values (?,?)";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1,eid);
        pstm.setObject(2,lid);

        return pstm.executeUpdate() > 0;
    }

    public static String getCurrenteId() throws SQLException {
        //  String sql = "SELECT order_id FROM orders ORDER BY order_id DESC LIMIT 1";
        String sql ="SELECT CONCAT('E', MAX(CAST(SUBSTRING(e_id, 2) AS UNSIGNED))) AS max_e_id FROM election";

        PreparedStatement pstm = DbConnection.getInstance().getConnection()
                .prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();
        if(resultSet.next()) {
            String eid = resultSet.getString(1);
            return eid;
        }
        return null;
    }

    public static String getCurrentId() throws SQLException {

           String sql = "SELECT e_id FROM election ORDER BY e_id DESC LIMIT 1";
           Connection connection = DbConnection.getInstance().getConnection();
           PreparedStatement pstm = connection.prepareStatement(sql);

           ResultSet resultSet = pstm.executeQuery();
           if (resultSet.next()) {
               String eid = resultSet.getString(1);
               return eid;
           }
           return null;

    }



    public static List<String> getElection() throws SQLException {

            String sql = "SELECT e_name FROM election where status = 'active'";
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




    public static String getID(String electionName) throws SQLException {
        String sql = "SELECT e_id FROM Election WHERE e_name = ?";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);
            pstm.setString(1, electionName);
            ResultSet resultSet = pstm.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString(1);
            }

        return null;
    }

    public static Election searchData(String id) throws SQLException {
        String status = "active";
        String sql = "SELECT * from election where e_id = ?";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1,id);
        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            String e_id = resultSet.getString(1);
            String e_name = resultSet.getString(2);
            String e_type = resultSet.getString(3);
            String stDate = resultSet.getString(4);
            String endDate= resultSet.getString(5);
            String lid = resultSet.getString(7);


            Election election = new Election(e_id, e_name, e_type, stDate,endDate,status, lid);
            return election;
        }
        return null;
    }

    public static boolean updateData(Election election) throws SQLException {
        String sql = "UPDATE Election " +
                "SET e_name = ?, " +
                "    e_type = ?, " +
                "    start_date = ?, " +
                "    end_date = ?, " +
                "    l_id = ? " +
                "WHERE e_id = ?";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,election.getEname());
        pstm.setObject(2,election.getEtype());
        pstm.setObject(3,election.getSdate());
        pstm.setObject(4,election.getEdate());
        pstm.setObject(5,election.getLid());
        pstm.setObject(6,election.getEId());

        return  pstm.executeUpdate() > 0;

    }

    public static boolean deletction(String id) throws SQLException {
        System.out.println("delete wenn one id eka ==  " +id);
        PreparedStatement pstm = null;
        try (Connection connection = DbConnection.getInstance().getConnection()) {
              pstm = connection.prepareStatement("UPDATE Election SET status = 'deactivated' WHERE e_id = ?");

            pstm.setObject(1, id);
            System.out.println("///////////////Election Status Changed////////////////");
        }catch (Exception e){
            System.out.println(">>>>>>>>>>>>>>>>>>> "+e);
        }
        return pstm.executeUpdate() > 0;

    }

    public static boolean deleteElection(String id) throws SQLException {
        String sql = "UPDATE Election " +
                "SET status = 'deactivated' " +
                "WHERE e_id = ?";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1, id);
        return pstm.executeUpdate() > 0;
    }

    public static String getElectionName(String electionid) throws SQLException {
        String sql = "select e_name from election where e_id = ?";

        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);

        pstm.setObject(1,electionid);

        ResultSet resultSet = pstm.executeQuery();
        if (resultSet.next()){
            String ename = resultSet.getString(1);
            return ename;
        }
        return null;
    }

    public static String getUpcommingEname() throws SQLException {
        String sql ="SELECT e_name\n" +
                "FROM election\n" +
                "WHERE start_date > CURRENT_DATE()\n" +
                "  AND status = 'active'\n" +
                "ORDER BY start_date ASC\n" +
                "LIMIT 1\n";


            PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);

            ResultSet resultSet = pstm.executeQuery();

            if (resultSet.next()){
                String name = resultSet.getString(1);
                return name;
            }
            return null;

    }
}

