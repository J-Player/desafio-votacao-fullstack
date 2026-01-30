package api.controllers;

import api.models.dtos.ResultadoResponse;
import api.models.dtos.SessaoDTO;
import api.models.entities.Sessao;
import api.services.SessaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/sessao")
@Tag(name = "Sessão")
@AllArgsConstructor
public class SessaoController {

    private final SessaoService service;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Busca uma sessão pelo ID")
    public Mono<Sessao> findById(@PathVariable UUID id) {
        return service.findById(id);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retorna todas as sessões")
    public Flux<Sessao> findAll() {
        return service.findAll();
    }

    @GetMapping("/pauta/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Retorna o resultado de uma pauta")
    public Mono<ResultadoResponse> getResultado(@PathVariable("id") UUID pautaId) {
        return service.getResultado(pautaId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Realiza um voto sobre uma pauta")
    public Mono<Sessao> votar(@RequestBody SessaoDTO dto) {
        Sessao sessao = Sessao.of(dto);
        return service.votar(sessao);
    }
}
