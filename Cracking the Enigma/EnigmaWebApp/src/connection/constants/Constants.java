package connection.constants;

import com.google.gson.Gson;

public class Constants {
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");

    public final static String USERNAME_PARAMETER = "username";
    public final static String USER_TYPE_PARAMETER = "user-type";

    public final static String FILE_PARAMETER = "file";
    public final static String XML = "xml";

    public final static String CONFIGURATION_MODE_PARAMETER = "configuration-mode";
    public final static String MANUAL = "manual";
    public final static String RANDOM = "random";

    public final static String MESSAGE = "message";

    public final static String BATTLEFIELD_DETAILS_TYPE_PARAMETER = "details-type";
    public final static String ALLIES_COUNT = "allies-count";
    public final static String CANDIDATES_LIST_SIZE_PARAMETER = "candidates-list-size";

    public final static String BATTLEFIELD_NAME_PARAMETER = "battlefield-name";
    public final static String ALL_BATTLEFIELDS = "all-battlefields";

    public final static String TASK_SIZE_PARAMETER = "task-size";

    public final static String AGENT_DATA_TYPE = "agent-data-type";
    public final static String MACHINE_STRING = "machine-string";
    public final static String MESSAGE_TO_DECRYPT = "message-to-decrypt";

    public final static Gson GSON_INSTANCE = new Gson();
}
