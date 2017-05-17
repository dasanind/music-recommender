/**
 * @author Kaveri Thiruvilwamala g_kaveri@yahoo.com
* @author Anindita Das dasanuiit@gmail.com
**/
import java.util.Comparator;

public class SongReverseComparator implements Comparator {

    public int compare(Object o1, Object o2) {
        Song song1 = (Song) o1;
        Song song2 = (Song) o2;

        if (song1.getMeasure() > song2.getMeasure())
            return 1;
        else if (song1.getMeasure() < song2.getMeasure())
            return -1;
        else
            return 0;
    }
}
