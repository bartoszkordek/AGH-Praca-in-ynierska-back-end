package com.healthy.gym.user.component;

import javax.servlet.http.HttpServletRequest;

public interface HttpHeaderParser {
    String getAuthenticationToken(HttpServletRequest request);
}
