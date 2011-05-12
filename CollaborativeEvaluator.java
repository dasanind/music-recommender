import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.tools.data.FileHandler;

import java.io.File;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Authors: Kaveri Thiruvilwamala
 *          Anindita Das
 */

public class CollaborativeEvaluator{

    public static void predictRatings() {

        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/";

        String db = "Music";
        String driver = "com.mysql.jdbc.Driver";
        String user = "root";
        String pass = "";

        BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter Active User Id for Collaborative:");

        String userId = null;
        try {
            userId = bf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedReader bf1 = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Enter Table Name Collaborative:");

        String tableName = null;
        try {
            tableName = bf1.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Integer activeUser = Integer.parseInt(userId);

        calculate_Pearsons_Coeff(url, db, driver, user, pass, activeUser, tableName);
        calculateAndStorePredictions(url, db, driver, user, pass, activeUser, tableName);


    }

    private static void calculateAndStorePredictions(String url, String db, String driver, String user, String pass, Integer activeUser, String tableName) {
        Connection con;

        try {

            File f = new File("/Users/MusicDataDump/UserUnRatedTest.txt");

            Dataset newSongs = FileHandler.loadDataset(f, ",");

            //Dataset newSongs = FileHandler.loadDataset(new File("/Users/kaverigovindan/UT Austin/Data mining/Project/files/testSet200000.txt"), ",");


            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);

            Statement st = con.createStatement();

            ResultSet rs = st.executeQuery("select * from Pearsons_Coeff");
            Map<Integer, Float> coeff = new HashMap<Integer, Float>();
            List<User> neighborhood = new ArrayList<User>();
            Map<Integer, Map> userAndSongs = new HashMap<Integer, Map>();
            List<Song> songsAndPredictedRatings = new ArrayList<Song>();

            while (rs.next()) {
                int u = rs.getInt(1);
                float co_eff = rs.getFloat(2);

                coeff.put(u, co_eff);
            }
            rs.close();


            Iterator iter = newSongs.iterator();
            while (iter.hasNext()) {

                int songId = (int) ((Instance) iter.next()).value(0);
                neighborhood.clear();
                userAndSongs.clear();

                float numerator = 0;

                float denominator = 0;

                // select users that have rated the same song  and that have Pearson's coeff > 0
                rs = st.executeQuery("select userId from " + tableName + " where songId = " + songId);

                while (rs.next()) {
                    int userId = rs.getInt(1);
                    if (coeff.keySet().contains(userId)) {
                        neighborhood.add(new User(userId, coeff.get(userId)));
                    }
                }

                rs.close();
                //sort them
                Collections.sort(neighborhood, new PearsonCoefficientComparator());
                //select top 30
                List<User> kNeighbors = neighborhood.subList(0, neighborhood.size() > 10 ? 9 : neighborhood.size() - 1);

                // build userAndSongs map for these users and the active user.

                List users = new ArrayList();

                for (User usr : kNeighbors) {
                    users.add(usr.getUserId());
                }

                rs = st.executeQuery("select userId, songId, rating from "+tableName+" where userId in (" + activeUser + "," + toString(users.toArray(), ",", ",") + ")");
                while (rs.next()) {
                    int u = rs.getInt(1);
                    int s = rs.getInt(2);
                    int r = rs.getInt(3);
                    if (userAndSongs.get(u) == null) {
                        userAndSongs.put(u, new HashMap());
                    }
                    (userAndSongs.get(u)).put(s, r);
                }

                rs.close();

                // proceed with calculations for the prediction formula

                // calculate ra_bar
                float ra_bar = 0;
                float sumRatingsByActiveUser = 0;
                Collection<Integer> ratingsByActiveUser = (userAndSongs.get(activeUser)).values();

                for (Integer r : ratingsByActiveUser) {
                    sumRatingsByActiveUser = sumRatingsByActiveUser + r;
                }
                ra_bar = sumRatingsByActiveUser / ratingsByActiveUser.size();


                for (User usr : kNeighbors) {

                    int userId = usr.getUserId();

                    //calculate ru_bar

                    float ru_bar = 0;
                    float sumRatingsByOtherUser = 0;
                    Collection<Integer> ratingsByUser = (userAndSongs.get(userId)).values();

                    for (Integer r : ratingsByUser) {
                        sumRatingsByOtherUser = sumRatingsByOtherUser + r;
                    }
                    ru_bar = sumRatingsByOtherUser / ratingsByUser.size();

                    float ru = (userAndSongs.get(userId)).get(songId) == null ? 0 : (Integer) (userAndSongs.get(userId)).get(songId);
                    numerator = numerator + (ru - ru_bar) * coeff.get(userId);

                    denominator = denominator + usr.getPearsonCoefficient();

                }

                float rating = 0;
                if (denominator != 0)
                    rating = ra_bar + numerator / denominator;

                songsAndPredictedRatings.add(new Song(songId, rating));
            }
            con.close();

            storePredictedRatingsInDatabase(songsAndPredictedRatings);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void storePredictedRatingsInDatabase(List<Song> songsAndPredictedRatings) {

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
            st.executeUpdate("drop table if exists CollaborativeTestRatings");
            st.executeUpdate("create table CollaborativeTestRatings ( songId int(10), rating double)");

            for (Song song : songsAndPredictedRatings) {
                st.executeUpdate("insert into CollaborativeTestRatings values (" + song.getSongId() + "," + song.getMeasure() + ")");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static void calculate_Pearsons_Coeff(String url, String db, String driver, String user, String pass, Integer activeUser, String tableName) {
        Connection con;
        System.out.println("starting   " + new Date());
        try {

            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);

            Statement st = con.createStatement();
            Statement st1 = con.createStatement();
            Statement st2 = con.createStatement();
            Statement st3 = con.createStatement();
            Statement st4 = con.createStatement();

            st4.executeUpdate("DROP TABLE if exists Pearsons_Coeff");

            String table = "CREATE TABLE Pearsons_Coeff (userId int(15), coefficient  double )";
            st3.executeUpdate(table);

            ResultSet rs = st.executeQuery("select distinct userId from " + tableName + " where userId != " + activeUser);

            Map<Integer, Map> userAndSongs = new HashMap<Integer, Map>();
            userAndSongs.put(activeUser, new HashMap());
            HashMap songsAndRatings = new HashMap();

            Collection<Integer> collection = new ArrayList<Integer>();

            while (rs.next()) {

                int otherUserId = rs.getInt(1);

                // use prepared st

                ResultSet rs1 = st1.executeQuery("select userId, songId, rating from " + tableName + " where userId in (" + activeUser + "," + otherUserId + ")");
                Integer uId;
                Integer songId;
                Integer rating;


                songsAndRatings.clear();
                userAndSongs.put(otherUserId, songsAndRatings);


                while (rs1.next()) {

                    uId = rs1.getInt(1);
                    songId = rs1.getInt(2);
                    rating = rs1.getInt(3);

                    (userAndSongs.get(uId)).put(songId, rating);
                }

                // We end up with a HashMap that has a relation like ths : userId -> (songId -> rating)

                // Calculating ra_bar and ru_bar

                collection.clear();
                collection = new ArrayList((userAndSongs.get(activeUser)).values());


                float sumRatingsByActiveUser = 0;

                for (Integer r : collection) {
                    sumRatingsByActiveUser = sumRatingsByActiveUser + r;
                }

                float ra_bar = sumRatingsByActiveUser / collection.size();


                collection.clear();


                collection = new ArrayList(userAndSongs.get(otherUserId).values());

                float sumRatingsByOtherUser = 0;
                for (Integer r : collection) {
                    sumRatingsByOtherUser = sumRatingsByOtherUser + r;
                }

                float ru_bar = sumRatingsByOtherUser / collection.size();

                float numerator = 0;
                float denominator_term1 = 0;
                float denominator_term2 = 0;


                // to find common songIds for both users
                // take the intersection of the 2 sets

                collection.clear();

                collection = new HashSet(userAndSongs.get(activeUser).keySet());

                collection.retainAll(userAndSongs.get(otherUserId).keySet());

                double Pearsons_coeff = -2;

                if (!collection.isEmpty()) {
                    for (int song_id : collection) {
                        //for each song

                        // ra is the rating active user has given to song

                        Integer ra = (Integer) ((userAndSongs.get(activeUser))).get(song_id);

                        // ru  is the rating the other user has given to song
                        Integer ru = (Integer) ((userAndSongs.get(otherUserId))).get(song_id);

                        // gather summation terms in numerator and denominator

                        numerator = numerator + (ra - ra_bar) * (ru - ru_bar);
                        denominator_term1 = denominator_term1 + (ra - ra_bar) * (ra - ra_bar);
                        denominator_term2 = denominator_term2 + (ru - ru_bar) * (ru - ru_bar);

                    }
                    if (denominator_term1 * denominator_term2 != 0) {
                        Pearsons_coeff = numerator / Math.sqrt(denominator_term1 * denominator_term2);
                        //float weighting_factor = collection.size() < 50 ? collection.size()/50 : 1;
                        //Pearsons_coeff = Pearsons_coeff * weighting_factor;

                    }
                    if (Pearsons_coeff > 0)
                        st2.executeUpdate("insert into Pearsons_Coeff values( " + otherUserId + "," + Pearsons_coeff + ")");
                }


                userAndSongs.remove(otherUserId);

                rs1.close();

            }
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }


    private static String toString(Object[] objects, String delimiter, String lastDelimiter) {
        if (objects == null) return "";
        if (objects.length == 0) return "";
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < objects.length; i++) {
            if (objects[i] != null) {
                if ("".equals(objects[i].toString())) continue;
                b.append(objects[i].toString());
            } else {
                b.append("null");
            }
            if (i + 1 < objects.length) {
                if ((i + 2 == objects.length) && lastDelimiter != null)
                    b.append(lastDelimiter);
                else
                    b.append(delimiter);
            }
        }
        return b.toString().trim();
    }
}

