package agent.connection.settings;

import agent.connection.cookie.manager.SimpleCookieManager;
import okhttp3.*;

import java.io.IOException;

public class ConnectionSettings {
    private final static String DOMAIN = "localhost";
    public final static String BASE_URL = "http://" + DOMAIN + ":8080/EnigmaWebApp";
    private final static SimpleCookieManager simpleCookieManager = new SimpleCookieManager();
    public final static OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder()
                    .cookieJar(simpleCookieManager)
                    .followRedirects(false)
                    .build();

    public static void removeCookiesOf(String domain) {
        simpleCookieManager.removeCookiesOf(domain);
    }

    public final static String LOGIN = "/login";
    public final static String LOGOUT = "/logout";
    public final static String CONTEST_STATUS = "/battlefield/game-status";
    public final static String ALLIES_DATA = "/allies-data";
    public final static String CANDIDATES = "/battlefield/candidates";
    public final static String DECRYPTION_TASKS = "/decryption-tasks";
    public final static String AGENT_DATA = "/agent-data";
    public final static String CONTESTS_DATA = "/battlefield/contests-data";
    public final static String BATTLEFIELD_REGISTER = "/battlefield/register";
    public final static String RESET_CONTEST = "/battlefield/reset";

    public static void sendAsyncGetRequest(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Call call = HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void sendAsyncPostRequest(String url, RequestBody requestBody, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Call call = HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void sendAsyncPutRequest(String url, RequestBody requestBody, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .put(requestBody)
                .build();

        Call call = HTTP_CLIENT.newCall(request);

        call.enqueue(callback);
    }

    public static void logout() {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + LOGOUT).newBuilder();
        String finalUrl = urlBuilder.build().toString();

        sendAsyncGetRequest(finalUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) {
                removeCookiesOf(DOMAIN);
                response.close();
            }
        });
    }

    public static void shutdown() {
        HTTP_CLIENT.dispatcher().executorService().shutdown();
        HTTP_CLIENT.connectionPool().evictAll();
    }
}
