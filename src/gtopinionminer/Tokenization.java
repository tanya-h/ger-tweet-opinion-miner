package gtopinionminer;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


/**
 * The class creates a convenient method to use Stanford NLP Tokenizer.
 * @author Tanya Anikina, Uni Saarland
 * @see gtopinionminer.Tokenization#tokenize(java.lang.String)
 */

public class Tokenization {


    /**
     * @param text text to be tokenized
     * @return a List of tokenized Strings
     */

    public List<String> tokenize(String text) {

        String str = text.replace("-", " - ");
        str = str.replace(",", " ");

        PTBTokenizer<Word> tokenizer = PTBTokenizer
                .newPTBTokenizer(new StringReader(str));
        List<Word> words = tokenizer.tokenize();
        List<String> tokens = new ArrayList<>(words.size());
        for (Word w : words) {
            tokens.add(w.value());
        }
        return tokens;
    }
}
