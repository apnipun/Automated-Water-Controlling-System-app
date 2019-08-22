package com.app.vortex.vortex.app;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Kasun on 12/3/2016.
 */

public class SystemInfo implements Parcelable{
    protected SystemInfo(Parcel in) {
        tankLevel = in.readInt();
        motorStatus = parseFromString(in.readString());
    }

    public static final Creator<SystemInfo> CREATOR = new Creator<SystemInfo>() {
        @Override
        public SystemInfo createFromParcel(Parcel in) {
            return new SystemInfo(in);
        }

        @Override
        public SystemInfo[] newArray(int size) {
            return new SystemInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(tankLevel);
        dest.writeString(motorStatus.name());
    }

    public enum MotorStatus{
        on,off,unknown,
    }

    private int tankLevel;
    private MotorStatus motorStatus;

    public SystemInfo() {
    }

    public SystemInfo(int tankLevel, MotorStatus motorStatus) {
        this.tankLevel = tankLevel;
        this.motorStatus = motorStatus;
    }


    public int getTankLevel() {
        return tankLevel;
    }

    public void setTankLevel(int tankLevel) {
        this.tankLevel = tankLevel;
    }

    public MotorStatus getMotorStatus() {
        return motorStatus;
    }

    public void setMotorStatus(MotorStatus motorStatus) {
        this.motorStatus = motorStatus;
    }

    public static MotorStatus parseFromString(String s){
        MotorStatus status;

        if(s.equals("on"))
            status = MotorStatus.on;
        else if(s.equals("off"))
            status = MotorStatus.off;
        else
            status = MotorStatus.unknown;
        return status;
    }
}
