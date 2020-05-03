package com.league.lugas.shop.system;

import com.league.lugas.shop.events.shop.ShopClickEventListener;
import com.league.lugas.shop.events.shop.ShopOpenEventListener;

public class ListenerExcuter {
	
	public ListenerExcuter() {
		new ShopOpenEventListener();
		new ShopClickEventListener();
	}
	
}
