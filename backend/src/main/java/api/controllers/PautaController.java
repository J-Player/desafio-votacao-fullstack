package api.controllers;

import api.models.dtos.PautaDTO;
import api.models.entities.Pauta;
import api.services.PautaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/pauta")
@Tag(name = "Pauta")
@AllArgsConstructor
public class PautaController {

    private final PautaService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca uma pauta pelo ID")
    public Mono<Pauta> findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retorna todas as pautas")
    public Flux<Pauta> findAll() {
        return service.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria uma pauta")
    public Mono<Pauta> create(@RequestBody PautaDTO dto) {
        Pauta pauta = Pauta.of(dto);
        pauta.setStartedAt(Instant.now());
        pauta.setEndAt(pauta.getStartedAt().plusSeconds(
                dto.duration() != null ? dto.duration().getSeconds() : 60
        ));
        return service.create(pauta);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Exclui uma pauta pelo ID")
    public Mono<Void> deleteById(@PathVariable UUID id) {
        return service.deleteById(id);
    }
}
