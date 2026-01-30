package api.models.entities;

import api.models.dtos.SessaoDTO;
import api.models.enums.Voto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Table
@Data
@With
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class Sessao {
    @Id
    private UUID id;
    @Column("pauta_id")
    private UUID pautaId;
    private String cpf;
    private Voto voto;
    @CreatedDate
    @Column("created_at")
    private Instant createdAt;

    public static Sessao of(SessaoDTO dto) {
        return Sessao.builder()
                .pautaId(dto.pautaId())
                .cpf(dto.cpf())
                .voto(dto.voto())
                .build();
    }
}
