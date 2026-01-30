package api.models.entities;

import api.models.dtos.PautaDTO;
import api.models.enums.PautaStatus;
import lombok.*;
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
public final class Pauta {
    @Id
    private UUID id;
    private String titulo;
    private String descricao;
    @CreatedDate
    @Column("started_at")
    private Instant startedAt;
    @Column("end_at")
    private Instant endAt;
    @Builder.Default
    private PautaStatus status = PautaStatus.ABERTA;

    public static Pauta of(PautaDTO dto) {
        return Pauta.builder()
                .titulo(dto.titulo())
                .descricao(dto.descricao())
                .endAt(Instant.now().plusSeconds(dto.duration() != null ? dto.duration().getSeconds() : 60))
                .build();
    }

    public boolean isOpen(Instant now) {
        if (startedAt == null || endAt == null) {
            return false;
        }
        return !now.isBefore(this.startedAt) && now.isBefore(this.endAt);
    }
}
