import java.util.Comparator;

/**
 * Authors: Kaveri Thiruvilwamala
 *          Anindita Das
 */
public class PearsonCoefficientComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        User user1 = (User) o1;
        User user2 = (User) o2;

        if (user1.getPearsonCoefficient() < user2.getPearsonCoefficient())
            return 1;
        else if (user1.getPearsonCoefficient() > user2.getPearsonCoefficient())
            return -1;
        else
            return 0;
    }
}
