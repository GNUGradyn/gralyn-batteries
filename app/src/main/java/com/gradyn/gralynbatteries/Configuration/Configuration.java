package com.gradyn.gralynbatteries.Configuration;

public class Configuration {
    public Configuration(Boolean batteryReporting, String apiRoot, String accessCode) {
        BatteryReporting = batteryReporting;
        ApiRoot = apiRoot;
        AccessCode = accessCode;
    }

    public Boolean getBatteryReporting() {
        return BatteryReporting;
    }
    public void setBatteryReporting(Boolean batteryReporting) {
        BatteryReporting = batteryReporting;
    }
    Boolean BatteryReporting;

    public String getApiRoot() {
        return ApiRoot;
    }
    public void setApiRoot(String apiRoot) {
        ApiRoot = apiRoot;
    }
    String ApiRoot;

    public String getAccessCode() {
        return AccessCode;
    }
    public void setAccessCode(String accessCode) {
        AccessCode = accessCode;
    }
    String AccessCode;
}
