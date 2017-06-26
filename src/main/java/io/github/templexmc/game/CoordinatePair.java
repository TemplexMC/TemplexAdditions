package io.github.templexmc.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CoordinatePair {
    @Getter
    private final double x;
    @Getter
    private final double z;

    @Override
    public String toString() {
        return getX() + " " + getZ();
    }

}
