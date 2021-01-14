package com.rules.model;

import lombok.*;

@Getter
@Setter
@Builder
@Data
public class Criminal {

    private String nameSurname;
    private String gender;
    private int age;
    private Interval ageInterval;
    private Interval tallInterval;
    private int tall;
    private String hairStyle;
    private boolean hasGlasses;

    private int guiltinessRate;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Interval{
        private int minVal;
        private int maxVal;
    }
}
