package ch.uzh.ifi.seal.soprafs20.gameLogic;

import ch.uzh.ifi.seal.soprafs20.GameLogic.WordReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class WordReaderTest {

    @Test
    public void wordReader_constructor_test() throws IOException, URISyntaxException {
        WordReader reader = new WordReader();
        List<String> words = reader.getWords();

        assertTrue(words.size() > 0);
    }
}
