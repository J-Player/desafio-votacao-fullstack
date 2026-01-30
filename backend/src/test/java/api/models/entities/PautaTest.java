package api.models.entities;

import api.models.dtos.PautaDTO;
import api.models.enums.PautaStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da Entidade Pauta")
class PautaTest {

    @Test
    @DisplayName("Deve criar pauta com duração específica")
    void deveCriarPautaComDuracaoEspecifica() {
        PautaDTO dto = new PautaDTO("Pauta de Teste", "Descrição", Duration.ofMinutes(5));
        
        Pauta pauta = Pauta.of(dto);
        
        assertEquals("Pauta de Teste", pauta.getTitulo());
        assertEquals("Descrição", pauta.getDescricao());
        assertEquals(PautaStatus.ABERTA, pauta.getStatus());
        assertNotNull(pauta.getEndAt());
    }

    @Test
    @DisplayName("Deve criar pauta com duração padrão quando não especificada")
    void deveCriarPautaComDuracaoPadraoQuandoNaoEspecificada() {
        PautaDTO dto = new PautaDTO("Pauta de Teste", "Descrição", null);
        
        Pauta pauta = Pauta.of(dto);
        
        assertEquals("Pauta de Teste", pauta.getTitulo());
        assertEquals("Descrição", pauta.getDescricao());
        assertEquals(PautaStatus.ABERTA, pauta.getStatus());
        assertNotNull(pauta.getEndAt());
    }

    @Test
    @DisplayName("Deve retornar true quando pauta estiver aberta")
    void deveRetornarTrueQuandoPautaEstiverAberta() {
        Instant agora = Instant.now();
        Pauta pauta = Pauta.builder()
                .startedAt(agora.minusSeconds(30))
                .endAt(agora.plusSeconds(30))
                .build();

        assertTrue(pauta.isOpen(agora));
    }

    @Test
    @DisplayName("Deve retornar false quando pauta estiver fechada")
    void deveRetornarFalseQuandoPautaEstiverFechada() {
        Instant agora = Instant.now();
        Pauta pauta = Pauta.builder()
                .startedAt(agora.minusSeconds(120))
                .endAt(agora.minusSeconds(60))
                .build();

        assertFalse(pauta.isOpen(agora));
    }

    @Test
    @DisplayName("Deve retornar false quando ainda não iniciada")
    void deveRetornarFalseQuandoAindaNaoIniciada() {
        Instant agora = Instant.now();
        Pauta pauta = Pauta.builder()
                .startedAt(agora.plusSeconds(30))
                .endAt(agora.plusSeconds(120))
                .build();

        assertFalse(pauta.isOpen(agora));
    }
}