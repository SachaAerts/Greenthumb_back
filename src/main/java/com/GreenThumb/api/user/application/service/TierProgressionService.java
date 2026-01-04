package com.GreenThumb.api.user.application.service;

import com.GreenThumb.api.user.application.dto.TierDto;
import com.GreenThumb.api.user.domain.entity.Tier;
import com.GreenThumb.api.user.domain.repository.ThreadLimitTierRepository;
import com.GreenThumb.api.user.domain.repository.UserRepository;
import com.GreenThumb.api.user.infrastructure.entity.ThreadLimitTierEntity;
import com.GreenThumb.api.user.infrastructure.mapper.TierMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TierProgressionService {
    private final ThreadLimitTierRepository threadLimitTierRepository;
    private final UserRepository userRepository;

    public TierProgressionService(ThreadLimitTierRepository threadLimitTierRepository, UserRepository userRepository) {
        this.threadLimitTierRepository = threadLimitTierRepository;
        this.userRepository = userRepository;
    }

    public TierDto checkAndUpgradeTier(Long userId, int currentMessageCount, Long currentTierId) {
        ThreadLimitTierEntity tier = threadLimitTierRepository.findCurrentTier(currentMessageCount);

        if (tier.getIdTier().equals(currentTierId)) {
            return null;
        }

        userRepository.updateUserTier(userId, tier.getIdTier());
        Tier tierDomain = TierMapper.toDomain(tier);

        return TierDto.toDto(tierDomain);
    }
}
