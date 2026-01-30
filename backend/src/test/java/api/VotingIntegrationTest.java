package api;

import api.models.dtos.PautaDTO;
import api.models.dtos.ResultadoResponse;
import api.models.dtos.SessaoDTO;
import api.models.entities.Pauta;
import api.models.entities.Sessao;
import api.models.enums.Voto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@DisplayName("Testes de Integração do Sistema de Votação")
class VotingIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("Deve executar fluxo completo de votação com sucesso")
    void deveExecutarFluxoCompletoDeVotacaoComSucesso() {
        // 1. Criar uma pauta
        PautaDTO pautaDTO = new PautaDTO("Pauta de Teste de Integração", "Descrição de Teste", Duration.ofMinutes(5));
        
        Pauta pautaCriada = webTestClient.post()
                .uri("/pauta")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(pautaDTO)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Pauta.class)
                .returnResult()
                .getResponseBody();

        UUID pautaId = pautaCriada.getId();

        // 2. Registrar votos
        SessaoDTO voto1 = new SessaoDTO(pautaId, "12345678901", Voto.SIM);
        SessaoDTO voto2 = new SessaoDTO(pautaId, "98765432100", Voto.SIM);
        SessaoDTO voto3 = new SessaoDTO(pautaId, "11111111111", Voto.NAO);

        webTestClient.post()
                .uri("/sessao")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(voto1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Sessao.class);

        webTestClient.post()
                .uri("/sessao")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(voto2)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Sessao.class);

        webTestClient.post()
                .uri("/sessao")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(voto3)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Sessao.class);

        // 3. Obter resultados
        webTestClient.get()
                .uri("/sessao/pauta/{id}", pautaId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResultadoResponse.class)
                .value(resultado -> {
                    assert resultado.pautaId().equals(pautaId);
                    assert resultado.totalVotos().equals(3L);
                    assert resultado.votosSim().equals(2L);
                    assert resultado.votosNao().equals(1L);
                });

        // 4. Verificar se pauta pode ser recuperada
        webTestClient.get()
                .uri("/pauta/{id}", pautaId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Pauta.class)
                .value(pauta -> {
                    assert pauta.getId().equals(pautaId);
                    assert pauta.getTitulo().equals("Pauta de Teste de Integração");
                });
    }

    @Test
    @DisplayName("Deve retornar dados dos endpoints de listagem")
    void deveRetornarDadosDosEndpointsDeListagem() {
        // Testar listar todas as pautas
        webTestClient.get()
                .uri("/pauta/all")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Pauta.class);

        // Testar listar todas as sessões
        webTestClient.get()
                .uri("/sessao/all")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Sessao.class);
    }
}