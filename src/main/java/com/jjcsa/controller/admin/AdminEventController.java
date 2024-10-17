package com.jjcsa.controller.admin;

import com.jjcsa.dto.events.CreateEventDto;
import com.jjcsa.model.User;
import com.jjcsa.model.events.Event;
import com.jjcsa.service.EventService;
import com.jjcsa.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.adapters.springsecurity.account.SimpleKeycloakAccount;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

import static java.util.Objects.isNull;

@Slf4j
@RestController
@RequestMapping(path="/api/admin", produces = "application/json")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;
    private final UserService userService;

    @PostMapping(path = "/event")
    public Event createNewEvent(@RequestBody @Valid CreateEventDto createEventDto,
                                KeycloakAuthenticationToken authenticationToken) {
        SimpleKeycloakAccount account = (SimpleKeycloakAccount) authenticationToken.getDetails();
        AccessToken token = account.getKeycloakSecurityContext().getToken();

        User adminUser = userService.getUserById(token.getSubject());
        if (isNull(adminUser)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find admin user making the request");
        }

        return eventService.createEvent(createEventDto, adminUser);
    }
}
