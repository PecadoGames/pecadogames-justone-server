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

        if (clueToLower.equals(wordToLower)) {
            return false;
        }
        if (stemWord(clueToLower).equals(stemWord(wordToLower))) {
            return false;
        }

        return true;
    }

    public String stemWord(String word) {
        return stemmer.stem(word);
    }

}
