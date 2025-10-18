package com.centennial.gamepickd.repository.contracts;

import com.centennial.gamepickd.entities.Platform;
import com.centennial.gamepickd.util.enums.PlatformType;

import java.util.Set;

public interface PlatformDAO {
    Set<Platform> findByLabels(Set<PlatformType> labels);
}
