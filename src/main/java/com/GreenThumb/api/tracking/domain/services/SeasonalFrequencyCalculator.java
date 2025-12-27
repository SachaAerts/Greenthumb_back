package com.GreenThumb.api.tracking.domain.services;

import com.GreenThumb.api.tracking.domain.entity.TaskTemplate;
import com.GreenThumb.api.plant.application.enums.Season;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Slf4j
@Service
public class SeasonalFrequencyCalculator {

    public int calculateAdjustedFrequency(TaskTemplate template) {
        LocalDate now = LocalDate.now();
        Season currentSeason = Season.fromMonth(now.getMonthValue());

        return calculateAdjustedFrequency(template, currentSeason);
    }

    public int calculateAdjustedFrequency(TaskTemplate template, Season season){
        int baseFrequency = template.baseFrequency();
        int adjustment = getSeasonAdjustment(template, season);

        int adjustedFrequency = baseFrequency + (baseFrequency * adjustment / 100);

        if (adjustment == -100) {
            log.debug("Task {} will NOT be created for season {} (adjustment = -100%)",
                    template.taskType(), season);
            return 0;
        }

        adjustedFrequency = Math.max(1, adjustedFrequency);

        log.debug("Adjusted frequency for {} in {}: {} days (base: {}, adjustment: {}%)",
                template.taskType(), season, adjustedFrequency, baseFrequency, adjustment);

        return adjustedFrequency;
    }

    public boolean shouldCreateTask(TaskTemplate template) {
        LocalDate now = LocalDate.now();
        Season currentSeason = Season.fromMonth(now.getMonthValue());

        int adjustment = getSeasonAdjustment(template, currentSeason);

        if (adjustment == -100) {
            log.info("Skipping task {} for season {} (adjustment = -100%)",
                    template.taskType(), currentSeason);
            return false;
        }

        return true;
    }

    public LocalDate calculateNextTaskDate(TaskTemplate template) {
        int adjustedFrequency = calculateAdjustedFrequency(template);

        if (adjustedFrequency == 0) {
            return null;
        }

        return LocalDate.now().plusDays(adjustedFrequency);
    }

    private int getSeasonAdjustment(TaskTemplate template, Season season) {
        return switch (season) {
            case SPRING -> template.springAdjustment();
            case SUMMER -> template.summerAdjustment();
            case AUTUMN -> template.autumnAdjustment();
            case WINTER -> template.winterAdjustment();
        };
    }
}
