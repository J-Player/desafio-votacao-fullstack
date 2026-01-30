package api.repositories;

import api.models.entities.Pauta;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PautaRepository extends ReactiveCrudRepository<Pauta, UUID> {
}
