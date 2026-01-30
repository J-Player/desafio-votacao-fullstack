package api.models.entities;

import api.models.dtos.SessaoDTO;
import api.models.enums.Voto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da Entidade Sessão")
class SessaoTest {

    @Test
    @DisplayName("Deve criar sessão a partir do DTO")
    void deveCriarSessaoAPartirDoDTO() {
        UUID pautaId = UUID.randomUUID();
        SessaoDTO dto = new SessaoDTO(pautaId, "12345678901", Voto.SIM);
        
        Sessao sessao = Sessao.of(dto);
        
        assertEquals(pautaId, sessao.getPautaId());
        assertEquals("12345678901", sessao.getCpf());
        assertEquals(Voto.SIM, sessao.getVoto());
        assertNull(sessao.getCreatedAt()); // CreatedDate é definido pelo Spring Data
    }

    @Test
    @DisplayName("Deve criar sessão com builder contendo todos os campos")
    void deveCriarSessaoComBuilderContendoTodosOsCampos() {
        UUID pautaId = UUID.randomUUID();
        
        Sessao sessao = Sessao.builder()
                .pautaId(pautaId)
                .cpf("98765432100")
                .voto(Voto.NAO)
                .build();
        
        assertEquals(pautaId, sessao.getPautaId());
        assertEquals("98765432100", sessao.getCpf());
        assertEquals(Voto.NAO, sessao.getVoto());
    }
}