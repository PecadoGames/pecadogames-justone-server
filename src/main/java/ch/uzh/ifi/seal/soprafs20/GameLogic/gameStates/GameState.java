package ch.uzh.ifi.seal.soprafs20.GameLogic.gameStates;

public interface GameState {

    public GameState getNextState();
    public GameState getPreviousState();
    public void handle();
}
