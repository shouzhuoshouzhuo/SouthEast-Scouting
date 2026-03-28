package com.southeast.scouting.player.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "player")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wyscout_id", unique = true)
    private Long wyscoutId;

    @Column(name = "display_name", nullable = false, length = 128)
    private String displayName;

    @Column(name = "full_name", length = 128)
    private String fullName;

    @Column(name = "normalized_name", nullable = false, length = 128)
    private String normalizedName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column
    private Short age;

    @Column(name = "birth_country", length = 64)
    private String birthCountry;

    @Column(name = "passport_country_raw")
    private String passportCountryRaw;

    @Column(length = 16)
    private String foot;

    @Column(name = "height_cm")
    private Short heightCm;

    @Column(name = "weight_kg")
    private Short weightKg;

    @Column(name = "market_value_eur")
    private Long marketValueEur;

    @Column(name = "contract_expires")
    private LocalDate contractExpires;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    public Long getId() {
        return id;
    }

    public Long getWyscoutId() {
        return wyscoutId;
    }

    public void setWyscoutId(Long wyscoutId) {
        this.wyscoutId = wyscoutId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getNormalizedName() {
        return normalizedName;
    }

    public void setNormalizedName(String normalizedName) {
        this.normalizedName = normalizedName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public Short getAge() {
        return age;
    }

    public void setAge(Short age) {
        this.age = age;
    }

    public String getBirthCountry() {
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry) {
        this.birthCountry = birthCountry;
    }

    public String getPassportCountryRaw() {
        return passportCountryRaw;
    }

    public void setPassportCountryRaw(String passportCountryRaw) {
        this.passportCountryRaw = passportCountryRaw;
    }

    public String getFoot() {
        return foot;
    }

    public void setFoot(String foot) {
        this.foot = foot;
    }

    public Short getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Short heightCm) {
        this.heightCm = heightCm;
    }

    public Short getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Short weightKg) {
        this.weightKg = weightKg;
    }

    public Long getMarketValueEur() {
        return marketValueEur;
    }

    public void setMarketValueEur(Long marketValueEur) {
        this.marketValueEur = marketValueEur;
    }

    public LocalDate getContractExpires() {
        return contractExpires;
    }

    public void setContractExpires(LocalDate contractExpires) {
        this.contractExpires = contractExpires;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

