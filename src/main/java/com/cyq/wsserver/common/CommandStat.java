package com.cyq.wsserver.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CommandStat {

	public final static Map<Command, CommandStat> commandAndCount = new ConcurrentHashMap<>();

	public static CommandStat getCount(Command command) {
		if (command == null) {
			return null;
		}
		CommandStat ret = commandAndCount.get(command);
		if (ret != null) {
			return ret;
		}
		synchronized (commandAndCount) {
			ret = commandAndCount.get(command);
			if (ret != null) {
				return ret;
			}
			ret = new CommandStat();
			commandAndCount.put(command, ret);
		}
		return ret;
	}


	public final AtomicLong received = new AtomicLong();

	public final AtomicLong handled = new AtomicLong();

	public final AtomicLong sent = new AtomicLong();

	public AtomicLong getHandled() {
		return handled;
	}

	public AtomicLong getReceived() {
		return received;
	}

	public AtomicLong getSent() {
		return sent;
	}

}
