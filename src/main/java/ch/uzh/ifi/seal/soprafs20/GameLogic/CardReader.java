package ch.uzh.ifi.seal.soprafs20.GameLogic;
import java.io.*;
import java.util.ArrayList;
public class CardReader {
    ArrayList<Card> cards = new ArrayList<>();

    public CardReader() throws FileNotFoundException, IOException {
        File words = new File("resources\\words.txt");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(words));

        Card card = new Card();
        String word;
        int counter = 0;
        while ((word = bufferedReader.readLine()) != null){
            if(counter <= 4){
                card.add(word);
                counter = counter + 1;
            } else {
                cards.add(card);
                card = new Card();
                counter = 0;
            }
        }
    }


}
