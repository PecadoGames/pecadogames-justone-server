package ch.uzh.ifi.seal.soprafs20.GameLogic;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

public class APIResponse {

    String word;

    Long score;

    @JsonIgnore
    List tags;

    public List getTags() {
        return tags;
    }

    public void setTags(List tags) {
        this.tags = tags;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }
}
