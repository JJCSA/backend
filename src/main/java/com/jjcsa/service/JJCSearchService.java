package com.jjcsa.service;


import com.google.common.collect.Lists;
import com.jjcsa.dto.JJCSearchDTO;
import com.jjcsa.dto.UserDTO;
import com.jjcsa.mapper.JJCSearchMapper;
import com.jjcsa.model.JJCSearch;
import com.jjcsa.model.User;
import com.jjcsa.repository.JJCSearchRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class JJCSearchService {
    private final JJCSearchRepository jjcSearchRepository;
    private final AWSS3Service awss3Service;
    private  final JJCSearchMapper jjcSearchMapper;
    public List<JJCSearchDTO> invokeJJCSearch() {
        // TODO use pageable for Lazy initialization from server to client.
        List<JJCSearch> jjcSearchResult = jjcSearchRepository.findAll();
        if(jjcSearchResult == null) {
            return Lists.newArrayList();
        }
        return jjcSearchResult.stream()
                .map(this::toJJCSearchDTO)
                .collect(Collectors.toList());
    }

    private JJCSearchDTO toJJCSearchDTO(JJCSearch jjcSearch) {
        JJCSearchDTO jjcSearchDTO = jjcSearchMapper.toJJCSearchDTO(jjcSearch);
        jjcSearchDTO.setProfilePicture(awss3Service.generateSignedURLFromS3(jjcSearch.getProfilePicture()));
        return jjcSearchDTO;
    }
}
