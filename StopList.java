package gtopinionminer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;


/**
 * The class stores a stoplist used in the preprocessing.
 */
public class StopList extends HashSet<String>{

    public final static Set<String> STOPWORDLIST = new StopList();

    /**
     * Creates a StopList by reading from "stopwords.txt".
     * This stop list for the German language was edited manually and
     * contains about 400 entries, including some strings to clean the output
     * of the preprocessing tools.
     * @see OpinionMiner#initTools()
     * @see OpinionMiner#processTweets(Tweet)
     */
    private StopList(){
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream("stopwords.txt"), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                this.add(line);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}