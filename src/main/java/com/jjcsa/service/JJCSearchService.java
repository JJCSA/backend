package com.jjcsa.service;


import com.jjcsa.dto.JJCSearchDTO;
import com.jjcsa.mapper.JJCSearchMapper;
import com.jjcsa.repository.JJCSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JJCSearchService {

    private final JJCSearchRepository jjcSearchRepository;
    private final AWSS3Service awss3Service;
    private  final JJCSearchMapper jjcSearchMapper;

    public List<JJCSearchDTO> invokeJJCSearch(String userId) {
        // TODO use pageable for Lazy initialization from server to client.
        return
                jjcSearchRepository
                        .findAllByUserIdNotOrderByName(userId)
                        .stream()
                        .map(jjcSearchMapper::toJJCSearchDTO)
                        .peek(dto -> dto.setProfilePicture(awss3Service.generateSignedURLFromS3(dto.getProfilePicture())))
                        .collect(Collectors.toList());
    }
}
