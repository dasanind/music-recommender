import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.tools.data.FileHandler;
import net.sf.javaml.distance.JaccardIndexSimilarity;

import java.io.File;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.util.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.
/**
 *          This class takes each new song and computes it's average similarity to the songs rated 5 by the user.
 *          We recommend the top 50 songs.
 *          Jaccard Similarity is used.
 *			@author Kaveri Thiruvilwamla g_kaveri@yahoo.com
 *			@author Anindita Das dasanuiit@gmail.com
 */
public class ContentBasedFilter {
    private static int NO_OF_RECOMMENDATIONS = 50;

    public static void main(String args[]) {
        Sql fileexist = new Sql();
        int j = fileexist.DataFile();

        try {

            // data file looks like this - songId, artistId, albumId, genreId
            File f = new File("/Users/anindita/Desktop/Dump/UserRated.txt");
            File f1 = new File("/Users/anindita/Desktop/Dump/UserUnRated.txt");

            Dataset songsRated5 = FileHandler.loadDataset(f, ",");
            Dataset newSongs = FileHandler.loadDataset(f1, ",");

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

            //Predicts songs and calculates ratings when the set of songs rated as 5 or 4 is non empty
            if (j==1){

                // Sort the ArrayList from most similar to least similar
                Collections.sort(recommendations, new SongComparator());

                // Print out the first 50 songs from the sorted list.
                System.out.println("Recommendations are : ");
                int countRecommendations = 0;
                for (Song eachRecommendation : recommendations) {
                    countRecommendations = countRecommendations + 1;

                    System.out.println(Math.round(eachRecommendation.getSongId()) + " " +
                            eachRecommendation.getMeasure());
                    if (countRecommendations == ContentBasedFilter.NO_OF_RECOMMENDATIONS) break;
                }

                // store ratings in database
                Map songsAndPredictedRatings = new HashMap();
                for (Song eachRecommendation : recommendations) {
                    double rating = getEstimatedRating((recommendations.get(0)).getMeasure(),
                            (recommendations.get(recommendations.size() - 1)).getMeasure(), eachRecommendation.getMeasure());
                    songsAndPredictedRatings.put(eachRecommendation.getSongId(), rating);
                }

                storePredictedRatingsInDB(songsAndPredictedRatings);

            }
            //Predicts songs and calculates ratings when the set of songs rated as 5 or 4 is empty and the set of songs rated as 3 is non empty
            else if (j==2){

                // Sort the ArrayList from most similar to least similar
                Collections.sort(recommendations, new SongComparator());
                
                double MidSimilarity = ((recommendations.get(0)).getMeasure() - (recommendations.get(recommendations.size() - 1)).getMeasure())/2;
                
                // Print out the 50 songs from the sorted list.
                System.out.println("Recommendations are : ");
                int countRecommendations = 0;
                for (Song eachRecommendation : recommendations) {

                    if (eachRecommendation.getMeasure() < MidSimilarity) {
                    
                        System.out.println(Math.round(eachRecommendation.getSongId()) + " " +
                                eachRecommendation.getMeasure());
                        countRecommendations = countRecommendations + 1;
                    }

                    if (countRecommendations == ContentBasedFilter.NO_OF_RECOMMENDATIONS) break;
                }

                // store ratings in database
                Map songsAndPredictedRatings = new HashMap();
                for (Song eachRecommendation : recommendations) {
                    double rating = getEstimatedRating((recommendations.get(0)).getMeasure(),
                            (recommendations.get(recommendations.size() - 1)).getMeasure(), eachRecommendation.getMeasure());
                    songsAndPredictedRatings.put(eachRecommendation.getSongId(), rating);
                }

                storePredictedRatingsInDB(songsAndPredictedRatings);

            }
            //Predicts songs and calculates ratings when the set of songs rated as 5, 4, 3 is empty and the set of songs rated as 2 or 1 is non empty
            else if (j==3){

                // Sort the ArrayList from least similar to most similar
                Collections.sort(recommendations, new SongReverseComparator());

                // Print out the first 50 songs from the sorted list.
                System.out.println("Recommendations are : ");
                int countRecommendations = 0;
                for (Song eachRecommendation : recommendations) {
                    countRecommendations = countRecommendations + 1;

                    System.out.println(Math.round(eachRecommendation.getSongId()) + " " +
                            eachRecommendation.getMeasure());
                    if (countRecommendations == ContentBasedFilter.NO_OF_RECOMMENDATIONS) break;
                }
                System.out.println ("recommendations.get(0)).getMeasure() "+(recommendations.get(0)).getMeasure());
                System.out.println ("(recommendations.get(recommendations.size() - 1)).getMeasure() "+(recommendations.get(recommendations.size() - 1)).getMeasure());
                // store ratings in database
                Map songsAndPredictedRatings = new HashMap();
                for (Song eachRecommendation : recommendations) {
                    double rating = getEstimatedRating((recommendations.get(0)).getMeasure(),
                            (recommendations.get(recommendations.size() - 1)).getMeasure(), eachRecommendation.getMeasure());
                    songsAndPredictedRatings.put(eachRecommendation.getSongId(), rating);
                }

                storePredictedRatingsInDB(songsAndPredictedRatings);

            }
            boolean success = f.delete();
            boolean success1 = f1.delete();
            if (!success) {
                throw new IllegalArgumentException("Delete: deletion failed for file f");
            }
            if (!success1) {
                throw new IllegalArgumentException("Delete: deletion failed for file f1");
            }
        } catch (Exception e) {

        }
    }


    private static double getEstimatedRating(double max_sim, double min_sim, double sim_i) {
        double rating = 0;
        if (max_sim - min_sim != 0)
            rating = (((sim_i - min_sim) * 4) / (max_sim - min_sim)) + 1;
        return rating;

    }

    private static void storePredictedRatingsInDB(Map<Integer, Double> predictedRatings) {


        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/";

        String db = "MusicData";
        String driver = "com.mysql.jdbc.Driver";
        String user = "anindita";
        String pass = "sandip";

        try {
            Class.forName(driver).newInstance();
            con = DriverManager.getConnection(url + db, user, pass);

            Statement st = con.createStatement();
            st.executeUpdate("drop table if exists ContentFilterRatings");
            st.executeUpdate("create table ContentFilterRatings ( songId int(10), rating double)");

            for (int songId : predictedRatings.keySet()) {
                st.executeUpdate("insert into ContentFilterRatings values (" + songId + "," + predictedRatings.get(songId) + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


