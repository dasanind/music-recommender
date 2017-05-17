/**
 * @author Kaveri Thiruvilwamla g_kaveri@yahoo.com
 * @author Anindita Das dasanuiit@gmail.com
 *
 */
import java.sql.*;
import java.io.*;

public class Sql {

    public int DataFile() {


        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/";

        String db = "Music";
        String driver = "com.mysql.jdbc.Driver";
        String user = "kaveri";
        String pass = "";
        int ret =0;
        int rowCount = 0;
        int rowCount1 = 0;
        int rowCount2 = 0;
        int rowCount3 = 0;
        int rowCount4 = 0;

        try{
            Class.forName(driver).newInstance();;
            con = DriverManager.getConnection(url+db, user, pass);
            try{
                Statement st = con.createStatement();

                BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Enter user Id :");
                String userId = bf.readLine();

                BufferedReader bf1 = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Enter table name :");
                String tableName = bf1.readLine();

                //Check whether there are anysongs rated 5 by the user

                ResultSet res = st.executeQuery("SELECT COUNT(*),songId FROM  "+tableName+" WHERE userId =" + userId + " AND rating = 5");
                while (res.next()){
                    rowCount = res.getInt(1);
                }
                System.out.println("Number of songs rated by user " + userId + " as 5 is = " + rowCount);
                Statement st1 = con.createStatement();
                st1.executeUpdate("DROP TABLE if exists UserAll");
                System.out.println("Table deleted successfully!");
         rowCount = 0;
                if (rowCount!= 0) {

                    Statement st2 = con.createStatement();
                    String path = "/Users/MusicDataDump/UserRated.txt";
                    
                    st2.executeQuery("SELECT songId, albumId, artistId, genreId INTO OUTFILE \""+path+"\" FIELDS TERMINATED BY ',' FROM "+tableName+" WHERE rating = 5 AND userId =" +userId);

                    
                    Statement st3 = con.createStatement();
                    String table = "CREATE TABLE UserAll (songId int(10))";
                    st3.executeUpdate(table);
                    System.out.println("Table creation process successfull!");

                    Statement st4 = con.createStatement();
                    st4.executeUpdate("INSERT INTO UserAll (songId) SELECT songId FROM "+tableName+" WHERE userId =" + userId);

                    
                    Statement st5 = con.createStatement();
                    String path1 = "/Users/MusicDataDump/UserUnRated.txt";
                    st5.executeQuery("SELECT * INTO OUTFILE \""+ path1+"\" FIELDS TERMINATED BY ',' FROM SongAttributes s WHERE s.songId NOT IN (SELECT t.songId FROM UserAll t)");
                    
                    ret = 1;
                }
                else {

                    //Check whether there are anysongs rated 4 by the user
                    Statement st6 = con.createStatement();
                    ResultSet res1 = st6.executeQuery("SELECT COUNT(*),songId FROM  "+tableName+" WHERE userId =" + userId + " AND rating = 4");
                    while (res1.next()){
                        rowCount1 = res1.getInt(1);
                    }
                    System.out.println("Number of songs rated by user " + userId + " as 4 is = " + rowCount1);
           rowCount1 =0;
                    if (rowCount1!= 0) {

                        Statement st2 = con.createStatement();
                        String path = "/Users/MusicDataDump/UserRated.txt";

                        st2.executeQuery("SELECT songId, albumId, artistId, genreId INTO OUTFILE \""+path+"\" FIELDS TERMINATED BY ',' FROM "+tableName+" WHERE rating = 4 AND userId =" +userId);


                        Statement st3 = con.createStatement();
                        String table = "CREATE TABLE UserAll (songId int(10))";
                        st3.executeUpdate(table);
                        System.out.println("Table creation process successfull for 4!");

                        Statement st4 = con.createStatement();
                        st4.executeUpdate("INSERT INTO UserAll (songId) SELECT songId FROM "+tableName+" WHERE userId =" + userId);


                        Statement st5 = con.createStatement();
                        String path1 = "/Users/MusicDataDump/UserUnRated.txt";
                        st5.executeQuery("SELECT * INTO OUTFILE \""+ path1+"\" FIELDS TERMINATED BY ',' FROM SongAttributes s WHERE s.songId NOT IN (SELECT t.songId FROM UserAll t)");

                        ret = 1;
                    }
                    else {

                        //Check whether there are anysongs rated 3 by the user
                        Statement st7 = con.createStatement();
                        ResultSet res2 = st7.executeQuery("SELECT COUNT(*),songId FROM  "+tableName+" WHERE userId =" + userId + " AND rating = 3");
                        while (res2.next()){
                            rowCount2 = res2.getInt(1);
                        }
                        System.out.println("Number of songs rated by user " + userId + " as 3 is = " + rowCount2);
           rowCount2 =0;
                        if (rowCount2!= 0) {

                            Statement st2 = con.createStatement();
                            String path = "/Users/MusicDataDump/UserRated.txt";

                            st2.executeQuery("SELECT songId, albumId, artistId, genreId INTO OUTFILE \""+path+"\" FIELDS TERMINATED BY ',' FROM "+tableName+" WHERE rating = 3 AND userId =" +userId);


                            Statement st3 = con.createStatement();
                            String table = "CREATE TABLE UserAll (songId int(10))";
                            st3.executeUpdate(table);
                            System.out.println("Table creation process successful for 3");

                            Statement st4 = con.createStatement();
                            st4.executeUpdate("INSERT INTO UserAll (songId) SELECT songId FROM "+tableName+" WHERE userId =" + userId);


                            Statement st5 = con.createStatement();
                            String path1 = "/Users/MusicDataDump/UserUnRated.txt";
                            st5.executeQuery("SELECT * INTO OUTFILE \""+ path1+"\" FIELDS TERMINATED BY ',' FROM SongAttributes s WHERE s.songId NOT IN (SELECT t.songId FROM UserAll t)");

                            ret = 2;
                        }
                        else {

                            //Check whether there are anysongs rated 2 by the user
                            Statement st8 = con.createStatement();
                            ResultSet res3 = st8.executeQuery("SELECT COUNT(*),songId FROM  "+tableName+" WHERE userId =" + userId + " AND rating = 2");
                            while (res3.next()){
                                rowCount3 = res3.getInt(1);
                            }
                            System.out.println("Number of songs rated by user " + userId + " as 2 is = " + rowCount3);
                   rowCount3=0;
                            if (rowCount3!= 0) {

                                Statement st2 = con.createStatement();
                                String path = "/Users/MusicDataDump/UserRated.txt";

                                st2.executeQuery("SELECT songId, albumId, artistId, genreId INTO OUTFILE \""+path+"\" FIELDS TERMINATED BY ',' FROM "+tableName+" WHERE rating = 2 AND userId =" +userId);


                                Statement st3 = con.createStatement();
                                String table = "CREATE TABLE UserAll (songId int(10))";
                                st3.executeUpdate(table);
                                System.out.println("Table creation process successful for 2");

                                Statement st4 = con.createStatement();
                                st4.executeUpdate("INSERT INTO UserAll (songId) SELECT songId FROM "+tableName+" WHERE userId =" + userId);


                                Statement st5 = con.createStatement();
                                String path1 = "/Users/MusicDataDump/UserUnRated.txt";
                                st5.executeQuery("SELECT * INTO OUTFILE \""+ path1+"\" FIELDS TERMINATED BY ',' FROM SongAttributes s WHERE s.songId NOT IN (SELECT t.songId FROM UserAll t)");

                                ret = 3;
                            }
                            else {

                                //Check whether there are anysongs rated 1 by the user
                                Statement st9 = con.createStatement();
                                ResultSet res4 = st9.executeQuery("SELECT COUNT(*),songId FROM  "+tableName+" WHERE userId =" + userId + " AND rating = 1");
                                while (res4.next()){
                                    rowCount4 = res4.getInt(1);
                                }
                                System.out.println("Number of songs rated by user " + userId + " as 1 is = " + rowCount4);
                                if (rowCount4!= 0) {

                                    Statement st2 = con.createStatement();
                                    String path = "/Users/MusicDataDump/UserRated.txt";

                                    st2.executeQuery("SELECT songId, albumId, artistId, genreId INTO OUTFILE \""+path+"\" FIELDS TERMINATED BY ',' FROM "+tableName+" WHERE rating = 1 AND userId =" +userId);


                                    Statement st3 = con.createStatement();
                                    String table = "CREATE TABLE UserAll (songId int(10))";
                                    st3.executeUpdate(table);
                                    System.out.println("Table creation process successful for 1");

                                    Statement st4 = con.createStatement();
                                    st4.executeUpdate("INSERT INTO UserAll (songId) SELECT songId FROM "+tableName+" WHERE userId =" + userId);


                                    Statement st5 = con.createStatement();
                                    String path1 = "/Users/MusicDataDump/UserUnRated.txt";
                                    st5.executeQuery("SELECT * INTO OUTFILE \""+ path1+"\" FIELDS TERMINATED BY ',' FROM SongAttributes s WHERE s.songId NOT IN (SELECT t.songId FROM UserAll t)");

                                    ret = 3;

                                }
                            }
                        }

                    }
                }
        con.close();
      }
      catch (SQLException s){
        System.out.println("SQL code does not execute.");
      }
    }
    catch (Exception e){
      e.printStackTrace();
    }
    return ret;
    }

}