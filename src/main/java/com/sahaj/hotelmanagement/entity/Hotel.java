package com.sahaj.hotelmanagement.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Hotel {
	
	private List<Floor> floors;
	
	private String hotelName;
	
	public static class HotelBuilder {
		
		public HotelBuilder setFloors(int noOfFloors,int noOfMainCorridors,int noOfSubCorridors,int maxInactiveTime) {
			this.floors = new ArrayList<>();
			 for (int i = 0 ; i < noOfFloors ; i++){
		            Floor floor = Floor.builder()
		            		.floorId(i+1)
		            		.maxInactiveTime(maxInactiveTime)
		            		.setMainCorridors(noOfMainCorridors)
	            			.setSubCorridors(noOfSubCorridors)
	            			.initialize()
	            			.build();
		            this.floors.add(floor);
			 }
			return this;
		}
		
	}
	
	public void onMovement(int floorId,int subCorridorId){
        System.out.println("Movement in floor"+ floorId + " ,Sub corridor" + subCorridorId );
        Floor floor = floors.get(floorId-1);
        floor.movementDetected(subCorridorId-1);
        this.getStatus();
    }
	
    public void onNoMovement(){
    	for(Floor floor : this.floors){
    		int corridorId = floor.noMovementDetected();
    		if(corridorId != 0) {
            	System.out.println("No Movement in floor" + floor.getFloorId() + ",Sub corridor" + corridorId + "for a minute");
            	this.getStatus();       	
            }
        }           
    }
		
	
	public void getStatus(){
        for(Floor floor : this.floors){
        	System.out.println("********** floor"+ floor.getFloorId() + "*************");
			floor.printFloorStatus();
        }
    }
	
	public Floor getFloor(int floorId) {
        return floors.get(floorId-1);

    }
		

}
