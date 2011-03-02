package net.didion.jwnl.data;

import net.didion.jwnl.JWNL;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * WordNet contains different file types, index, synset data, and exception files.
 * This class defines what the current dictionary is.
 *
 * @author brett
 */
public class DictionaryElementType {

    /**
     * Property to define an index file.
     */
    public static final DictionaryElementType INDEX_WORD = new DictionaryElementType("INDEX_WORD");

    /**
     * Property to define a synset file.
     */
    public static final DictionaryElementType SYNSET = new DictionaryElementType("SYNSET");

    /**
     * Property that defines an exception file.
     */
    public static final DictionaryElementType EXCEPTION = new DictionaryElementType("EXCEPTION");

    /**
     * The name of the dictionary.
     */
    private final String name;

    /**
     * A list of the different dictionary types.
     */
    private static final List<DictionaryElementType> ALL_TYPES = Collections.unmodifiableList(
            Arrays.asList(INDEX_WORD, SYNSET, EXCEPTION));


    /**
     * Gets all the dictionary types.
     *
     * @return
     */
    public static List<DictionaryElementType> getAllDictionaryElementTypes() {
        return ALL_TYPES;
    }


    /**
     * Create a new DictionaryElementType.
     *
     * @param name
     */
    private DictionaryElementType(String name) {
        this.name = name;
    }

    public String toString() {
        return JWNL.resolveMessage("DATA_TOSTRING_016", getName());
    }

    /**
     * Gets the name of this DictionaryElementType.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public int hashCode() {
        return name.hashCode();
    }
}