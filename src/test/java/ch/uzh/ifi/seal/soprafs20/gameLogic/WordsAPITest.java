package ch.uzh.ifi.seal.soprafs20.gameLogic;

import ch.uzh.ifi.seal.soprafs20.GameLogic.APIResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WordsAPITest {

    @Test
    public void test_externalAPI() throws IOException {
        final String uri = "https://api.datamuse.com/words?ml=nuclear+power";

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(uri, String.class);

        System.out.println(result);
        ObjectMapper objectMapper = new ObjectMapper();
        List<APIResponse> response = objectMapper.readValue(result, new TypeReference<List<APIResponse>>(){});

        APIResponse highestScore = new APIResponse();
        if(response.size() > 0) {
            highestScore = response.get(0);
        }

        assertTrue(highestScore.getScore() > 0);
        assertFalse(highestScore.getWord().isBlank());
        //The PoS tags of the word are ignored since not needed
        assertTrue(highestScore.getTags().isEmpty());
    }
}
