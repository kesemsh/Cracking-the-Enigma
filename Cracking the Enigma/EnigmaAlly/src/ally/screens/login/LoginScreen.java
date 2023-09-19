package ally.screens.login;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import object.user.type.UserType;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

import java.io.IOException;
import java.util.function.Consumer;

import static ally.connection.constants.Constants.*;
import static ally.connection.settings.ConnectionSettings.*;

public class LoginScreen {
    private Consumer<String> onError;
    private Consumer<String> onSuccessfulLogin;
    @FXML private Button loginButton;
    @FXML private TextField loginTextField;

    public void setUp(Consumer<String> onError, Consumer<String> onSuccessfulLogin) {
        this.onError = onError;
        this.onSuccessfulLogin = onSuccessfulLogin;
    }

    @FXML
    private void onLoginButtonClicked() {
        if (loginTextField.getText().isEmpty()) {
            onError.accept("You must enter a name!");
        } else {
            loginButton.setDisable(true);
            HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + LOGIN).newBuilder();

            urlBuilder.addQueryParameter(USERNAME_PARAMETER, loginTextField.getText());
            urlBuilder.addQueryParameter(USER_TYPE_PARAMETER, UserType.ALLY.getStringValue());
            String finalUrl = urlBuilder.build().toString();

            sendAsyncGetRequest(finalUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Platform.runLater(() -> {
                        onError.accept(e.getMessage());
                        loginButton.setDisable(false);
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        Platform.runLater(() -> {
                            onSuccessfulLogin.accept(loginTextField.getText());
                            loginButton.setDisable(false);
                        });
                        response.close();
                    } else {
                        String errorMessage = response.body().string();

                        Platform.runLater(() -> {
                            onError.accept(errorMessage);
                            loginButton.setDisable(false);
                        });
                    }
                }
            });
        }
    }

    public void reset() {
        loginTextField.clear();
    }
}
