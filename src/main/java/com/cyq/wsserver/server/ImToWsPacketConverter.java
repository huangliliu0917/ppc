package com.cyq.wsserver.server;


import com.cyq.wsserver.common.ImPacket;
import org.tio.core.ChannelContext;
import org.tio.core.PacketConverter;
import org.tio.core.intf.Packet;

public class ImToWsPacketConverter implements PacketConverter {

	@Override
	public Packet convert(Packet packet, ChannelContext channelContext) {
		if (packet instanceof ImPacket) {
			ImPacket imPacket = (ImPacket) packet;
			return imPacket.toWs();
		}
		return packet;
	}
}
