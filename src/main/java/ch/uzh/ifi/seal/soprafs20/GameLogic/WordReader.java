package ch.uzh.ifi.seal.soprafs20.GameLogic;

import com.fasterxml.jackson.core.util.BufferRecycler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WordReader {
    private String wordsAsString;
    private List<String> words = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(WordReader.class);

    //ToDo: Catch exceptions
    public WordReader() {
        TextFile words = new TextFile();
        wordsAsString = words.getWords();
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
