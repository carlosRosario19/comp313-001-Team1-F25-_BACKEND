package com.centennial.gamepickd.services.impl;

import com.centennial.gamepickd.repository.contracts.PlatformDAO;
import com.centennial.gamepickd.services.contracts.PlatformService;
import com.centennial.gamepickd.util.enums.PlatformType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PlatformServiceImpl implements PlatformService {

    private final PlatformDAO platformDAO;

    @Autowired
    public PlatformServiceImpl(@Qualifier("platformDAOJpaImpl") PlatformDAO platformDAO) {
        this.platformDAO = platformDAO;
    }

    @Override
    @Cacheable(value = "platformsCache")
    public Set<String> getAll() {
        return platformDAO.findAll().stream()
                .map(PlatformType::getVal)
                .collect(Collectors.toSet());
    }
}
