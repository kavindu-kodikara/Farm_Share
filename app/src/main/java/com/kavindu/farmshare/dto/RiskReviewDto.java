package com.kavindu.farmshare.dto;

import java.io.Serializable;
import java.util.ArrayList;

public class RiskReviewDto implements Serializable {
    private ArrayList<ChartEntruDto> weatherChartList;
    private ArrayList<ChartEntruDto> soilChartList;
    private boolean soilScoreDrop;
    private boolean weatherScoreDrop;
    private boolean risk;
    private String riskScore;

    public RiskReviewDto() {
    }

    public ArrayList<ChartEntruDto> getWeatherChartList() {
        return weatherChartList;
    }

    public void setWeatherChartList(ArrayList<ChartEntruDto> weatherChartList) {
        this.weatherChartList = weatherChartList;
    }

    public ArrayList<ChartEntruDto> getSoilChartList() {
        return soilChartList;
    }

    public void setSoilChartList(ArrayList<ChartEntruDto> soilChartList) {
        this.soilChartList = soilChartList;
    }

    public boolean isSoilScoreDrop() {
        return soilScoreDrop;
    }

    public void setSoilScoreDrop(boolean soilScoreDrop) {
        this.soilScoreDrop = soilScoreDrop;
    }

    public boolean isWeatherScoreDrop() {
        return weatherScoreDrop;
    }

    public void setWeatherScoreDrop(boolean weatherScoreDrop) {
        this.weatherScoreDrop = weatherScoreDrop;
    }

    public boolean isRisk() {
        return risk;
    }

    public void setRisk(boolean risk) {
        this.risk = risk;
    }

    public String getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(String riskScore) {
        this.riskScore = riskScore;
    }
}
