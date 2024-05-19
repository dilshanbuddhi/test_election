package lk.ijse.repo;

public class EndElectionRepo {
    /*private static String electionId;
    private static List<Result> resultList;

    public static boolean endElection() throws SQLException {
        Connection connection = DbConnection.getInstance().getConnection();
        connection.setAutoCommit(false);
        //boolean electionDeaactivate = ElectionRepo.deleteElection(electionId);

        try {
            boolean electionDeaactivate = ElectionRepo.deleteElection(electionId);
            if (electionDeaactivate) {
                boolean saveResult = ResultRepo.saveResult(electionId,resultList);
                if (saveResult) {
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

    public static void sendData(String electionid, List<Result> resultList) {
        EndElectionRepo.electionId = electionid;
        EndElectionRepo.resultList = resultList;
    }*/
}
