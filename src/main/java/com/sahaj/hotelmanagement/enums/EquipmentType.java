package com.sahaj.hotelmanagement.enums;

public enum EquipmentType {
	AC(10),LIGHT(5);

    int unit;

    EquipmentType(int unit) {
        this.unit = unit;
    }

    public int getUnit(){
        return this.unit;
    }
}
