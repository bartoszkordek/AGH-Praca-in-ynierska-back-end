package com.healthy.gym.auth.component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface AuthResponseManager {

    void handleTokenExpiredLogout(HttpServletResponse response) throws IOException;

    void handleSuccessfulLogout(HttpServletResponse response) throws IOException;

    void handleUnsuccessfulLogout(HttpServletResponse response) throws IOException;
}
