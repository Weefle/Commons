package com.caved_in.commons.game;

import com.caved_in.commons.chat.Chat;
import com.caved_in.commons.game.state.GameState;
import com.caved_in.commons.game.state.IGameState;

/**
 * Barebones implementation of a game state, overriding the defaults defined in {@link IGameState} to provide functionality.
 */
public abstract class MiniGameState extends GameState {
    private boolean setup = false;

    public abstract void update();

    public abstract int id();

    public abstract boolean switchState();

    public abstract int nextState();

    public void setSetup(boolean val) {
        setup = val;
    }

    @Override
    public boolean isSetup() {
        return setup;
    }

    @Override
    public abstract void setup();

    public abstract void destroy();

    public void debug(String... text) {
        Chat.debug(text);
    }
}
