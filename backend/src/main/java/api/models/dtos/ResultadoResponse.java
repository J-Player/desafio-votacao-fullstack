package api.models.dtos;

import api.models.enums.PautaStatus;

import java.util.UUID;

public record ResultadoResponse(
        UUID pautaId,
        Long totalVotos,
        Long votosSim,
        Long votosNao,
        PautaStatus status) {
}
