package com.bnpparibas.dsibddf.fis.pvi.api;

import com.bnpparibas.dsibddf.fis.pvi.api.dto.PviSimulationRequestDto;
import com.bnpparibas.dsibddf.fis.pvi.api.dto.PviSimulationResponseDto;
import com.bnpparibas.dsibddf.fis.pvi.domain.service.PviSimulationService;
import com.bnpparibas.dsibddf.fis.pvi.mapper.PviMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pvi")
@RequiredArgsConstructor
@Tag(name = "PVI", description = "Simulateur de plus-values immobilières")
public class PviController {

    private final PviSimulationService simulationService;
    private final PviMapper mapper;

    @PostMapping("/simulate")
    @Operation(
            summary = "Simuler la plus-value immobilière",
            description = "Reproduit la logique du simulateur PHP : plus-value brute, abattements, IR, PS, surtaxe, net disponible, messages."
    )
    public PviSimulationResponseDto simulate(@RequestBody PviSimulationRequestDto requestDto) {
        var req = mapper.toDomain(requestDto);
        var result = simulationService.simulate(req);
        return mapper.toDto(result);
    }
}
