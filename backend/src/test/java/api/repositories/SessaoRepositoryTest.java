package api.repositories;

import api.models.entities.Pauta;
import api.models.entities.Sessao;
import api.models.enums.PautaStatus;
import api.models.enums.Voto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Testes do Repositório de Sessões")
class SessaoRepositoryTest {

    @Autowired
    private SessaoRepository sessaoRepository;

    @Autowired
    private PautaRepository pautaRepository;

    @Test
    @DisplayName("Deve contar votos por pauta e tipo de voto corretamente")
    void deveContarVotosPorPautaETipoDeVotoCorretamente() {
        Pauta pauta = Pauta.builder()
                .titulo("Pauta de Teste")
                .status(PautaStatus.ABERTA)
                .startedAt(Instant.now())
                .endAt(Instant.now().plusSeconds(60))
                .build();

        StepVerifier.create(
                pautaRepository.save(pauta)
                    .flatMap(savedPauta -> {
                        UUID pautaId = savedPauta.getId();
                        Sessao sessao1 = Sessao.builder().pautaId(pautaId).cpf("111").voto(Voto.SIM).build();
                        Sessao sessao2 = Sessao.builder().pautaId(pautaId).cpf("222").voto(Voto.SIM).build();
                        Sessao sessao3 = Sessao.builder().pautaId(pautaId).cpf("333").voto(Voto.NAO).build();
                        
                        return sessaoRepository.save(sessao1)
                            .then(sessaoRepository.save(sessao2))
                            .then(sessaoRepository.save(sessao3))
                            .then(sessaoRepository.countByPautaIdAndVoto(pautaId, Voto.SIM));
                    })
        )
        .expectNext(2L)
        .verifyComplete();
    }

    @Test
    @DisplayName("Deve retornar zero quando não houver votos")
    void deveRetornarZeroQuandoNaoHouverVotos() {
        UUID pautaId = UUID.randomUUID();

        StepVerifier.create(sessaoRepository.countByPautaIdAndVoto(pautaId, Voto.SIM))
                .expectNext(0L)
                .verifyComplete();
    }
}