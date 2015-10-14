package gtopinionminer;

import edu.stanford.nlp.ling.Word;
import edu.stanford.nlp.process.PTBTokenizer;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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
        List<String> tokens = new ArrayList<>();

        Pattern pat = Pattern.compile("[-]");
        Matcher m = pat.matcher(text);
        String str = m.replaceAll(" - ");
        str = str.replaceAll(",", " ");

        PTBTokenizer<Word> tokenizer = PTBTokenizer
                .newPTBTokenizer(new BufferedReader(new StringReader(str)));
        List<Word> words = tokenizer.tokenize();
        for (Word w : words) {
            tokens.add(w.value());
        }
        return tokens;
    }
}
