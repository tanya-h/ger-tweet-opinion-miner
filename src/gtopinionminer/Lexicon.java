package gtopinionminer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


/**
 * The class provides a utility to query the sentiment dictionaries used in the tool.
 * <p>For this, two lexica are created. The German opinion lexicon is based on <a href="http://asv.informatik.uni-leipzig.de/download/sentiws.html"
 * target="_blank"> SentiWS</a>, "a publicly available German-language resource for sentiment analysis, opinion mining". However,
 * with only about 3 500 entries, it is insufficient for any reasonable analysis. A common work-around for this well know
 * issue in the opinion mining is using the numerous resources available for the English language. Thus, an additional
 * English lexicon is created from the <a href="http://sentiwordnet.isti.cnr.it" target="_blank">SentiWordNet</a>.
 * "SentiWordNet is a lexical resource for opinion mining" with over 27 000 entries. Moreover, it is based on the synsets
 * of WordNet with three sentiment scores (positivity, negativity, objectivity) assigned to them.
 * </p>
 */

public final class Lexicon extends HashMap<String, Double>{

    public static final Lexicon LEXICON_GERMAN = new  Lexicon("res/sentiWS.tsv");
    public static final Lexicon LEXICON_ENGLISH = new Lexicon("res/senti_word_net.tsv");

    private Lexicon (String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] splitted = line.split("\t");
                this.put(splitted[0].toLowerCase(), Double.parseDouble(splitted[1]));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * @param key the word to look up
     * @return the word's sentiment score or 0.0 if not in the dictionary
     */

    public double lookup(String key) {
        Double val = this.get(key);
        return (val == null)? 0.0 : val;
    }
}

