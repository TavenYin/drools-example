package org.example.drools.web.forward.model;

import lombok.Data;

/**
 * 洒水器 Fact
 *
 * @author tianwen.yin
 */
@Data
public class Sprinkler {
    private Room room;
    private boolean on;

    public Sprinkler(Room room) {
        this.room = room;
    }
}
