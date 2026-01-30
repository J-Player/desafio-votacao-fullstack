package api.models.dtos;

import api.models.enums.Voto;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SessaoDTO(@NotNull UUID pautaId, @NotNull String cpf, @NotNull Voto voto) {
}
