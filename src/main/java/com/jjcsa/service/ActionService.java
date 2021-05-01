package com.jjcsa.service;

import com.jjcsa.exception.BadRequestException;
import com.jjcsa.model.Action;
import com.jjcsa.repository.ActionRepository;
import com.jjcsa.util.ActionUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ActionService {

    private final ActionRepository actionRepository;

    public Action saveAction(String toUserEmail,
                             String fromUserEmail,
                             String actionDetails) {
        Action action = ActionUtil.buildAction(toUserEmail, fromUserEmail,actionDetails);
        if(action == null) {
            throw new BadRequestException(
                "Action is null",
                "Action details does not exist",
                "",
                "",
                ""
            );
        }

        action = actionRepository.save(action);
        return action;
    }
}
