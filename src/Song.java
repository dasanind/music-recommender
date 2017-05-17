/**
 * @author Kaveri Thiruvilwamala
 * @author        Anindita Das
 */
public class Song {

    private int songId;
    private double measure;

    public Song(double songId, double measure) {
        this.songId = (int)songId;
        this.measure = measure;
    }


    public int getSongId() {
        return songId;
    }

    public double getMeasure() {
        return measure;
    }

    public void setMeasure(double measure) {
        this.measure = measure;
    }
}
