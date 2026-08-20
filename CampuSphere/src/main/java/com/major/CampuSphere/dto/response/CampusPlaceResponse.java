package com.major.CampuSphere.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter @Builder
public class CampusPlaceResponse {
    private Long id;
    private String slug;
    private String name;
    private String type;
    private int floors;
    private String openHours;
    private BigDecimal mapX;
    private BigDecimal mapY;
    private String walkTime;
}
