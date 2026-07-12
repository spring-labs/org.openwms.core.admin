/*
 * Copyright 2005-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openwms.core.admin.app;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.authentication.logout.RedirectServerLogoutSuccessHandler;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import java.net.URI;

/**
 * A AdminSecurityConfiguration configures Spring Security of this service.
 *
 * @author Heiko Scherrer
 */
@Configuration
@EnableWebFluxSecurity
class AdminSecurityConfiguration implements WebFluxConfigurer {

    private final AdminServerProperties adminServer;
    @Value("${owms.admin.start-page}")
    private String startPage;

    public AdminSecurityConfiguration(AdminServerProperties adminServer) {
        this.adminServer = adminServer;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChainSecure(ServerHttpSecurity http) {
        return http
                .authorizeExchange(
                        authorizeExchange -> authorizeExchange
                                .pathMatchers(this.adminServer.path("/assets/**")).permitAll()
                                .pathMatchers(this.adminServer.path("/actuator")).permitAll()
                                .pathMatchers(this.adminServer.path("/actuator/**")).permitAll()
                                .pathMatchers(this.adminServer.path("/instances")).permitAll()
                                .pathMatchers(this.adminServer.path("/instances/**")).permitAll()
                                .pathMatchers(this.adminServer.path("/login")).permitAll()
                                .anyExchange()
                                .authenticated())
                .formLogin(formLogin -> formLogin.loginPage(this.adminServer.path("/login"))
                        .authenticationSuccessHandler(loginSuccessHandler(this.adminServer.path(startPage))))
                .logout(logout -> logout.logoutUrl(this.adminServer.path("/logout"))
                        .logoutSuccessHandler(logoutSuccessHandler(this.adminServer.path("/login?logout"))))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }

    private ServerLogoutSuccessHandler logoutSuccessHandler(String uri) {
        var successHandler = new RedirectServerLogoutSuccessHandler();
        successHandler.setLogoutSuccessUrl(URI.create(uri));
        return successHandler;
    }

    private ServerAuthenticationSuccessHandler loginSuccessHandler(String uri) {
        var successHandler = new RedirectServerAuthenticationSuccessHandler();
        successHandler.setLocation(URI.create(uri));
        return successHandler;
    }
}
