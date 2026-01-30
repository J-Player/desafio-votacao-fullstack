package api.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum PautaStatus {
    ABERTA("aberta"),
    FECHADA("fechada");

    @Getter
    private final String valor;
}
