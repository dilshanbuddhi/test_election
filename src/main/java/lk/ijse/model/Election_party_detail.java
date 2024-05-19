package lk.ijse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor@AllArgsConstructor@Data
public class Election_party_detail {
    private String pid;
    private String eid;


    /*public static boolean fillAssociate(String pid, String eid) throws SQLException {
        String sql ="insert into election_voter_detail values (?,?)";
        Connection connection = DbConnection.getInstance().getConnection();
        PreparedStatement pstm = connection.prepareStatement(sql);
        pstm.setObject(1,pid);
        pstm.setObject(2,eid);

        return pstm.executeUpdate() > 0;
    }*/



}
