package ch.uzh.ifi.seal.soprafs20.gameLogic;

import ch.uzh.ifi.seal.soprafs20.GameLogic.NLP;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NLPTest {

    @Test
    public void porterStemmer_test() {
        NLP nlp = new NLP();
        String word = "laughing";
        String stem = nlp.stemWord(word);
        assertNotEquals(word, stem);
    }

    @Test
    public void porterStemmer_fictionalWord_test() {
        NLP nlp = new NLP();
        String elvishWord = "suilon";
        String stem = nlp.stemWord(elvishWord);
        assertEquals(elvishWord, stem);
    }
}
