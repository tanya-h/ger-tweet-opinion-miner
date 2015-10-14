package gtopinionminer;

import is2.data.SentenceData09;
import is2.lemmatizer.Lemmatizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The class creates a convenient method to use Mate is2.Lemmatizer for the German language.
 * @author Tanya Anikina, Uni Saarland
 * @see gtopinionminer.Lemmatization#lemmatize(java.util.List, is2.lemmatizer.Lemmatizer)
 */

public class Lemmatization {

    SentenceData09 i; // data container for the sentence

    /**
     *
     * @param tokens tokens to be lemmatized
     * @param lemmatizer instance of Mate lemmatizer with the correct model loaded
     * @return a List of lemmatized Strings
     * @throws IOException if the specified model cannot be found
     */

    public List<String> lemmatize(List<String> tokens, Lemmatizer lemmatizer)
            throws IOException {

        List<String> lemmas = new ArrayList<>();
        i = new SentenceData09(); // create a data container for a sentence


        ArrayList<String> forms = new ArrayList<>(tokens);

        i.init(forms.toArray(new String[0]));
        lemmatizer.apply(i); // lemmatize a sentence; the result is stored in the sentenceData09 i
        lemmas.addAll(Arrays.asList(i.plemmas));
        return lemmas;
    }
}
