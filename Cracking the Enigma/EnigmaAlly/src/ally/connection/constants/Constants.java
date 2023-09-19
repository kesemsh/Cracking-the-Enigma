package ally.connection.constants;

import com.google.gson.Gson;

public class Constants {
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    public final static String USERNAME_PARAMETER = "username";
    public final static String USER_TYPE_PARAMETER = "user-type";

    public final static String TASK_SIZE_PARAMETER = "task-size";

    public final static String BATTLEFIELD_NAME_PARAMETER = "battlefield-name";
    public final static String ALL_BATTLEFIELDS = "all-battlefields";

    public final static String BATTLEFIELD_DETAILS_TYPE_PARAMETER = "details-type";
    public final static String ALLIES_COUNT = "allies-count";

    public final static String CANDIDATES_LIST_SIZE_PARAMETER = "candidates-list-size";

    public final static Gson GSON_INSTANCE = new Gson();
}
