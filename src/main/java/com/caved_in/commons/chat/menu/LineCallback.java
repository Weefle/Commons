package com.caved_in.commons.chat.menu;

/**
 * Callback for a {@link LineBuilder}
 */
public interface LineCallback {

    /**
     * @return Index of the line
     */
    int getIndex();

    /**
     * @return the {@link LineBuilder}
     */
    LineBuilder getLine();

}