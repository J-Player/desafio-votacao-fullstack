package api.services;

import api.models.entities.Pauta;
import api.models.entities.Sessao;
import api.models.enums.PautaStatus;
import api.models.enums.Voto;
import api.repositories.SessaoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Sessões")
class SessaoServiceTest {

    @Mock
    private PautaService pautaService;

    @Mock
    private SessaoRepository repository;

    @InjectMocks
    private SessaoService sessaoService;

    @Test
    @DisplayName("Deve retornar sessão quando ela existir")
    void deveRetornarSessaoQuandoElaExistir() {
        UUID id = UUID.randomUUID();
        Sessao sessao = Sessao.builder()
                .pautaId(UUID.randomUUID())
                .cpf("12345678901")
                .voto(Voto.SIM)
                .build();

        when(repository.findById(id)).thenReturn(Mono.just(sessao));

        StepVerifier.create(sessaoService.findById(id))
                .expectNext(sessao)
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve lançar exceção quando sessão não existir")
    void deveLancarExcecaoQuandoSessaoNaoExistir() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(sessaoService.findById(id))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("Deve permitir votar quando pauta estiver aberta")
    void devePermitirVotarQuandoPautaEstiverAberta() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .titulo("Pauta de Teste")
                .status(PautaStatus.ABERTA)
                .startedAt(Instant.now().minusSeconds(30))
                .endAt(Instant.now().plusSeconds(30))
                .build();

        Sessao sessao = Sessao.builder()
                .pautaId(pautaId)
                .cpf("12345678901")
                .voto(Voto.SIM)
                .build();

        when(pautaService.findById(pautaId)).thenReturn(Mono.just(pauta));
        when(repository.save(sessao)).thenReturn(Mono.just(sessao));

        StepVerifier.create(sessaoService.votar(sessao))
                .expectNext(sessao)
                .verifyComplete();
    }

    @Test
    @DisplayName("Deve lançar exceção quando pauta estiver fechada")
    void deveLancarExcecaoQuandoPautaEstiverFechada() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .titulo("Pauta de Teste")
                .status(PautaStatus.ABERTA)
                .startedAt(Instant.now().minusSeconds(120))
                .endAt(Instant.now().minusSeconds(60))
                .build();

        Sessao sessao = Sessao.builder()
                .pautaId(pautaId)
                .cpf("12345678901")
                .voto(Voto.SIM)
                .build();

        when(pautaService.findById(pautaId)).thenReturn(Mono.just(pauta));

        StepVerifier.create(sessaoService.votar(sessao))
                .expectError(ResponseStatusException.class)
                .verify();
    }

    @Test
    @DisplayName("Deve retornar resultado da votação")
    void deveRetornarResultadoDaVotacao() {
        UUID pautaId = UUID.randomUUID();
        Pauta pauta = Pauta.builder()
                .id(pautaId)
                .titulo("Pauta de Teste")
                .status(PautaStatus.FECHADA)
                .build();

        when(pautaService.findById(pautaId)).thenReturn(Mono.just(pauta));
        when(repository.countByPautaIdAndVoto(pautaId, Voto.SIM)).thenReturn(Mono.just(6L));
        when(repository.countByPautaIdAndVoto(pautaId, Voto.NAO)).thenReturn(Mono.just(4L));

        StepVerifier.create(sessaoService.getResultado(pautaId))
                .expectNextMatches(resultado ->
                    resultado.pautaId().equals(pautaId) &&
                    resultado.totalVotos().equals(10L) &&
                    resultado.votosSim().equals(6L) &&
                    resultado.votosNao().equals(4L) &&
                    resultado.status().equals(PautaStatus.FECHADA))
                .verifyComplete();
    }
}