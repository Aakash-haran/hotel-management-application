package com.sahaj.hotelmanagement.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Predicate;

import com.sahaj.hotelmanagement.enums.EquipmentState;
import com.sahaj.hotelmanagement.enums.EquipmentType;
import com.sahaj.hotelmanagement.util.CorridorLastMovementComparator;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Floor {
			
			private int floorId;
	    	private int maxInactiveTime;
		    private List<Corridor> mainCorridors;
		    private List<Corridor> subCorridors;
		    private int maxPowerConsumption;
		    private int currentPowerConsumption;
		    private Map<Corridor,Corridor> lightsOnAcOffMap;
	    	
	    	public static class FloorBuilder {

				public FloorBuilder setMainCorridors(int noOfMainCorridors) {
					this.mainCorridors = new ArrayList<>();
			        for(int mainCorridor= 0; mainCorridor< noOfMainCorridors; mainCorridor++){
			        	this.mainCorridors.add(Corridor.builder().corridorId(mainCorridor+1).setAc(EquipmentState.ON).setLight(EquipmentState.ON).build());
			        }
		    		return this;
		    	}
		    	
				public FloorBuilder setSubCorridors(int noOfSubCorridors) {
					this.subCorridors = new ArrayList<>();
			        for(int subCorridor= 0; subCorridor< noOfSubCorridors; subCorridor++){
			        	this.subCorridors.add(Corridor.builder().corridorId(subCorridor+1).setAc(EquipmentState.ON).setLight(EquipmentState.OFF).build());
			        }
		    		return this;
		    	}
		    	
		    	public FloorBuilder initialize() {
		    		this.maxPowerConsumption = this.mainCorridors.size() * (Equipment.getEquipmentUnit(EquipmentType.AC) + Equipment.getEquipmentUnit(EquipmentType.LIGHT)) + ( this.subCorridors.size() * Equipment.getEquipmentUnit(EquipmentType.AC) );
		    		this.currentPowerConsumption = this.maxPowerConsumption;
		    		/*This map maintains the switched on lights and switched off Ac for power compensation
		    		 This map will be checked every minute to check for no movement and revert the no movement corridor back to default state
		    		 */
		    		this.lightsOnAcOffMap = new TreeMap<>(new CorridorLastMovementComparator());
		    		return this;
		    	}
	    	}
	    	
	    	
	    	
	    	public void movementDetected(int corridorId){
	           Corridor corridor =  this.subCorridors.get(corridorId);
	           corridor.movementDetected();
	           turnLightOn(corridor);
	           turnAcOn(corridor);            
    		}
	    	
	    	private boolean checkMaxPowerConsumption(EquipmentType type) {
	    		//If currentPowerConsumption + equipment to be switched on for the movement detected corridor exceeds the maxPowerLimit.
	    		return currentPowerConsumption+ Equipment.getEquipmentUnit(type) > maxPowerConsumption;
	    		
	    	}
	    	
	    	private Corridor turnOffOtherCorridorAc(Corridor inputCorridor) {	    		
	    		Predicate<Corridor> otherCor = cor -> cor.getCorridorId() != inputCorridor.getCorridorId();
	    		Predicate<Corridor> otherCorAcOn = acOnCor -> acOnCor.isAcOn();
	    		Optional<Corridor> otherCorridor = subCorridors.stream().filter(otherCor.and(otherCorAcOn)).findFirst();
	    		if(otherCorridor.isPresent()) {
	    			turnAcOff(otherCorridor.get());
	    			return otherCorridor.get();
	    		}else {
	    			turnAcOff(inputCorridor);
	    			return inputCorridor;
	    		}
	    		
	    	}
	    	
	    	private void turnLightOn(Corridor corridor) {
	    		boolean otherAcOff = false;
	    		 if(!corridor.isLightOn()) {
	    			 if(checkMaxPowerConsumption(EquipmentType.LIGHT)) {
	    				 Corridor corridorCheck = turnOffOtherCorridorAc(corridor);
	    				 if(corridorCheck.getCorridorId() != corridor.getCorridorId()) {
	    					 otherAcOff = true;
	    					 lightsOnAcOffMap.put(corridor,corridorCheck); 
	    				 }
	    			 }
    	            currentPowerConsumption += Equipment.getEquipmentUnit(EquipmentType.LIGHT);
    	            corridor.turnLightOn();
    	            if(!otherAcOff)
    	            lightsOnAcOffMap.put(corridor,corridor); 
    	        }
	    	}
	    	
	    	private void turnLightOff(Corridor corridor) {
	    		 if(corridor.isLightOn()) {
    	            currentPowerConsumption -= Equipment.getEquipmentUnit(EquipmentType.LIGHT);
    	            corridor.turnLightOff();
    	        }
	    		
	    	}
	    	
	    	private void turnAcOn(Corridor corridor){
	            if(!corridor.isAcOn()) {
	            	if(checkMaxPowerConsumption(EquipmentType.AC)) {
	    				 Corridor corridorCheck = turnOffOtherCorridorAc(corridor);
	    				 if(corridorCheck.getCorridorId() != corridor.getCorridorId()) {
	    					 currentPowerConsumption += Equipment.getEquipmentUnit(EquipmentType.AC);
	    					 corridor.turnAcOn(); 
	    				 }
	    			 }else {
	    				 currentPowerConsumption += Equipment.getEquipmentUnit(EquipmentType.AC);
    					 corridor.turnAcOn();
	    			 }
	            }	
	        }
	    	
	    	private void turnAcOff(Corridor corridor) {
	    		if(corridor.isAcOn()) {
	                currentPowerConsumption -= Equipment.getEquipmentUnit(EquipmentType.AC);
	                corridor.turnAcOff();
	            }
	    	}
	    	
	    	
	    	public int noMovementDetected(){    		
	    		while(lightsOnAcOffMap.size() != 0) {
	    			for(Corridor corridor:lightsOnAcOffMap.keySet()) {
		    			if(corridor.isNoMovement(this.maxInactiveTime)) {	
		    				changeToDefaultState(corridor);
		    				return corridor.getCorridorId();
		    			}
		    		}
	    		}
	    		return 0;	    		
	    	}
	    	
	    	private void changeToDefaultState(Corridor corridor) {
	    		turnLightOff(corridor);
	    		Corridor corridorAcOff = lightsOnAcOffMap.get(corridor);
	    		if(corridorAcOff != null) {
	    			turnAcOn(corridorAcOff);
	    			lightsOnAcOffMap.remove(corridor);
	    		}    		
	    	}
	    	
	    	public void printFloorStatus() {
	            System.out.println("Maximum Power Consumption per floor : "+maxPowerConsumption+" Current Power Consumption per floor : "+ currentPowerConsumption);
	            for(Corridor mainCorridor : mainCorridors){            	
	                System.out.println("Main corridor" + mainCorridor.getCorridorId() + "Light: " + mainCorridor.getLightState() + "AC: " + mainCorridor.getAcState());
	            }
	            for(Corridor subCorridor : subCorridors){
	            	System.out.println("Sub corridor" + subCorridor.getCorridorId() + "Light: " + subCorridor.getLightState() + "AC: " + subCorridor.getAcState());
	            }
	        }
	    	
	    	public Corridor getMainCorridor(int mainCorridorId) {
                return mainCorridors.get(mainCorridorId-1);
	        }
	    	
	    	public Corridor getSubCorridor(int subCorridorId) {
                return subCorridors.get(subCorridorId-1);
	        }

}
