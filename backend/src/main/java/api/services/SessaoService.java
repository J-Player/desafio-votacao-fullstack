package api.services;

import api.models.dtos.ResultadoResponse;
import api.models.entities.Sessao;
import api.models.enums.Voto;
import api.repositories.SessaoRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class SessaoService {

    private final PautaService pautaService;
    private final SessaoRepository repository;

    public Mono<Sessao> findById(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Sessão não encontrada")));
    }

    public Flux<Sessao> findAll() {
        return repository.findAll();
    }

    public Mono<Sessao> votar(Sessao sessao) {
        return pautaService.findById(sessao.getPautaId())
                .flatMap(pauta -> {
                    if (!pauta.isOpen(Instant.now())) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pauta fechada"));
                    }
                    return repository.save(sessao);
                });
    }

    public Mono<ResultadoResponse> getResultado(UUID pautaId) {
        return pautaService.findById(pautaId)
                .flatMap(pauta -> Mono.zip(
                        repository.countByPautaIdAndVoto(pautaId, Voto.SIM),
                        repository.countByPautaIdAndVoto(pautaId, Voto.NAO))
                        .map(votos -> new ResultadoResponse(
                                pauta.getId(),
                                votos.getT1() + votos.getT2(),
                                votos.getT1(),
                                votos.getT2(),
                                pauta.getStatus())));
    }
}
