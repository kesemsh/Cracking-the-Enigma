package utils;

import static connection.constants.Constants.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import object.user.type.UserType;

import java.io.IOException;

public class SessionUtils {

    public static String getUsername (HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(USERNAME_PARAMETER) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }
    
    public static String getUserType(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object sessionAttribute = session != null ? session.getAttribute(USER_TYPE_PARAMETER) : null;
        return sessionAttribute != null ? sessionAttribute.toString() : null;
    }

    public static boolean doesUserHaveSession(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String usernameFromSession = getUsername(request);

        if (usernameFromSession != null) {
            return true;
        } else {
            ServletUtils.setResponseErrorMessage(response, "User is not logged in!");
            return false;
        }
    }

    public static boolean isUserDesiredUserType(HttpServletRequest request, HttpServletResponse response, UserType desiredUserType, boolean  writeErrorMessage) throws IOException {
        String userTypeStringFromSession = getUserType(request);
        UserType userTypeFromSession = UserType.fromString(userTypeStringFromSession);

        if (userTypeFromSession != null && userTypeFromSession.equals(desiredUserType)) {
            return true;
        } else {
            if (writeErrorMessage) {
                ServletUtils.setResponseErrorMessage(response, "User is not a " + desiredUserType + "!");
            }

            return false;
        }
    }

    public static void clearSession (HttpServletRequest request) {
        request.getSession().invalidate();
    }
}