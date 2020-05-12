package ch.uzh.ifi.seal.soprafs20.gameLogic;

import ch.uzh.ifi.seal.soprafs20.GameLogic.NLP;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NLPTest {

    private NLP nlp;

    @BeforeAll
    public void setUp() {
        nlp = new NLP();
    }

    @Test
    public void porterStemmer_test() {
        String word = "laughing";
        String stem = nlp.stemWord(word);
        assertNotEquals(word, stem);
    }

    @Test
    public void porterStemmer_fictionalWord_test() {
        String elvishWord = "suilon";
        String stem = nlp.stemWord(elvishWord);
        assertEquals(elvishWord, stem);
    }

    @Test
    public void regex_twoWords() {
        String twoWords = "James Bond";
        String word = "agent";

        assertFalse(nlp.checkClue(twoWords, word));
    }

    @Test
    public void regex_charsAndDigits() {
        String charsAndDigits = "mus1c";
        String word = "piano";

        assertFalse(nlp.checkClue(charsAndDigits, word));
    }

    @Test
    public void regex_charsOnly() {
        String charsOnly = "music";
        String word = "piano";

        assertTrue(nlp.checkClue(charsOnly, word));
    }

    @Test
    public void regex_digitsOnly() {
        String digitsOnly = "007";
        String word = "James Bond";

        assertTrue(nlp.checkClue(digitsOnly, word));
    }

    @Test
    public void clue_contains_word() {
        String clue = "rainumbrella";
        String word = "umbrella";

        assertFalse(nlp.checkClue(clue, word));
    }

    @Test
    public void word_contains_clue() {
        String clue = "electric";
        String word = "electricity";

        assertFalse(nlp.checkClue(clue, word));
    }

}
