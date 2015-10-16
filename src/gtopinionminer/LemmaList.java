package gtopinionminer;

import com.gtranslate.Language;

import java.util.ArrayList;

/**
 * The class provides a data structure to store the words surrounding a keyword and
 * compute a sentiment that this keyword is likely to carry. For this purpose the "bag-of-words"
 * approach is combined with assigning certain weights based on specific features.
 * @see gtopinionminer.LemmaList#calculateSentiment()
 */

public class LemmaList extends ArrayList<TweetLemma> {

    private double sentiment;
    private String relevantToKeyword;
    private StringBuilder sb = new StringBuilder(200);

    public LemmaList(String relevantToKeyword) {
        this.relevantToKeyword = relevantToKeyword;
        sb.append("<<" + this.relevantToKeyword + ">>");
    }

    /**
     * <p>This method is central for the framework.</p>
     * <p>It incorporates dictionary lookup, calculation and normalisation.
     * Each lemma in the list is looked up first in the German lexicon. Should it contain
     * no such entry, the string is translated using Google Translate API and looked up in the English
     * lexicon. The obtained score is weighted by:</p>
     * <ul style="list-style-type:circle">
     *     <li>subtracting 0.5 if the word is negated</li>
     *     <li>doubling the score if it the word is a hashtag</li>
     *     <li>multiplicating it with the proximity value</li>
     *     </ul>
     * <p> Before adding to the final result, the weighted score is normalized back
     * to the interval [-1;1] while preserving neutral 0 scores.
     * Normalization thus is a piece-wise linear range reduction.</p> 
     */

    public void calculateSentiment() {
        double sum = 0.0;
        double c = 1./(2*1.5); //constant for normalising negative values
        double e = 1./(2*1.0); //constant for normalising positive values

        for (TweetLemma tl : this) {

            double polarity = Lexicon.LEXICON_GERMAN.lookup(tl.getLemma());
            if(polarity == 0.0) polarity = Lexicon.LEXICON_ENGLISH.lookup(
                  OpinionMiner.translator.translate(tl.getLemma(), Language.GERMAN, Language.ENGLISH));

            //weighting
            if (tl.isNegated()) polarity -= 0.5;
            if (tl.isHashtagged()) polarity *= 2;

            //intermediate values appended to a member StringBulder
            //WARNING: may decrease efficiency. outcomment for processing larger tweet sets.
            sb.append(tl.getLemma()).append("\t[").append(tl.getWeight()).append(" , ");
            sb.append(polarity).append("]\t");

            //normalization for individual scores
            double x = polarity * tl.getWeight();
            sum+= x<0 ? c*x : e*x;

        }
        //normalization for total scores
        double tmp = sum / this.size();
        double spread = 3;
        sentiment = Math.signum(tmp)*Math.pow(Math.abs(tmp), 1/spread);
        sb.append("\nSentiment score: ").append(sentiment);
    }


    public double getSentiment(){return this.sentiment;}
    public String toString() {return sb.toString();}
}
