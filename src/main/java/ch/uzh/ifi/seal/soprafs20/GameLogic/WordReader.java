package ch.uzh.ifi.seal.soprafs20.GameLogic;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class WordReader {
    private List<String> words = new ArrayList<>();

    public WordReader() throws FileNotFoundException, IOException, URISyntaxException {
        URL res = getClass().getClassLoader().getResource("words.txt");
        File file = Paths.get(res.toURI()).toFile();
        String absolutePath = file.getAbsolutePath();
        File words_file = new File(absolutePath);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(words_file));
        for(String line; (line = bufferedReader.readLine()) != null;) {
            if(!line.isEmpty()) {
                this.words.add(line);
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
