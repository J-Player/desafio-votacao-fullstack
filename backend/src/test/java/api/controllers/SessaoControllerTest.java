package api.controllers;

import api.models.dtos.ResultadoResponse;
import api.models.dtos.SessaoDTO;
import api.models.entities.Sessao;
import api.models.enums.PautaStatus;
import api.models.enums.Voto;
import api.services.SessaoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Controller de Sessões")
class SessaoControllerTest {

    @Mock
    private SessaoService sessaoService;

    @InjectMocks
    private SessaoController sessaoController;

    private WebTestClient webTestClient;

    @Test
    @DisplayName("Deve retornar sessão quando buscar por ID existente")
    void deveRetornarSessaoQuandoBuscarPorIdExistente() {
        webTestClient = WebTestClient.bindToController(sessaoController).build();
        
        UUID id = UUID.randomUUID();
        Sessao sessao = Sessao.builder()
                .pautaId(UUID.randomUUID())
                .cpf("12345678901")
                .voto(Voto.SIM)
                .createdAt(Instant.now())
                .build();

        when(sessaoService.findById(id)).thenReturn(Mono.just(sessao));

        webTestClient.get()
                .uri("/sessao/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Sessao.class)
                .isEqualTo(sessao);
    }

    @Test
    @DisplayName("Deve retornar todas as sessões")
    void deveRetornarTodasAsSessoes() {
        webTestClient = WebTestClient.bindToController(sessaoController).build();
        
        Sessao sessao1 = Sessao.builder().pautaId(UUID.randomUUID()).cpf("123").voto(Voto.SIM).build();
        Sessao sessao2 = Sessao.builder().pautaId(UUID.randomUUID()).cpf("456").voto(Voto.NAO).build();

        when(sessaoService.findAll()).thenReturn(Flux.just(sessao1, sessao2));

        webTestClient.get()
                .uri("/sessao/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Sessao.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("Deve registrar voto com sucesso")
    void deveRegistrarVotoComSucesso() {
        webTestClient = WebTestClient.bindToController(sessaoController).build();
        
        UUID pautaId = UUID.randomUUID();
        SessaoDTO dto = new SessaoDTO(pautaId, "12345678901", Voto.SIM);
        Sessao sessaoSalva = Sessao.builder()
                .pautaId(pautaId)
                .cpf(dto.cpf())
                .voto(dto.voto())
                .createdAt(Instant.now())
                .build();

        when(sessaoService.votar(any(Sessao.class))).thenReturn(Mono.just(sessaoSalva));

        webTestClient.post()
                .uri("/sessao")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Sessao.class)
                .value(sessao -> sessao.getVoto().equals(Voto.SIM));
    }

    @Test
    @DisplayName("Deve retornar resultado da votação")
    void deveRetornarResultadoDaVotacao() {
        webTestClient = WebTestClient.bindToController(sessaoController).build();
        
        UUID pautaId = UUID.randomUUID();
        ResultadoResponse resultado = new ResultadoResponse(pautaId, 10L, 6L, 4L, PautaStatus.FECHADA);

        when(sessaoService.getResultado(pautaId)).thenReturn(Mono.just(resultado));

        webTestClient.get()
                .uri("/sessao/pauta/{id}", pautaId)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResultadoResponse.class)
                .isEqualTo(resultado);
    }
}