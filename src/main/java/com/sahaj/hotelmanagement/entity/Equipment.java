package com.sahaj.hotelmanagement.entity;

import com.sahaj.hotelmanagement.enums.EquipmentState;
import com.sahaj.hotelmanagement.enums.EquipmentType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Equipment {
	
	private EquipmentType equipmentType;
	
	private EquipmentState equipmentState;
	
	public static class EquipmentBuilder {
		
		public EquipmentBuilder setEquipment(EquipmentType type,EquipmentState state) {
			this.equipmentType = type;
			this.equipmentState = state;
			return this;		
		}
	}
	
	public void equipmentStateOn(){
        this.equipmentState = EquipmentState.ON;
    }

    public void equipmentStateOff(){
        this.equipmentState = EquipmentState.OFF;
    }


    public boolean isEquipmentOn(){
       return  EquipmentState.ON.equals(this.getEquipmentState());
    }


    public static int getEquipmentUnit(EquipmentType equipmentType){
        return equipmentType.getUnit();
    }
	
	
	

}
