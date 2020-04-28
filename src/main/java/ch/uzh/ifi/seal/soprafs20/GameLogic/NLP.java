package ch.uzh.ifi.seal.soprafs20.GameLogic;

import opennlp.tools.stemmer.PorterStemmer;

public class NLP {
    private PorterStemmer stemmer = new PorterStemmer();

    public String stemWord(String word) {
        stemmer = new PorterStemmer();
        return stemmer.stem(word);
    }

}
