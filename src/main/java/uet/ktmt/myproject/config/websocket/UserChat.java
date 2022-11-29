package uet.ktmt.myproject.config.websocket;

import java.security.Principal;

public class UserChat implements Principal {

    private String username;

    public UserChat(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        return username;
    }
}
