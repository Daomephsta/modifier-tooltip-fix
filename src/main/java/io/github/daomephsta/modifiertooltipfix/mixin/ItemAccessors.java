package io.github.daomephsta.modifiertooltipfix.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.Item;

@Mixin(Item.class)
public interface ItemAccessors
{
	@Accessor("ATTACK_DAMAGE_MODIFIER_UUID")
	public static UUID getAttackDamageModifierUuid()
	{
		throw new IllegalStateException("Dummy method body invoked. A critical mixin failure has occured.");
	}

	@Accessor("ATTACK_SPEED_MODIFIER_UUID")
	public static UUID getAttackSpeedModifierUuid()
	{
		throw new IllegalStateException("Dummy method body invoked. A critical mixin failure has occured.");
	}
}
