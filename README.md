# music-recommender

Music Recommender based on Yahoo Music Ratings DataSet

To achieve our goal of recommending new songs for a user, we implemented three ap-
proaches of recommendation - Content Based Filtering, Collaborative filtering and Hybrid
Filtering. We tested these approaches on 2 partitions of the dataset.
The Software environment used for implementation consists of the Java Program-
ming language and the MySql Database. Java Machine Learning Library (Java-ML),
4http://java-ml.sourceforge.net/, provides an API to a collection of machine learn-
ing algorithms which is used for handling the datasets and computation purposes (like
Jaccard Set Similarity etc.). MySql Database is used to store the training, test, song
attributes and few temporary tables for the purpose of implementing the content-based,
collaborative and hybrid approaches.

Content-Based Filtering

The algorithm we used for content-based method can be summarized in the following
steps:
1. If the set of songs S 5 rated 5 by the user u is not empty, we consider this set.
Otherwise, we take the set of songs S 4 rated 4 by the user u. Songs rated as 5 and
4 by u is considered as positively rated.
2. Similarity sim i is calculated between each of the new songs i (not rated by the u)
and set S 5 (or S 4 if S 5 is empty).
3. Each of the new songs i are ranked according to the similarity sim i . Top 50 songs
(which are most similar to set S 5 ) from this list is recommended.
4. If the user u has provided only neutral rating (i.e, 3), step 2 is performed to compute
the similarity between each of the new songs i (not rated by the u) and set S 3 .
Compute the average similarity. Recommend 50 songs whose similarity measure is
less than the average similarity.
5. If the user u has given only ratings of 1 (or 0), step 2 is performed to compute the
similarity between each of the new songs i (not rated by the u) and set S 1 (or S 0 ).
Each of the new songs i are ranked in ascending order according to the similarity
sim i . Recommend the top 50 songs which are most dissimilar to the songs in set S 1
or S 0 . Songs rated as 1 and 0 by u is considered as negatively rated.


Collaborative Filtering

Collaborative filtering makes recommendations for the current user by taking into con-
sideration the ratings/likes/dislikes of other users that are most similar to the current
user.
The algorithm we used for collaborative filtering can be summarized in the following
steps:
1. Calculate the similarities between the active user a (the user that prediction refers
to) and the remaining of the users.
2. Select k most similar users that have rated the target item (in this case its a song
represented by song id) as the neighborhood.
3. Compute the predicted rating for the active user a, for each of the new songs i (not
rated by a).
4. Rank each of the new songs i according to the predicted rating. Recommend 50
songs from the list which have the highest predicted rating for the active user a.


Hybrid Filtering

For our Hybrid approach, we used the weighted method. In this method the final recom-
mendation is done by weighting and combining the prediction of the pure recommendation
techniques.
The algorithm we used for our hybrid approach is summarized as follows:
1. For the active user a compute the rating for each of the new songs i (not rated by
a) using the pure content-based and collaborative filter described above.
2. Compute the average rating for each of the new songs i (not rated by a). This the
predicted rating for the active user a for each of the new songs i.
3. Rank each of the new songs i according to the predicted rating. Recommend 50
songs from the list which have the highest predicted rating for the active user a.
