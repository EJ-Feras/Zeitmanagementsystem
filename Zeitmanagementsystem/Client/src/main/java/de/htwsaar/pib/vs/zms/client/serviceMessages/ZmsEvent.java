package de.htwsaar.pib.vs.zms.client.serviceMessages;

import lombok.*;

@Getter
public class ZmsEvent {
	
	private long id;
	private Change changetype;
	private Modelclass modelClass;

	public ZmsEvent(long id, Change change, Modelclass modelClass) {
		this.id = id;
		this.changetype = change;
		this.modelClass = modelClass;
	}

}
