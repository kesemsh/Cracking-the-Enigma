package utils;

import battlefield.manager.BattlefieldManager;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletResponse;
import users.manager.UserManager;

import java.io.IOException;

public class ServletUtils {
    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String BATTLEFIELD_MANAGER_ATTRIBUTE_NAME = "battlefieldManager";

    private static final Object userManagerLock = new Object();
    private static final Object battlefieldManagerLock = new Object();

    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, new UserManager());
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }

    public static BattlefieldManager getBattlefieldManager(ServletContext servletContext) {

        synchronized (battlefieldManagerLock) {
            if (servletContext.getAttribute(BATTLEFIELD_MANAGER_ATTRIBUTE_NAME) == null) {
                servletContext.setAttribute(BATTLEFIELD_MANAGER_ATTRIBUTE_NAME, new BattlefieldManager());
            }
        }
        return (BattlefieldManager) servletContext.getAttribute(BATTLEFIELD_MANAGER_ATTRIBUTE_NAME);
    }

    public static void setResponseSuccessMessage(HttpServletResponse response, String successMessage) throws IOException {
        response.getWriter().println(successMessage);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    public static void setResponseErrorMessage(HttpServletResponse response, String errorMessage) throws IOException {
        response.getWriter().println(errorMessage);
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}