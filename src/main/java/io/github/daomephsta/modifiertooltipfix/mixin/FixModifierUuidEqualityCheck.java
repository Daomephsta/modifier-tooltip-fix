package io.github.daomephsta.modifiertooltipfix.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin(ItemStack.class)
public class FixModifierUuidEqualityCheck
{	
    /** 
     * For UUIDs that are equals()-equal to either modifier UUID & modify the corresponding attribute, 
     * returns the modifier UUID instance instead. This effectively replaces the == check with an equals() check.
     */
	@Redirect(method = "getTooltip", 
	    at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier;getId()Ljava/util/UUID;"))
	UUID internAttackModifierUUIDs(EntityAttributeModifier attributeModifier)
	{
	    if (ItemAccessors.getAttackDamageModifierUuid().equals(attributeModifier.getId()) && 
	        modifiesAttribute(attributeModifier, EntityAttributes.GENERIC_ATTACK_DAMAGE))
	    {
	        return ItemAccessors.getAttackDamageModifierUuid();
	    }
	    if (ItemAccessors.getAttackSpeedModifierUuid().equals(attributeModifier.getId()) &&
	        modifiesAttribute(attributeModifier, EntityAttributes.GENERIC_ATTACK_SPEED))
        {
            return ItemAccessors.getAttackSpeedModifierUuid();
        }
	    return attributeModifier.getId();
	}

	@Unique
    private boolean modifiesAttribute(EntityAttributeModifier attributeModifier, EntityAttribute attribute)
    {
        Identifier id = Registry.ATTRIBUTE.getId(attribute);
        return id.toString().equals(attributeModifier.getName()) || 
            (id.getNamespace().equals("minecraft") && id.getPath().equals(attributeModifier.getName()));
    }
}