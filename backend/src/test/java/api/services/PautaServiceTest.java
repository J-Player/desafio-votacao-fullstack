package api.services;

import api.models.entities.Pauta;
import api.models.enums.PautaStatus;
import api.repositories.PautaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Pautas")
class PautaServiceTest {

    @Mock
    private PautaRepository repository;

    @InjectMocks
    private PautaService pautaService;

    @Test
    @DisplayName("Deve retornar pauta quando ela existir")
    void deveRetornarPautaQuandoElaExistir() {
        UUID id = UUID.randomUUID();
        Pauta pauta = Pauta.builder()
                .id(id)
                .titulo("Pauta de Teste")
                .status(PautaStatus.ABERTA)
                .build();

        when(repository.findById(id)).thenReturn(Mono.just(pauta));

        StepVerifier.create(pautaService.findById(id))
                .expectNext(pauta)
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve lançar exceção quando pauta não existir")
    void deveLancarExcecaoQuandoPautaNaoExistir() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(pautaService.findById(id))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("Deve retornar todas as pautas")
    void deveRetornarTodasAsPautas() {
        Pauta pauta1 = Pauta.builder().id(UUID.randomUUID()).titulo("Pauta 1").build();
        Pauta pauta2 = Pauta.builder().id(UUID.randomUUID()).titulo("Pauta 2").build();

        when(repository.findAll()).thenReturn(Flux.just(pauta1, pauta2));

        StepVerifier.create(pautaService.findAll())
                .expectNext(pauta1)
                .expectNext(pauta2)
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve salvar pauta com sucesso")
    void deveSalvarPautaComSucesso() {
        Pauta pauta = Pauta.builder()
                .titulo("Nova Pauta")
                .status(PautaStatus.ABERTA)
                .build();

        when(repository.save(pauta)).thenReturn(Mono.just(pauta));

        StepVerifier.create(pautaService.create(pauta))
                .expectNext(pauta)
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve excluir pauta por ID")
    void deveExcluirPautaPorId() {
        UUID id = UUID.randomUUID();
        when(repository.deleteById(id)).thenReturn(Mono.empty());

        StepVerifier.create(pautaService.deleteById(id))
                .verifyComplete();
    }
}