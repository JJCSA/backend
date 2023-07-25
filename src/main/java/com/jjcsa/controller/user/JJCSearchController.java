package com.jjcsa.controller.user;

import com.jjcsa.dto.JJCSearchDTO;
import com.jjcsa.service.JJCSearchService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/search")
public class JJCSearchController {

    @NonNull
    private final JJCSearchService jjcSearchService;

    @GetMapping
    public List<JJCSearchDTO> searchJJCUsers(KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();
        log.info("Invoking JJC Search for user:{}", token.getSubject());
        return jjcSearchService.invokeJJCSearch(token.getSubject());
    }

}
