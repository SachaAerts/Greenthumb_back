package com.GreenThumb.api.plant.application.facade;

import com.GreenThumb.api.plant.application.dto.PlantDto;
import com.GreenThumb.api.plant.application.service.PlantModuleService;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * Facade du module Plant
 *
 * <p>
 *  Cette classe applique le design pattern <b>Facade</b> :
 *  elle fournit un point d'entrée unique pour toutes les opérations
 *  liées aux plantes, et masque la complexité interne du module.
 * </p>
 *
 * <p>
 *     L'objectif est de :
 * </p>
 * <ul>
 *     <li>Simplifier l'accès au domaine en exposant des méthodes claires,</li>
 *     <li>Éviter que les couches externes (API Gateway, contrôleur, etc.)
 *          aient à connaitre plusieurs services internes,</li>
 *     <li>Orchestrer les appels aux services de manière centralisée,</li>
 *     <li>Réduire le couplage et stabiliser l'interface publique du module.</li>
 * </ul>
 */
@Component
public class ModulePlantFacade implements PlantFacade {
    private final PlantModuleService plantService;
    public ModulePlantFacade(PlantModuleService plantService) {
        this.plantService = plantService;
    }

    public List<PlantDto> getAllPlants() {
        return plantService.findAll();
    }

    public List<PlantDto> getAllPlantsByUsername(String username) {
        return plantService.findAllByUser_username(username);
    }
}
