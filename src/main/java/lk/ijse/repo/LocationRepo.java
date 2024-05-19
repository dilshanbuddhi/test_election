package lk.ijse.repo;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Location;

import java.beans.PropertyEditorSupport;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
public class LocationRepo {

    public static List<String> getLocatins() throws SQLException {
        String sql = "SELECT location FROM location";
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

    public static String getCurrentId() throws SQLException {
        String sql = " SELECT CONCAT('L', MAX(CAST(SUBSTRING(l_id, 2) AS UNSIGNED))) AS max_l_id FROM location";
        PreparedStatement pstm = DbConnection.getInstance().getConnection()
                .prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();
        if(resultSet.next()) {
            String locationId = resultSet.getString(1);
            return locationId;
        }
        return null;
    }

    public static String getCmbId(String lname) throws SQLException {
        String sql = "select l_id from location where location =?";
        PreparedStatement pstm = DbConnection.getInstance().getConnection()
                .prepareStatement(sql);
        pstm.setObject(1,lname);

        ResultSet resultSet = pstm.executeQuery();
        if(resultSet.next()) {
            String Lid = resultSet.getString(1);
            return Lid;
        }
        return null;
    }

    public static boolean saveData(Location location1) throws SQLException {
        String sql = "INSERT INTO location VALUES (?,?,?,?)";
        Connection connection= DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1,location1.getId());
        pstm.setObject(2,location1.getLocation());
        pstm.setObject(3,location1.getProvince());
        pstm.setObject(4,location1.getDistrict());

        return pstm.executeUpdate() > 0;

    }

    public static String getLocationName(String lid) throws SQLException {
        String sql = "select location from location where l_id = ?";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,lid);

        ResultSet resultSet = pstm.executeQuery();
        if(resultSet.next()) {
            String lname = resultSet.getString(1);
            return lname;
        }
        return null;
    }

    public static List<Location> getall() throws SQLException {
        List<Location> locationList = new ArrayList<>();

        String sql = "select * from location";
        PreparedStatement pstm = DbConnection.getInstance().getConnection().prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();

        while ( resultSet.next()){
            String id = resultSet.getString(1);
            String location = resultSet.getString(2);
            String province = resultSet.getString(3);
            String district = resultSet.getString(4);

            Location location1 = new Location(id, location, province, district);
            locationList.add(location1);
        }
return locationList;
    }


}
