package ru.javaprojects.projector.users.sociallogin;

import lombok.NonNull;
import org.springframework.security.oauth2.core.user.OAuth2User;
import ru.javaprojects.projector.users.AuthUser;
import ru.javaprojects.projector.users.model.User;

import java.util.Map;

public class CustomOAuth2User extends AuthUser implements OAuth2User {
    private final OAuth2User oauth2User;

    public CustomOAuth2User(@NonNull OAuth2User oauth2User, @NonNull User user) {
        super(user);
        this.oauth2User = oauth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oauth2User.getAttributes();
    }

    @Override
    public String getName() {
        return super.getUsername();
    }
}
