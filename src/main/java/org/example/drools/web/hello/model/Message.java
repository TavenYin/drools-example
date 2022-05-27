package org.example.drools.web.hello.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author tianwen.yin
 */
@Data
public class Message implements Serializable {
    public static final int HELLO   = 0;
    public static final int GOODBYE = 1;

    private String          message;

    private int             status;

}
