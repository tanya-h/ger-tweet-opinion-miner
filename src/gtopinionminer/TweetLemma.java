package gtopinionminer;

public class TweetLemma {

    private String lemma;
    private double weight;
    private boolean negated;
    private boolean hashtagged;

    /**
     * Creates a TweetLemma, storing the corresponding String as well as information
     * for further weighting, such as it being marked with a hashtag or a negation.
     * @param lemma lemmatized word
     * @param distanceToKeywordWeight weight calculated from the proximity to
     *                                the keyword in the initial tweet
     * @see OpinionMiner#processTweets(Tweet)
     * @see LemmaList#calculateSentiment()
     */

    public TweetLemma(String lemma, double weight) {
        this.lemma = lemma;
        if (lemma.startsWith("#")) this.hashtagged = true;
        if (lemma.matches("(nicht)|((kein)|(\\bnie)|(\\bun)).+")) this.negated = true;
        this.weight = weight;
    }

    public String getLemma() {
        return lemma;
    }

    public double getWeight() {
        return weight;
    }

    public boolean isNegated() {
        return negated;
    }

    public boolean isHashtagged() {
        return hashtagged;
    }
}
