import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Authors: Kaveri Thiruvilwamala
 *          Anindita Das
 */

public class HybridEvaluator {

    public static void predictRatings() {
        Map<Integer, Double> contentBasedRatings = new HashMap();
        Map<Integer, Double> collaborativeBasedRatings = new HashMap();

        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/";

        String db = "Music";
        String driver = "com.mysql.jdbc.Driver";
        String user = "root";
        String pass = "";

        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);

            Statement st = con.createStatement();
            st.executeUpdate("drop table if exists HybridTestRatings");
            st.executeUpdate("create table HybridTestRatings ( songId int(10), rating double)");

           ResultSet rs = st.executeQuery("select * from ContentTestRatings");

            while (rs.next()) {
                int songId = rs.getInt(1);
                double rating = rs.getDouble(2);
                contentBasedRatings.put(songId,rating);
            }
            rs.close();

            rs = st.executeQuery("select * from CollaborativeTestRatings");

            while (rs.next()) {
                int songId = rs.getInt(1);
                double rating = rs.getDouble(2);
                collaborativeBasedRatings.put(songId,rating);
            }
            rs.close();


            for (Integer songId : contentBasedRatings.keySet()) {

                st.executeUpdate("insert into HybridTestRatings values(" + songId + "," +
                        (contentBasedRatings.get(songId) + collaborativeBasedRatings.get(songId))/2 + ")");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

