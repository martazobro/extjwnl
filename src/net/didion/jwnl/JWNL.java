package net.didion.jwnl;

import net.didion.jwnl.data.Adjective;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.VerbFrame;
import net.didion.jwnl.dictionary.Dictionary;
import net.didion.jwnl.util.ResourceBundleSet;

import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Contains system info as well as JWNL properties.
 *
 * @author didion
 * @author Aliaksandr Autayeu avtaev@gmail.com
 */
public class JWNL {
    // OS types
    public static final OS WINDOWS = new OS("windows");
    public static final OS UNIX = new OS("unix");
    public static final OS MAC = new OS("mac");
    public static final OS UNDEFINED = new OS("undefined");

    public static final OS[] DEFINED_OS_ARRAY = {WINDOWS, UNIX, MAC};
    public static final String OS_PROPERTY_NAME = "os.name";

    private static final String CORE_RESOURCE = "JWNLResource";

    private static ResourceBundleSet bundle;
    private static OS currentOS = UNDEFINED;

    static {
        bundle = new ResourceBundleSet(CORE_RESOURCE);
        // set the locale
        //bundle.setLocale(Locale.getDefault());//enable when enough translations will be available.
        bundle.setLocale(new Locale("en", ""));

        // set the OS
        String os = System.getProperty(OS_PROPERTY_NAME);
        for (OS definedOS : DEFINED_OS_ARRAY) {
            if (definedOS.matches(os)) {
                currentOS = definedOS;
            }
        }

        // initialize bundle-dependant resources
        PointerType.initialize();
        Adjective.initialize();
        VerbFrame.initialize();
    }

    /**
     * Create a private JWNL to prevent construction.
     */
    private JWNL() {
    }

    /**
     * Parses a properties file and sets the ready state at various points. Initializes the
     * various PointerType, Adjective, and VerbFrame necessary preprocessing items.
     *
     * @param propertiesStream the properties file stream
     * @throws JWNLException various JWNL exceptions, depending on where this fails
     */
    public static void initialize(InputStream propertiesStream) throws JWNLException {
        Dictionary.getInstance(propertiesStream);
    }

    /**
     * Get the current OS.
     *
     * @return the current OS
     */
    public static OS getOS() {
        return currentOS;
    }

    public static ResourceBundle getResourceBundle() {
        return bundle;
    }

    public static ResourceBundleSet getResourceBundleSet() {
        return bundle;
    }

    /**
     * Resolves <var>msg</var> in one of the resource bundles used by the system
     *
     * @param msg message to resolve
     * @return resolved message
     */
    public static String resolveMessage(String msg) {
        return resolveMessage(msg, new Object[0]);
    }

    /**
     * Resolve <var>msg</var> in one of the resource bundles used by the system.
     *
     * @param msg message to resolve
     * @param obj parameter to insert into the resolved message
     * @return resolved message
     */
    public static String resolveMessage(String msg, Object obj) {
        return resolveMessage(msg, new Object[]{obj});
    }

    /**
     * Resolve <var>msg</var> in one of the resource bundles used by the system
     *
     * @param msg    message to resolve
     * @param params parameters to insert into the resolved message
     * @return resolved message
     */
    public static String resolveMessage(String msg, Object[] params) {
        return insertParams(bundle.getString(msg), params);
    }

    private static String insertParams(String str, Object[] params) {
        StringBuffer buf = new StringBuffer();
        int startIndex = 0;
        for (int i = 0; i < params.length && startIndex <= str.length(); i++) {
            int endIndex = str.indexOf("{" + i, startIndex);
            if (endIndex != -1) {
                buf.append(str.substring(startIndex, endIndex));
                buf.append(params[i] == null ? null : params[i].toString());
                startIndex = endIndex + 3;
            }
        }
        buf.append(str.substring(startIndex, str.length()));
        return buf.toString();
    }

    /**
     * Used to create constants that represent the major categories of operating systems.
     */
    public static final class OS {
        private String name;

        protected OS(String name) {
            this.name = name;
        }

        public String toString() {
            return resolveMessage("JWNL_TOSTRING_001", name);
        }

        /**
         * Returns true if <var>testOS</var> is a version of this OS. For example, calling
         * WINDOWS.matches("Windows 95") returns true.
         * @param testOS OS string
         * @return true if <var>testOS</var> is a version of this OS
         */
        public boolean matches(String testOS) {
            return testOS.toLowerCase().indexOf(name.toLowerCase()) >= 0;
        }
    }
}