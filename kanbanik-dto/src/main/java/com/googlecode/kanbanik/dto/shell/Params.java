package com.googlecode.kanbanik.dto.shell;

import java.io.Serializable;

public interface Params extends Serializable {
    String getSessionId();
    void setSessionId(String sessionId);
}
