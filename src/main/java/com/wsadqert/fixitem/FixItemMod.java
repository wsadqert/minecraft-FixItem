package com.wsadqert.fixitem;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@Mod("fixitem")
@EventBusSubscriber
public class FixItemMod {
	public FixItemMod() {
		FixCommandConfig.load(); // <- Load on mod init
	}

	@SubscribeEvent
	public static void onCommandRegister(RegisterCommandsEvent event) {
		FixItemCommand.register(event.getDispatcher());
		FixCommandToggle.register(event.getDispatcher());
	}
}
