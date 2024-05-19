package lk.ijse.repo;

import lk.ijse.db.DbConnection;
import lk.ijse.model.Area;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AreaRepo {
    public static String getCurrentId() throws SQLException {
        String sql = "SELECT MAX(CAST(a_id AS UNSIGNED)) AS max_id FROM area ";
        PreparedStatement pstm = DbConnection.getInstance().getConnection()
                .prepareStatement(sql);

        ResultSet resultSet = pstm.executeQuery();
        if(resultSet.next()) {
            String aid = resultSet.getString(1);
            return aid;
        }
        return null;
    }

    public static boolean saveData(Area area) throws SQLException {
        String sql = "insert into area values (?,?,?)";

        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);

        pstm.setObject(1,area.getAid());
        pstm.setObject(2,area.getDistric());
        pstm.setObject(3,area.getTerritorry());

        return  pstm.executeUpdate() > 0;
    }
}
