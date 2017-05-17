
/**
 * @author Kaveri Thiruvilwamala
 * @author Anindita Das
 */
import java.util.Comparator;

public class SongComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Song song1 = (Song) o1;
        Song song2 = (Song) o2;

        if (song1.getMeasure() < song2.getMeasure())
            return 1;
        else if (song1.getMeasure() > song2.getMeasure())
            return -1;
        else
            return 0;
    }
}