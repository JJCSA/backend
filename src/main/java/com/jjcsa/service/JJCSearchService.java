package com.jjcsa.service;


import com.google.common.collect.Lists;
import com.jjcsa.model.JJCSearch;
import com.jjcsa.repository.JJCSearchRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class JJCSearchService {

    @NonNull
    private final JJCSearchRepository jjcSearchRepository;

    public List<JJCSearch> invokeJJCSearch(){
        // TODO use pageable for Lazy initialization from server to client.
        Iterable<JJCSearch> jjcSearchResult = jjcSearchRepository.findAll();
        if(jjcSearchResult == null) {
            return Lists.newArrayList();
        }
        return Lists.newArrayList(jjcSearchResult);
    }


}
