package io.github.daomephsta.modifiertooltipfix.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

@Mixin(EntityAttributeModifier.class)
public class FixModifierUuidEqualityCheck
{	
    /** 
     * For UUIDs that are equals()-equal to either modifier UUID & modify the corresponding attribute, 
     * returns the modifier UUID instance instead. This effectively replaces the == check with an equals() check.
     */
	@ModifyVariable(method = "fromTag", at = @At(value = "INVOKE_ASSIGN", 
	    target = "net/minecraft/nbt/CompoundTag.getUuid(Ljava/lang/String;)Ljava/util/UUID;"))
	private static UUID internAttackModifierUUIDs(UUID uuid, CompoundTag tag)
	{
	    if (ItemAccessors.getAttackDamageModifierUuid().equals(uuid) && 
	        modifiesAttribute(tag, EntityAttributes.GENERIC_ATTACK_DAMAGE))
	    {
	        return ItemAccessors.getAttackDamageModifierUuid();
	    }
	    if (ItemAccessors.getAttackSpeedModifierUuid().equals(uuid) &&
	        modifiesAttribute(tag, EntityAttributes.GENERIC_ATTACK_SPEED))
        {
            return ItemAccessors.getAttackSpeedModifierUuid();
        }
	    return uuid;
	}

	@Unique
    private static boolean modifiesAttribute(CompoundTag tag, EntityAttribute attribute)
    {
        Identifier test = Registry.ATTRIBUTE.getId(attribute);
        String modifies = tag.getString("AttributeName");
        return test.toString().equals(modifies) || 
            (test.getNamespace().equals("minecraft") && test.getPath().equals(modifies));
    }
}