package com.jjcsa.service;

import com.jjcsa.dto.IUserStatusCountDTO;
import com.jjcsa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserDataAnalysisService {

    private final UserRepository userRepository;

    public List<IUserStatusCountDTO> getUserStatusCount() {
        return userRepository.getUserStatusCount();
    }

    public List<String> getListOfNewUsers() {
        return userRepository.getListOfNewUsers();
    }
}
