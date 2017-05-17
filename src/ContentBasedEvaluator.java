
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.distance.JaccardIndexSimilarity;

import java.io.File;
import java.util.*;
import java.sql.*;

/**
 * Authors: Kaveri Thiruvilwamala
 *          Anindita Das
 */

public class ContentBasedEvaluator {

    public static void predictRatings() {

        Map predictedRatings = new HashMap();
        Sql fileexist = new Sql();
        int j = fileexist.DataFile();

        try {

            // data file looks like this - songId, artistId, albumId, genreId
            File f = new File("/Users/MusicDataDump/UserRated.txt");
            File f1 = new File("/Users/MusicDataDump/UserUnRatedTest.txt");

            Dataset songsRated5 = FileHandler.loadDataset(f, ",");
            Dataset newSongs = FileHandler.loadDataset(f1, ",");

            //Dataset songsRated5 = FileHandler.loadDataset(new File("/Users/kaverigovindan/UT Austin/Data mining/Project/files/withparentgenre/songsRated4ByUser200000.txt"), ",");

            //Dataset newSongs = FileHandler.loadDataset(new File("/Users/kaverigovindan/UT Austin/Data mining/Project/files/testSet200000.txt"), ",");

            List<Song> recommendations = new ArrayList();

            Iterator iter2 = newSongs.iterator();

            while (iter2.hasNext()) {
                // get a new song
                Instance newSong = (Instance) iter2.next();

                double sum_similarity = 0;

                List<Double> sim = new ArrayList<Double>();
                // Sum up its similarities to every song Rated 5.
                Iterator iter = songsRated5.iterator();
                while (iter.hasNext()) {
                    Instance aSongRated5 = (Instance) iter.next();

                    Instance extracted_newSong;
                    Instance extracted_aSongRated5;


                    if (newSong.value(3) == 0 || aSongRated5.value(3) == 0) {
                        extracted_newSong = new DenseInstance(new double[]{newSong.value(1), newSong.value(2)});
                        extracted_aSongRated5 = new DenseInstance(new double[]{aSongRated5.value(1), aSongRated5.value(2)});
                    } else {
                        extracted_newSong = new DenseInstance(new double[]{newSong.value(1), newSong.value(2), newSong.value(3)});
                        extracted_aSongRated5 = new DenseInstance(new double[]{aSongRated5.value(1), aSongRated5.value(2), aSongRated5.value(3)});

                    }

                    double similarity = new JaccardIndexSimilarity().measure(extracted_aSongRated5, extracted_newSong);
                    sim.add(similarity);
                    sum_similarity = sum_similarity + similarity;

                }

                // Get average similarity
                double averageSimilarity = sum_similarity / (songsRated5.size());

                // Create a Song object with songId and similarity and add it to an ArrayList
                recommendations.add(new Song(newSong.value(0), averageSimilarity));

            }

            // Sort the ArrayList from most similar to least similar
            Collections.sort(recommendations, new SongComparator());

            for (Song eachRecommendation : recommendations) {
                double rating = getEstimatedRating((recommendations.get(0)).getMeasure(),
                        (recommendations.get(recommendations.size() - 1)).getMeasure(), eachRecommendation.getMeasure());
                predictedRatings.put(eachRecommendation.getSongId(), rating);

            }

            storePredictedRatingsInDB(predictedRatings);

        } catch (Exception e) {

        }
    }

    private static void storePredictedRatingsInDB(Map<Integer, Double> predictedRatings) {


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
            st.executeUpdate("drop table if exists ContentTestRatings");
            st.executeUpdate("create table ContentTestRatings ( songId int(10), rating double)");

            for (int songId : predictedRatings.keySet()) {
                st.executeUpdate("insert into ContentTestRatings values (" + songId + "," + predictedRatings.get(songId) + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static double getEstimatedRating(double max_sim, double min_sim, double sim_i) {
        double rating = 0;

        if (max_sim - min_sim != 0) {
            rating = (((sim_i - min_sim) * 4) / (max_sim - min_sim)) + 1;       
        }
        return rating;

    }
}