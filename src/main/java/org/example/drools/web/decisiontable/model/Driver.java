package org.example.drools.web.decisiontable.model;

import lombok.Data;

/**
 * @author tianwen.yin
 */
@Data
public class Driver {
    private String name;
    private Integer age;
    private Integer priorClaims;
    private String  locationRiskProfile;
}
