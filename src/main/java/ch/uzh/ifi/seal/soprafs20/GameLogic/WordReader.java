package ch.uzh.ifi.seal.soprafs20.GameLogic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WordReader {
    private List<String> words = new ArrayList<>();
    private final Logger log = LoggerFactory.getLogger(WordReader.class);

    //ToDo: Catch exceptions
    public WordReader() {
        URL resource;
        File words_file = null;
        BufferedReader bufferedReader = null;
        try {
            resource = getClass().getClassLoader().getResource("words.txt");
            assert resource != null;
            File file = Paths.get(resource.toURI()).toFile();
            String absolutePath = file.getAbsolutePath();
            words_file = new File(absolutePath);
        }
        catch (URISyntaxException exception) {
            log.error(exception.getMessage());
        }

        try {
            assert words_file != null;
            bufferedReader = new BufferedReader(new FileReader(words_file));
        }
        catch (FileNotFoundException exception) {
            log.error(exception.getMessage());
        }

        try {
            for(String line; (line = bufferedReader.readLine()) != null;) {
                if(!line.isEmpty()) {
                    this.words.add(line);
                }
            }
        }
        catch (IOException exception) {
            log.error(exception.getMessage());
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
