package com.sahaj.hotelmanagement.util;

import java.util.Comparator;

import com.sahaj.hotelmanagement.entity.Corridor;

public class CorridorLastMovementComparator implements Comparator<Corridor>{
	
	 @Override
    public int compare(Corridor c1, Corridor c2) {
        return c1.getLastMovement().compareTo(c2.getLastMovement());
    }

}
