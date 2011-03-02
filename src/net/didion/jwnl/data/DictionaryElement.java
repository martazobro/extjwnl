package net.didion.jwnl.data;

import net.didion.jwnl.util.factory.Owned;

import java.io.Serializable;

/**
 * Any class that represents an element contained in the dictionary (<code>IndexWord</code>s,
 * <code>Synset</code>s, and <code>Exc</code>eptions) must implement this interface.
 *
 * @author didion
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public interface DictionaryElement extends Serializable, Owned {

    /**
     * Returns a key that can be used to index this element.
     *
     * @return a key that can be used to index this element
     */
    public Object getKey();


    /**
     * Returns the element's type.
     *
     * @return the element's type
     */
    public DictionaryElementType getType();

    /**
     * Returns element's part of speech.
     *
     * @return element's part of speech
     */
    public POS getPOS();
}