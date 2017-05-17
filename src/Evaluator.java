
import java.util.Map;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;

public class Evaluator {
    public static void main(String args[]) {

        ContentBasedEvaluator.predictRatings();
        CollaborativeEvaluator.predictRatings();
        HybridEvaluator.predictRatings();

        File f = new File("/Users/MusicDataDump/UserRated.txt");
        File f1 = new File("/Users/MusicDataDump/UserUnRatedTest.txt");
        boolean success = f.delete();
        boolean success1 = f1.delete();
        if (!success) {
            throw new IllegalArgumentException("Delete: deletion failed for file f");
        }
        if (!success1) {
            throw new IllegalArgumentException("Delete: deletion failed for file f1");
        }

        Map<Integer, Double> contentTestRatings = new HashMap();
        Map<Integer, Double> collaborativeTestRatings = new HashMap();
        Map<Integer, Double> hybridTestRatings = new HashMap();
        Map<Integer, Double> actualTestRatings = new HashMap();


        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/";

        String db = "Music";
        String driver = "com.mysql.jdbc.Driver";
        String user = "root";
        String pass = "";

        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter User Id for Evaluator:");

        String userId = null;
        try {
            userId = bf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader bf1 = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter Test Table Name for Evaluator:");

        String tableName = null;
        try {
            tableName = bf1.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Integer User = Integer.parseInt(userId);

        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);

            Statement st = con.createStatement();


            ResultSet rs = st.executeQuery("select * from ContentTestRatings");

            while (rs.next()) {
                int songId = rs.getInt(1);
                double rating = rs.getDouble(2);
                contentTestRatings.put(songId, rating);
            }
            rs.close();

            rs = st.executeQuery("select * from CollaborativeTestRatings");

            while (rs.next()) {
                int songId = rs.getInt(1);
                double rating = rs.getDouble(2);
                collaborativeTestRatings.put(songId, rating);
            }
            rs.close();

            rs = st.executeQuery("select * from HybridTestRatings");

            while (rs.next()) {
                int songId = rs.getInt(1);
                double rating = rs.getDouble(2);
                hybridTestRatings.put(songId, rating);
            }
            rs.close();


            rs = st.executeQuery("select songId, rating from "+tableName+" where userId = "+User);

            while (rs.next()) {
                int songId = rs.getInt(1);
                double rating = rs.getDouble(2);
                actualTestRatings.put(songId, rating);
            }
            rs.close();

            double MAEContentBased = evaluate(contentTestRatings, actualTestRatings);
            double MAECollaborativeBased = evaluate(collaborativeTestRatings, actualTestRatings);
            double MAEHybrid = evaluate(hybridTestRatings, actualTestRatings);
            
            System.out.println(" MAE for content based filter is " + evaluate(contentTestRatings, actualTestRatings));
            System.out.println(" MAE for collaborative based filter is " + evaluate(collaborativeTestRatings, actualTestRatings));
            System.out.println(" MAE for hybrid based filter is " + evaluate(hybridTestRatings, actualTestRatings));

            Statement st1 = con.createStatement();

            st1.executeUpdate("create table if not exists MAE (userId int(15), MAEContentBased double, MAECollaborativeBased double, MAEHybrid double)");
            st1.executeUpdate("insert into MAE values (" + User + "," + MAEContentBased + "," + MAECollaborativeBased + "," + MAEHybrid + ")");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static double evaluate(Map<Integer, Double> predictedTestRatings, Map<Integer, Double> actualTestRatings) {

        double sum_error = 0;
        for (int songId : actualTestRatings.keySet()) {
            sum_error = sum_error + Math.abs(predictedTestRatings.get(songId) - actualTestRatings.get(songId));
        }
        double mean_absolute_error = sum_error / actualTestRatings.size();
        return mean_absolute_error;


    }

}
