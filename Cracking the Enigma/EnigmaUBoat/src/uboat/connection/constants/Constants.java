package uboat.connection.constants;

import com.google.gson.Gson;

public class Constants {
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    public final static String USERNAME_PARAMETER = "username";
    public final static String USER_TYPE_PARAMETER = "user-type";

    public final static String FILE_PARAMETER = "file";

    public final static String CONFIGURATION_MODE_PARAMETER = "configuration-mode";
    public final static String MANUAL = "manual";
    public final static String RANDOM = "random";

    public final static String MESSAGE = "message";

    public final static String BATTLEFIELD_DETAILS_TYPE_PARAMETER = "details-type";
    public final static String ALLIES_COUNT = "allies-count";

    public final static String CANDIDATES_LIST_SIZE_PARAMETER = "candidates-list-size";

    public final static Gson GSON_INSTANCE = new Gson();
}
