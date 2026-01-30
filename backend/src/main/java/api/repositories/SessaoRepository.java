package api.repositories;

import api.models.entities.Sessao;
import api.models.enums.Voto;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface SessaoRepository extends ReactiveCrudRepository<Sessao, UUID> {

    Mono<Long> countByPautaIdAndVoto(UUID pautaId, Voto voto);


}
