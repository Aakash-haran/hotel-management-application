package com.sahaj.hotelmanagement.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class HotelTest {
	
	@Test
	public void basicHotelSetUpDefaultState() {
		Hotel hotel = Hotel.builder().hotelName("Hotel 1").setFloors(2,2,2,60).build();
		Assertions.assertEquals("Hotel 1", hotel.getHotelName());
		Assertions.assertEquals(2,hotel.getFloors().size());
		Assertions.assertEquals(true,hotel.getFloor(1).getMainCorridor(1).isAcOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getMainCorridor(1).isLightOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(1).isAcOn());
		Assertions.assertEquals(false,hotel.getFloor(1).getSubCorridor(1).isLightOn());
		Assertions.assertEquals(false,hotel.getFloor(2).getSubCorridor(2).isLightOn());
		Assertions.assertEquals(hotel.getFloor(1).getCurrentPowerConsumption(), hotel.getFloor(1).getMaxPowerConsumption());
	}
	
	@Test
	public void movementandNoMovementDetectionWhenOnOneSubCorridor() {
		Hotel hotel = Hotel.builder().hotelName("Hotel 1").setFloors(1,1,1,10).build();
		Assertions.assertEquals(true,hotel.getFloor(1).getMainCorridor(1).isAcOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getMainCorridor(1).isLightOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(1).isAcOn());
		Assertions.assertEquals(false,hotel.getFloor(1).getSubCorridor(1).isLightOn());
		hotel.onMovement(1, 1);
		/*  max power per floor = number of main corridors * 15 + number of sub corridors * 10
		max power per floor = 1 * 15 + 1 * 10
		Max power per floor = 25
		CurrentPowerConsumption floor 1 = 25
		Floor 1 
		Main Corridor 1 Ac on light on  15
		Sub Corridor 1 Ac on light off  10
		When movement is detected in floor 1, subCorridor 1, Light cannot be switched on since it will go above max power
		hence subCorridor 1 Ac is switched off which will give room for subCorridor Light 1 to be switched on.
		CurrentPowerConsumption floor 1 = 20 
		Floor 1  
		Main Corridor 1 Ac on light on  15
		Sub Corridor 1 Ac off light on  5   */
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(1).isLightOn());
		Assertions.assertEquals(false,hotel.getFloor(1).getSubCorridor(1).isAcOn());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		hotel.onNoMovement();
		/*  CurrentPowerConsumption floor 1 = 25
		Main Corridor 1 Ac on light on  15
		Sub Corridor 1 Ac On light off 10 */
		Assertions.assertEquals(false,hotel.getFloor(1).getSubCorridor(1).isLightOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(1).isAcOn());
		Assertions.assertEquals(hotel.getFloor(1).getMaxPowerConsumption(),hotel.getFloor(1).getCurrentPowerConsumption());
	}
	
	@Test
	public void movementandNoMovementDetectionWhenOnTwoSubCorridors() {
		Hotel hotel = Hotel.builder().hotelName("Hotel 1").setFloors(2,2,2,10).build();
		Assertions.assertEquals(50,hotel.getFloor(1).getCurrentPowerConsumption());
		hotel.onMovement(1, 1);
		/*  max power per floor = number of main corridors * 15 + number of sub corridors * 10
		 	max power per floor = 2 * 15 + 2 * 10
			max power per floor = 50
			CurrentPowerConsumption floor 1 = 50
			Floor 1 
			Main Corridor 1 Ac on light on  15
			Main Corridor 2 Ac on light on  15
			Sub Corridor 1 Ac on light off  10
			Sub Corridor 2 Ac on light off  10
			When movement is detected in floor 1, subCorridor 1, Light cannot be switched on since it will go above max power
			hence subCorridor 2 Ac is switched off which will give room for subCorridor Light 1 to be switched on.
			Main Corridor 1 Ac on light on  15
			Main Corridor 2 Ac on light on  15
			Sub Corridor 1 Ac on light on  15
			Sub Corridor 2 Ac off light off  0 
			CurrentPowerConsumption floor 1 = 50   */
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(1).isAcOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(1).isLightOn());
		Assertions.assertEquals(false,hotel.getFloor(1).getSubCorridor(2).isAcOn());
		Assertions.assertEquals(false,hotel.getFloor(1).getSubCorridor(2).isLightOn());
		Assertions.assertEquals(45,hotel.getFloor(1).getCurrentPowerConsumption());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		hotel.onNoMovement();
		/*  On no movement for one minute on floor 1 subCorridor 1, subCorridor goes back to default state 
		i.e Light of subCorridor 1 is switched off and AC is switched on
		Sub Corridor 2 AC which was switched off for compensating power consumption is reverted back to its default state i.e switched on. */
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(1).isAcOn());
		Assertions.assertEquals(false,hotel.getFloor(1).getSubCorridor(1).isLightOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(2).isAcOn());
		Assertions.assertEquals(false,hotel.getFloor(1).getSubCorridor(2).isLightOn());
		Assertions.assertEquals(hotel.getFloor(1).getMaxPowerConsumption(),hotel.getFloor(1).getCurrentPowerConsumption());
	}
	
	
	@Test
	public void movementandNoMovementDetectionWhenOnManySubCorridorsInterruptionBeforeOneMinute() {
		Hotel hotel = Hotel.builder().hotelName("Hotel 1").setFloors(1,1,4,5).build();
		hotel.onMovement(1, 1);
		hotel.onMovement(1, 3);
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(1).isLightOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(1).isAcOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(3).isLightOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(3).isAcOn());
		try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		hotel.onMovement(1, 1);
		hotel.onNoMovement();
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(1).isLightOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(1).isAcOn());
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		hotel.onNoMovement();
		Assertions.assertEquals(false,hotel.getFloor(1).getSubCorridor(3).isLightOn());
		Assertions.assertEquals(true,hotel.getFloor(1).getSubCorridor(3).isAcOn());
	}

	
	
	
}

