package ch.uzh.ifi.seal.soprafs20.GameLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WordReader {
    private final List<String> words = new ArrayList<>();

    //ToDo: Catch exceptions
    public WordReader() {
        TextFile wordsFile = new TextFile();
        String wordsAsString = wordsFile.getWords();
        String[] wordsInList = wordsAsString.split("\n");
        for(String string : wordsInList) {
            if(!string.isEmpty()) {
                this.words.add(string);
            }
        }
    }

    public List<String> getWords() { return this.words; }

    public List<String> getRandomWords(int amount) {
        List<String> randWords = new ArrayList<>();
        int randIndex = ThreadLocalRandom.current().nextInt(0, this.words.size());
        for(int i = 0; i < amount; i++) {
            randWords.add(this.words.get(randIndex));
            randIndex = (randIndex + 5) % this.words.size();
        }
        return randWords;
    }
}
