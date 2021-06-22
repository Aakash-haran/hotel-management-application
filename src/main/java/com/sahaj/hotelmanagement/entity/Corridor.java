package com.sahaj.hotelmanagement.entity;

import java.time.Instant;
import java.util.Date;

import com.sahaj.hotelmanagement.enums.EquipmentState;
import com.sahaj.hotelmanagement.enums.EquipmentType;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Corridor {
	
		private int corridorId;
			
		private Equipment ac;
		
		private Equipment light;
		
		private Instant lastMovement;
		
		public static class CorridorBuilder {
			
			public CorridorBuilder setAc(EquipmentState state) {
				this.ac =  Equipment.builder().setEquipment(EquipmentType.AC,state).build();
				return this;
			}
			
			public CorridorBuilder setLight(EquipmentState state) {
				this.light = Equipment.builder().setEquipment(EquipmentType.LIGHT,state).build();
				return this;
			}
		}
		
		public void turnAcOn() {
			this.ac.equipmentStateOn();
		}

	    public void turnAcOff(){
	        this.ac.equipmentStateOff();
	    }

	    public void turnLightOn(){
	        this.light.equipmentStateOn();
	    }

	    public void turnLightOff(){
	        this.light.equipmentStateOff();
	    }

	    public  boolean isAcOn(){
	        return this.ac.isEquipmentOn();
	    }

	    public  boolean isLightOn(){
	        return this.light.isEquipmentOn();
	    }
	    
	    public EquipmentState getLightState(){
	        return this.light.getEquipmentState();
	    }

	    public  EquipmentState getAcState(){
	        return this.ac.getEquipmentState();
	    }
	    
	    public void movementDetected() {
	    	lastMovement= Instant.now();
	    }
	    
	    public boolean isNoMovement(int maxInactiveTime){
	        return  Instant.now().minusSeconds(maxInactiveTime).isAfter(lastMovement);
	    }
	    
	    

}
