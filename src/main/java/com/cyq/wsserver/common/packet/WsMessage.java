package com.cyq.wsserver.common.packet;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class WsMessage implements Serializable{
	public String action ;
	public String channelCode;



	public WsMessage(String action){
		this.action = action;
	}

}