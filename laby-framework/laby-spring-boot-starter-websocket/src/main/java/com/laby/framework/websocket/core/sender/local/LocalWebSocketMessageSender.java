package com.laby.framework.websocket.core.sender.local;

import com.laby.framework.websocket.core.sender.AbstractWebSocketMessageSender;
import com.laby.framework.websocket.core.sender.WebSocketMessageSender;
import com.laby.framework.websocket.core.session.WebSocketSessionManager;

/**
 * 本地的 {@link WebSocketMessageSender} 实现类
 *
 * 注意：仅仅适合单机场景！！！
 *
 * @author Laby
 */
public class LocalWebSocketMessageSender extends AbstractWebSocketMessageSender {

    public LocalWebSocketMessageSender(WebSocketSessionManager sessionManager) {
        super(sessionManager);
    }

}
