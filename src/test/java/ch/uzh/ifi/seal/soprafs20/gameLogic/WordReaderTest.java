package ch.uzh.ifi.seal.soprafs20.gameLogic;

import ch.uzh.ifi.seal.soprafs20.GameLogic.WordReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WordReaderTest {

    @Test
    void wordReader_constructor_test() throws IOException, URISyntaxException {
        WordReader reader = new WordReader();
        List<String> words = reader.getWords();

        assertTrue(words.size() > 0);
    }

    @Test
    void wordReader_getRandomWords_test() throws IOException, URISyntaxException {
        WordReader reader = new WordReader();
        List<String> randWords = reader.getRandomWords(13);

        assertEquals(13, randWords.size());
    }
}
