package com.GreenThumb.api.user.application.service;

import com.GreenThumb.api.user.application.dto.TierDto;
import com.GreenThumb.api.user.domain.entity.Tier;
import com.GreenThumb.api.user.domain.repository.ThreadLimitTierRepository;
import org.springframework.stereotype.Service;

@Service
public class TierService {

    private final ThreadLimitTierRepository threadLimitTierRepository;

    public TierService(ThreadLimitTierRepository threadLimitTierRepository) {
        this.threadLimitTierRepository = threadLimitTierRepository;
    }

    public TierDto findNextTier(String name) {
        Tier tier = threadLimitTierRepository.findNextTier(name);

        return TierDto.toDto(tier);
    }
}
