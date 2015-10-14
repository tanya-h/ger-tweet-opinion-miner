package gtopinionminer;

import java.util.HashMap;
import java.util.Map;

public class Tweet {

    private long id;
    private String tweetText;
    private Map<String, LemmaList> relevantLemmas;
    private double avgSentiment;

    /**
     * Creates a Tweet. For future statistical analysis, more fields can be added (e.g. number of retweets
     * or similar meta data).
     * @param id the unique tweet ID
     * @param text the tweet text attached to it
     */
    public Tweet(long id, String text) {
        this.id = id;
        this.tweetText = text;
        relevantLemmas = new HashMap<>(200);
    }

    /**
     * Hashes the lemma list.
     * @param toKeyword  keyword to which the lemma list relevant, the key
     * @param lemmaList  words surrounding it, the value
     */
    public void addRelevant(String toKeyword, LemmaList lemmaList) {
        relevantLemmas.put(toKeyword, lemmaList);
    }
    public String getTweetText() {return tweetText;}


    /**
     * Updates the tweet's average sentiment score.
     */
    public void calculateAvgSentiment(){
        double sum = 0.0;
        for (LemmaList ltl: relevantLemmas.values()){
            sum += ltl.getSentiment();
        }
        avgSentiment = sum/relevantLemmas.size();
    }


    public String toString() {
        String stats = id + "\t" + tweetText;
        StringBuilder sb = new StringBuilder(stats);
        if(relevantLemmas != null) {
            String tag = (avgSentiment > 0)? " + " : " - ";
            sb.append("\nTag: ").append(tag);
            sb.append("\nOverall score: ").append(avgSentiment);

            for (LemmaList ltl : relevantLemmas.values()) {
                sb.append("\n").append(ltl);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}