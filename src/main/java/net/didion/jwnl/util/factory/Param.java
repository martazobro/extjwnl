package net.didion.jwnl.util.factory;

import net.didion.jwnl.JWNLException;

/**
 * Represents a parameter in a properties file. Parameters can be nested.
 */
public interface Param {
    String getName();

    String getValue();

    void addParam(Param param);

    Object create() throws JWNLException;
}