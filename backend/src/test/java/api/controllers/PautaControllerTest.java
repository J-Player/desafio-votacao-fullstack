package api.controllers;

import api.models.dtos.PautaDTO;
import api.models.entities.Pauta;
import api.models.enums.PautaStatus;
import api.services.PautaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Controller de Pautas")
class PautaControllerTest {

    @Mock
    private PautaService pautaService;

    @InjectMocks
    private PautaController pautaController;

    private WebTestClient webTestClient;

    @Test
    @DisplayName("Deve retornar pauta quando buscar por ID existente")
    void deveRetornarPautaQuandoBuscarPorIdExistente() {
        webTestClient = WebTestClient.bindToController(pautaController).build();
        
        UUID id = UUID.randomUUID();
        Pauta pauta = Pauta.builder()
                .id(id)
                .titulo("Pauta de Teste")
                .descricao("Descrição de Teste")
                .status(PautaStatus.ABERTA)
                .startedAt(Instant.now())
                .endAt(Instant.now().plusSeconds(60))
                .build();

        when(pautaService.findById(id)).thenReturn(Mono.just(pauta));

        webTestClient.get()
                .uri("/pauta/{id}", id)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Pauta.class)
                .isEqualTo(pauta);
    }

    @Test
    @DisplayName("Deve retornar todas as pautas")
    void deveRetornarTodasAsPautas() {
        webTestClient = WebTestClient.bindToController(pautaController).build();
        
        Pauta pauta1 = Pauta.builder().id(UUID.randomUUID()).titulo("Pauta 1").build();
        Pauta pauta2 = Pauta.builder().id(UUID.randomUUID()).titulo("Pauta 2").build();

        when(pautaService.findAll()).thenReturn(Flux.just(pauta1, pauta2));

        webTestClient.get()
                .uri("/pauta/all")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Pauta.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("Deve criar nova pauta com sucesso")
    void deveCriarNovaPautaComSucesso() {
        webTestClient = WebTestClient.bindToController(pautaController).build();
        
        PautaDTO dto = new PautaDTO("Nova Pauta", "Descrição", Duration.ofMinutes(5));
        Pauta pautaSalva = Pauta.builder()
                .id(UUID.randomUUID())
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .status(PautaStatus.ABERTA)
                .build();

        when(pautaService.create(any(Pauta.class))).thenReturn(Mono.just(pautaSalva));

        webTestClient.post()
                .uri("/pauta")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Pauta.class)
                .value(pauta -> pauta.getTitulo().equals("Nova Pauta"));
    }

    @Test
    @DisplayName("Deve excluir pauta por ID")
    void deveExcluirPautaPorId() {
        webTestClient = WebTestClient.bindToController(pautaController).build();
        
        UUID id = UUID.randomUUID();
        when(pautaService.deleteById(id)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/pauta/{id}", id)
                .exchange()
                .expectStatus().isNoContent();
    }
}