package api.services;

import api.models.entities.Pauta;
import api.models.enums.PautaStatus;
import api.repositories.PautaRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class PautaService {

    private final PautaRepository repository;

    public Mono<Pauta> findById(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Pauta não encontrada")));
    }

    public Flux<Pauta> findAll() {
        return repository.findAll();
    }

    public Mono<Pauta> create(Pauta pauta) {
        return repository.save(pauta);
    }

    public Mono<Void> deleteById(UUID id) {
        return repository.deleteById(id);
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 10000) //Frequência de atualização ⇾ 10 segundos
    public void atualizarPautas() {
        repository.findAll()
                .filter(pauta -> pauta.getStatus() == PautaStatus.ABERTA && pauta.getEndAt().isBefore(Instant.now()))
                .map(p -> p.withStatus(PautaStatus.FECHADA))
                .doOnNext(repository::save)
                .doOnNext(p -> log.debug("Pauta fechada! ID: {} | Titulo: {}", p.getId(), p.getTitulo()));
    }
}
