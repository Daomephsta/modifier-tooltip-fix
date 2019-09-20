package io.github.daomephsta.modifiertooltipfix;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;

public class FixModifierUuidEqualityCheckContext
{
	private LivingEntity player;
	private EntityAttributeModifier modifier;
	private boolean populated;
	
	public void populate(LivingEntity player, EntityAttributeModifier modifier)
	{
		this.player = player;
		this.modifier = modifier;
		this.populated = true;
	}
	
	public LivingEntity player()
	{
		return player;
	}
	
	public EntityAttributeModifier modifier()
	{
		return modifier;
	}

	public boolean isPopulated()
	{
		return populated;
	}
	
	public void clear()
	{
		player = null;
		modifier = null;
		populated = false;
	}
}