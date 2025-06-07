package com.wsadqert.fixitem;

import com.mojang.brigadier.CommandDispatcher;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class FixCommandToggle {
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
			Commands.literal("fixcommands")
				.requires(cs -> cs.hasPermission(4)) // OP-only
				.then(Commands.literal("allow")
					.executes(ctx -> {
						FixCommandConfig.setEnabled(true);
						ctx.getSource().sendSuccess(() -> Component.literal("Fix commands enabled."), true);
						return 1;
					}))
				.then(Commands.literal("deny")
					.executes(ctx -> {
						FixCommandConfig.setEnabled(false);
						ctx.getSource().sendSuccess(() -> Component.literal("Fix commands disabled."), true);
						return 1;
					}))
		);
	}
}
