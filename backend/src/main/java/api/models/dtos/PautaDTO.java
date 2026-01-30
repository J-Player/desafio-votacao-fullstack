package api.models.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.Duration;

public record PautaDTO(@NotNull String titulo, @Nullable String descricao, @Nullable Duration duration) {

}
