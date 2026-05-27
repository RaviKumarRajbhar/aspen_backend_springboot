package com.example.aspen.Dto;

import com.example.aspen.Entities.DeviceType;

public class RegisterDeviceRequest {

    private String token;

    private DeviceType type;

    private String deviceName;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
