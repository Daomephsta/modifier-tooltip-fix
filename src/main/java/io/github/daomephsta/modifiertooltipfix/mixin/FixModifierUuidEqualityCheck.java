package io.github.daomephsta.modifiertooltipfix.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public class FixModifierUuidEqualityCheck
{	
    /** 
     * For UUIDs that are equals()-equal to either modifier UUID, returns the modifier UUID instance instead. 
     * This effectively replaces the == check with an equals() check.
     */
	@Redirect(method = "getTooltip", 
	    at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getId()Ljava/util/UUID;"))
	UUID internAttackModifierUUIDs(EntityAttributeModifier attributeModifier)
	{
	    if (ItemAccessors.getAttackDamageModifierUuid().equals(attributeModifier.getId()))
	        return ItemAccessors.getAttackDamageModifierUuid();
	    if (ItemAccessors.getAttackSpeedModifierUuid().equals(attributeModifier.getId()))
	        return ItemAccessors.getAttackSpeedModifierUuid();
	    return attributeModifier.getId();
	}
}