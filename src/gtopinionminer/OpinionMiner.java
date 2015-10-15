package gtopinionminer;

import com.gtranslate.Translator;
import is2.lemmatizer.Lemmatizer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static java.lang.System.out;


/**
 * <h1>The main class of the Opinion Miner Tool for German Tweets</h1>
 * <p> This tool was build as a student project during Fall School in Computational Linguistics 2015
 * at Uni Trier, Germany. It is capable of determining a sentiment for German-speaking tweets
 * as a score in the range [-1, 1] (negative to positive), for which it exploits the so called
 * lexicon approach. In particular, 2 sentiment dictionaries are used. </p>
 * @see gtopinionminer.Lexicon
 * <p>ATTENTION: REQUIRES WORKING INTERNET CONNECTION</p>
 * @author Tanya Herasymova
 * @version 1.1
 * @since September 25, 2015
 * */

public class OpinionMiner {

    static final int WINDOW_SIZE_RELEVANT = 4;
    static final int TWEET_MAX_CHARACTERS = 140;

    List<String> keywords;
    List<Tweet> tweets, relevantTweets;
    static Set<String> stopwords = StopList.STOPWORDLIST;

    //tools
    private Tokenization tokenization;
    private Lemmatization lemmatization;
    private Lemmatizer lemmatizer;
    static Translator translator = Translator.getInstance();

    /**
     *Create an OpinionMiner object to run the tool. Provide a list of tweets you want to mine
     * and a list of keywords you want to determine the sentiment for.
     * @param tweetSet  .tsv file containing tweet-id \t tweet text in German to be analysed
     * @param keywordSet .tsv file listing keywords of interest
     */

    public OpinionMiner(String tweetSet, String keywordSet) {
        try {
            initTools();
            keywords = new ArrayList<>();
            tweets = new ArrayList<>();
            relevantTweets = new ArrayList<>();

            //read keywords in
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(keywordSet), "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] split = line.split("\t");
                    String[] hashtaged_split = new String[split.length];
                      for (int i = 0; i<split.length; i++){
                            hashtaged_split[i] = "#"+split[i];
                      }
                    keywords.addAll(Arrays.asList(split));
                    keywords.addAll(Arrays.asList(hashtaged_split));
                }
                keywords = lemmatization.lemmatize(keywords, lemmatizer);


            } catch (IOException ex) {
                ex.printStackTrace();
            }

            //read tweets in
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    new FileInputStream(tweetSet), "UTF-8"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.equals("") || line.equals("\n")) continue;
                    String[] splitted = line.split("\t");
                    Tweet tweet = new Tweet(Long.parseLong(splitted[0]),
                            splitted[1]);
                    tweets.add(tweet);
                    processTweets(tweet);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Initializes the Stanford tokenizer and the Mate lemmatizer for tweet preprocessing.
     * @see <a href="http://nlp.stanford.edu/software/corenlp.shtml" target="_blank">A Suite of Stanford Core NLP Tools</a>
     * @see <a href="http://www.ims.uni-stuttgart.de/forschung/ressourcen/werkzeuge/matetools.html" target="_blank">Mate tools</a>
     *
     * @throws IOException if something goes wrong e.g. with loading models
     */
    private void initTools() throws IOException {
        tokenization = new Tokenization();
        lemmatization = new Lemmatization();
        is2.lemmatizer.Options optsLemmatizer = new is2.lemmatizer.Options(
                new String[]{"-model", "res/lemma-ger-3.6.model"});
        lemmatizer = new Lemmatizer(optsLemmatizer);
    }

    /**
     * Maps the offset of a relevant lemma to a keyword to a weight suitable for
     * use in {@link LemmaList#calculateSentiment()}.
     *
     * @param offset  the (signed) offset of the lemma to the keyword (negative
     *                when it's positioned before to the keyword, positive
     *                otherwise)
     * @return the influence of this lemma on the sentiment score the with
     *         respect to the distance (i.e. {@link java.lang.Math#abs(int)}) to
     *         the keyword, a number between 1 and #WINDOW_SIZE_RELEVANT.
     */
    private static int offsetToKeyword2weight(int offset)
    {
        int distance = Math.abs(offset);
        return WINDOW_SIZE_RELEVANT - Math.max(distance-1, 0);
    }


    /**
     * @param tweet tweet to process
     * <p>Each encountered tweet is preprocessed, including tokenization, lemmatization and stopword removal.
     * <b>Only</b> tweets that contain keywords are further selected, with the tokens surrounding
     * the found keyword k within the window of size 4 being put into a LemmaList.</p>
     * <p>After that each LemmaList is processed by calling its member method,
     * whereby the sentiment score for the keyword k is computed.</p>
     * <p>Finnaly, the sentiment is averaged per tweet.</p>
     * @see Tweet
     * @see LemmaList
     * @see LemmaList#calculateSentiment()
     * @see Tweet#calculateAvgSentiment()
     */
    private void processTweets(Tweet tweet) {

        try {
            List<String> lemmas = lemmatization.lemmatize(tokenization.tokenize(tweet.getTweetText()), lemmatizer);

            //remove stopwords
            List<String> tmp = new ArrayList<>(TWEET_MAX_CHARACTERS);
            String suspect;
            for (int i = 0; i < lemmas.size(); i++){
                if (stopwords.contains(suspect = lemmas.get(i))){
                    tmp.add(suspect);
                }
            }
            lemmas.removeAll(tmp);


            //look for keywords
            int pos = 0;
            for (String relevant : lemmas) {
                if (keywords.contains(relevant)) {

                    LemmaList lemmaList = new LemmaList(relevant);

                    //assigning positional weight based on proximity to the keyword
                    //window of size #WINDOW_SIZE_RELEVANT
                    for (int before = pos - 1; before >= 0 && before >= pos - WINDOW_SIZE_RELEVANT; before--) {
                        lemmaList.add(new TweetLemma(
                                lemmas.get(before),
                                offsetToKeyword2weight(pos - before)));
                    }

                    for (int after = pos + 1; after < lemmas.size() && after <= pos + WINDOW_SIZE_RELEVANT; after++) {
                        lemmaList.add(new TweetLemma(
                                lemmas.get(after),
                                offsetToKeyword2weight(pos - after)));
                    }

                    tweet.addRelevant(relevant, lemmaList);
                    lemmaList.calculateSentiment();
                    if (!relevantTweets.contains(tweet)) relevantTweets.add(tweet);
                }
                pos++;
            }
            tweet.calculateAvgSentiment();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Outputs information about relevant tweets with their sentiment scores  to stout.
     */
    public void printStats(){
        for (Tweet rel : relevantTweets) {
            out.println(rel+"\n");
        }
        out.format("Total tweets: %d; total relevant tweets: %d", tweets.size(), relevantTweets.size());
    }



    public static void main(String... args) {

        //The tool was tested using a set of tweets retrieved during a 75 minute German talkshow
        //"Guenter Jauch "on 18.01.2015. The experimental list of keywords was composed by a professor
        //of media studies and includes political figures and entities.
        //run the tool with the demo files "demotweets.tsv" and "keywords.tsv" or your own one.
        new OpinionMiner("demotweets.tsv",
                "keywords.tsv").printStats();
    }
}

