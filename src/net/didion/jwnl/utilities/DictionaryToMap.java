package net.didion.jwnl.utilities;


import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.DictionaryElement;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.AbstractCachingDictionary;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.dictionary.file.DictionaryCatalog;
import net.didion.jwnl.dictionary.file.DictionaryCatalogSet;
import net.didion.jwnl.dictionary.file.DictionaryFileType;
import net.didion.jwnl.dictionary.file.ObjectDictionaryFile;
import net.didion.jwnl.princeton.file.PrincetonObjectDictionaryFile;
import net.didion.jwnl.util.factory.NameValueParam;
import net.didion.jwnl.util.factory.Param;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * DictionaryToMap allows you to populate and create an in-memory map of the WordNet
 * library. The goal of this utility is to provide a performance boost to applications
 * using a high quantity of API calls to the JWNL library
 * (such as word sense disambiguation algorithms, or dictionary services).
 *
 * @author brett
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public class DictionaryToMap {

    private Dictionary dictionary;
    private DictionaryCatalogSet<ObjectDictionaryFile> destinationFiles;

    /**
     * Initialize with the given map destination directory, using the properties file(usually file_properties.xml)
     *
     * @param destinationDirectory - destination directory for in-memory map files
     * @param propFile             - properties file of file-based WordNet
     * @throws JWNLException JWNLException
     * @throws IOException   IOException
     */
    public DictionaryToMap(String destinationDirectory, String propFile)
            throws JWNLException, IOException {
        dictionary = Dictionary.getInstance(new FileInputStream(propFile));
        HashMap<String, Param> params = new HashMap<String, Param>();
        params.put(DictionaryCatalog.DICTIONARY_PATH_KEY, new NameValueParam(dictionary, DictionaryCatalog.DICTIONARY_PATH_KEY, destinationDirectory));
        params.put(DictionaryCatalog.DICTIONARY_FILE_TYPE_KEY, new NameValueParam(dictionary, DictionaryCatalog.DICTIONARY_FILE_TYPE_KEY, PrincetonObjectDictionaryFile.class.getCanonicalName()));
        destinationFiles = new DictionaryCatalogSet<ObjectDictionaryFile>(dictionary, params, ObjectDictionaryFile.class);
    }

    /**
     * Converts the current Dictionary to a MapBackedDictionary.
     *
     * @throws JWNLException JWNLException
     * @throws IOException   IOException
     */
    public void convert() throws JWNLException, IOException {
        destinationFiles.open();
        boolean canClearCache = (dictionary instanceof AbstractCachingDictionary) && ((AbstractCachingDictionary) dictionary).isCachingEnabled();
        for (DictionaryFileType fileType : DictionaryFileType.getAllDictionaryFileTypes()) {
            for (POS pos : POS.getAllPOS()) {
                System.out.println("Converting " + pos.getLabel() + " " + fileType.getName() + "...");
                serialize(pos, fileType);
            }

            if (canClearCache) {
                ((AbstractCachingDictionary) dictionary).clearCache(fileType.getElementType());
            }
        }

        destinationFiles.close();
    }

    private Iterator<? extends DictionaryElement> getIterator(POS pos, DictionaryFileType fileType) throws JWNLException {
        if (fileType == DictionaryFileType.DATA) {
            return dictionary.getSynsetIterator(pos);
        }
        if (fileType == DictionaryFileType.INDEX) {
            return dictionary.getIndexWordIterator(pos);
        }
        if (fileType == DictionaryFileType.EXCEPTION) {
            return dictionary.getExceptionIterator(pos);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private void serialize(POS pos, DictionaryFileType fileType)
            throws JWNLException, IOException {
        ObjectDictionaryFile file = destinationFiles.getDictionaryFile(pos, fileType);
        int count = 0;
        for (Iterator<? extends DictionaryElement> itr = getIterator(pos, fileType); itr.hasNext(); itr.next()) {
            if (++count % 10000 == 0) {
                System.out.println("Counted and cached word " + count + "...");
            }
        }

        Map<Object, DictionaryElement> map = new HashMap<Object, DictionaryElement>((int) Math.ceil((float) count / 0.9F) + 1, 0.9F);
        DictionaryElement elt;
        for (Iterator<? extends DictionaryElement> listItr = getIterator(pos, fileType); listItr.hasNext(); map.put(elt.getKey(), elt)) {
            elt = listItr.next();
        }

        file.writeObject(map);
        file.close();
        System.gc();
        Runtime rt = Runtime.getRuntime();
        System.out.println("total mem: " + rt.totalMemory() / 1024L + "K free mem: " + rt.freeMemory() / 1024L + "K");
        System.out.println("Successfully serialized " + count + " elements...");
    }

    public static void main(String args[]) {
        String destinationDirectory = null;
        String propertyFile = null;
        if (args.length == 2) {
            destinationDirectory = args[0];
            propertyFile = args[1];
        } else {
            System.out.println("DictionaryToMap <destination directory> <properties file>");
            System.exit(0);
        }
        try {
            (new DictionaryToMap(destinationDirectory, propertyFile)).convert();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}