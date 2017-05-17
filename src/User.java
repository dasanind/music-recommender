/**
 * Authors: Kaveri Thiruvilwamala
 *          Anindita Das
 */
public class User {

    private int userId;

    public User(int userId, float pearsonCoefficient) {
        this.userId = userId;
        this.pearsonCoefficient = pearsonCoefficient;
    }

    private float pearsonCoefficient;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public float getPearsonCoefficient() {
        return pearsonCoefficient;
    }

    public void setPearsonCoefficient(float pearsonCoefficient) {
        this.pearsonCoefficient = pearsonCoefficient;
    }
}

