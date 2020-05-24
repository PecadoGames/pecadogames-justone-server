package ch.uzh.ifi.seal.soprafs20.GameLogic;


import opennlp.tools.stemmer.PorterStemmer;
import org.apache.commons.text.similarity.LevenshteinDistance;

public class NLP {
    private final PorterStemmer stemmer = new PorterStemmer();
    private final LevenshteinDistance editDistance = new LevenshteinDistance();

    public boolean checkClue(String clue, String word) {
        String clueToLower = clue.toLowerCase();
        String wordToLower = word.toLowerCase();
        String clueStem = stemWord(clueToLower);
        String wordStem = stemWord(wordToLower);
        String clueWithoutFirst = clueToLower.substring(1);
        String wordWithoutFirst = wordToLower.substring(1);

        if(clueToLower.length() > 30) { return false; }

        if(!(clue.matches("^[a-zA-Z]+$") || clue.matches("^[0-9]+$"))) { return false; }

        if(clueToLower.contains(wordToLower) || wordToLower.contains(clueToLower)) { return false; }

        if(clueStem.equals(wordStem)) {return false;}

        if(getDistance(clueWithoutFirst, wordWithoutFirst) <= 1) { return false; }

        return !clueStem.contains(wordStem) && !wordStem.contains(clueStem);
    }

    public String stemWord(String word) {
        return stemmer.stem(word);
    }

    public int getDistance(String clue, String word) { return editDistance.apply(clue, word); }
}
