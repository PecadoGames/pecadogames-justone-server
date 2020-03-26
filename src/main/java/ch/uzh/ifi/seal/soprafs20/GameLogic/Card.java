package ch.uzh.ifi.seal.soprafs20.GameLogic;

import java.util.ArrayList;

public class Card {
    ArrayList<String> card = new ArrayList<>(4);

    /**
     * Adds string word to a card
     * @param word
     */
    public void add(String word){
        card.add(word);
    }
}
