package com.southeast.scouting.player.dto;

public class MetricDTO {
    private String key;
    private String displayName;
    private String group;
    private Double value;
    private Integer percentile;

    public MetricDTO(String key, String displayName, String group, Double value, Integer percentile) {
        this.key = key;
        this.displayName = displayName;
        this.group = group;
        this.value = value;
        this.percentile = percentile;
    }

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getGroup() { return group; }
    public void setGroup(String group) { this.group = group; }
    public Double getValue() { return value; }
    public void setValue(Double value) { this.value = value; }
    public Integer getPercentile() { return percentile; }
    public void setPercentile(Integer percentile) { this.percentile = percentile; }
}
