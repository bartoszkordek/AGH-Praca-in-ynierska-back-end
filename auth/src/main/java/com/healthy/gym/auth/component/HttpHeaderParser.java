package com.healthy.gym.auth.component;

import javax.servlet.http.HttpServletRequest;

public interface HttpHeaderParser {
    String getAuthenticationToken(HttpServletRequest request);
}
