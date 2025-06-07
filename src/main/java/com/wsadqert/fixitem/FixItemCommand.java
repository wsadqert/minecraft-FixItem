package com.wsadqert.fixitem;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class FixItemCommand {
	public static void sendMessage(CommandContext<CommandSourceStack> ctx, Component component) {
		ctx.getSource()
				.getPlayer()
				.sendSystemMessage(Component.literal("[FixItem] ")
						.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x888888))) // gray
																						// prefix
						.append(component));
	}

	public static void sendError(CommandContext<CommandSourceStack> ctx, MutableComponent component) {
		sendMessage(ctx, component.withStyle(
				Style.EMPTY.withColor(TextColor.fromRgb(0xFF0000))));
	}

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(
				Commands.literal("fix")
						.requires(cs -> cs.hasPermission(2)) // OP level 2
						.executes(ctx -> {
							if (!FixCommandConfig.isEnabled()) {
								sendError(ctx, Component.literal("Fix commands are currently disabled."));
								return 0;
							}

							ServerPlayer player = ctx.getSource().getPlayerOrException();
							ItemStack item = player.getMainHandItem();

							if (!item.isEmpty() && item.isDamageableItem()) {
								item.setDamageValue(0);
								sendMessage(ctx, item.getHoverName().copy()
										.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00)))
										.append(" has been repaired!"));
							} else {
								sendError(ctx, Component.literal("Item is not damageable!"));
							}
							return 0;
						}));

		dispatcher.register(
				Commands.literal("setdurabilitypercent")
						.then(Commands.argument("percent", FloatArgumentType.floatArg(0.0f, 100.0f))
								.executes(ctx -> {
									if (!FixCommandConfig.isEnabled()) {
										sendError(ctx, Component.literal("Fix commands are currently disabled."));
										return 0;
									}

									ServerPlayer player = ctx.getSource().getPlayerOrException();
									ItemStack item = player.getMainHandItem();

									if (!item.isEmpty() && item.isDamageableItem()) {
										float percent = FloatArgumentType.getFloat(ctx, "percent") / 100;
										int damage = (int) (item.getMaxDamage() * (1.0f - percent));

										item.setDamageValue(damage);

										sendMessage(ctx, Component.literal("Set ")
												.append(item.getHoverName().copy()
														.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))))
												.append(" durability to " + percent * 100 + "% ("
														+ item.getMaxDamage() * percent + "/" + item.getMaxDamage()
														+ ")"));

										// ⚠️ Must sync to client or client will revert the damage
										player.inventoryMenu.broadcastChanges();
									}
									return 0;
								})));
		dispatcher.register(
				Commands.literal("setdurability")
						.then(Commands.argument("durability", IntegerArgumentType.integer(0))
								.executes(ctx -> {
									if (!FixCommandConfig.isEnabled()) {
										sendError(ctx, Component.literal("Fix commands are currently disabled."));
										return 0;
									}

									ServerPlayer player = ctx.getSource().getPlayerOrException();
									ItemStack item = player.getMainHandItem();

									if (!item.isEmpty() && item.isDamageableItem()) {
										int durability = IntegerArgumentType.getInteger(ctx, "durability");
										float percent = (float) durability / item.getMaxDamage();

										item.setDamageValue(item.getMaxDamage() - durability);

										sendMessage(ctx, Component.literal("Set ")
												.append(item.getHoverName().copy()
														.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(0x00FF00))))
												.append(" durability to " + percent * 100 + "% ("
														+ durability + "/" + item.getMaxDamage()
														+ ")"));
										
										player.inventoryMenu.broadcastChanges();
									}
									return 0;
								})));
	}
}
