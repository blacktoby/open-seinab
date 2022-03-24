package de.seinab.backend.banking.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class BankingSocketSecurity extends AbstractSecurityWebSocketMessageBrokerConfigurer {
    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/socket/banking/**", "/private/socket/banking/**")
                    .hasRole("BACKEND");
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

}
