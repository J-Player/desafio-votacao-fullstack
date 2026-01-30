package api.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Voto {
    SIM("sim"),
    NAO("não");

    @Getter
    private final String value;

}
