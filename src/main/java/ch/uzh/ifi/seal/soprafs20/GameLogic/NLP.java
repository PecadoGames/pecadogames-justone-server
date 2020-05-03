package ch.uzh.ifi.seal.soprafs20.GameLogic;


import opennlp.tools.stemmer.PorterStemmer;

public class NLP {
    private final PorterStemmer stemmer = new PorterStemmer();

    public boolean checkClue(String clue, String word) {
        String clueToLower = clue.toLowerCase();
        String wordToLower = word.toLowerCase();

        if(clueToLower.contains(" ")) {
            return false;
        }

        if(clueToLower.contains(wordToLower) || wordToLower.contains(clueToLower)) {
            return false;
        }
        return !stemWord(clueToLower).equals(stemWord(wordToLower));
    }

    public String stemWord(String word) {
        return stemmer.stem(word);
    }

}
