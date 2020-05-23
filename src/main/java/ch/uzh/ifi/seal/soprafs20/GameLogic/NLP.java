package ch.uzh.ifi.seal.soprafs20.GameLogic;


import opennlp.tools.stemmer.PorterStemmer;

public class NLP {
    private final PorterStemmer stemmer = new PorterStemmer();

    public boolean checkClue(String clue, String word) {
        String clueToLower = clue.toLowerCase();
        String wordToLower = word.toLowerCase();
        String clueStem = stemWord(clueToLower);
        String wordStem = stemWord(wordToLower);

        if(clueToLower.length() > 30) { return false; }

        if(!(clue.matches("^[a-zA-Z]+$") || clue.matches("^[0-9]+$"))) { return false; }

        if(clueToLower.contains(wordToLower) || wordToLower.contains(clueToLower)) { return false; }

        if(clueStem.equals(wordStem)) {return false;}

        return !clueStem.contains(wordStem) && !wordStem.contains(clueStem);
    }

    public String stemWord(String word) {
        return stemmer.stem(word);
    }

}
