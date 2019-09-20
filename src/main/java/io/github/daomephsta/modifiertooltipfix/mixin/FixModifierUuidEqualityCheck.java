package io.github.daomephsta.modifiertooltipfix.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.google.common.collect.Multimap;

import io.github.daomephsta.modifiertooltipfix.FixModifierUuidEqualityCheckContext;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

@Mixin(ItemStack.class)
public class FixModifierUuidEqualityCheck
{
	private static final UUID ATTACK_DAMAGE_MODIFIER_UUID,
							  ATTACK_SPEED_MODIFIER_UUID;
	private static final ThreadLocal<FixModifierUuidEqualityCheckContext> polar_equalityCheckContext = ThreadLocal.withInitial(FixModifierUuidEqualityCheckContext::new);
	static
	{
		MappingResolver mappingResolver = FabricLoader.getInstance().getMappingResolver();
		String descriptor = "Ljava/util/UUID;";
		try
		{
			ATTACK_DAMAGE_MODIFIER_UUID = (UUID) Item.class.getDeclaredField(mappingResolver.mapFieldName("intermediary", "net.minecraft.class_1792", "field_8006", descriptor)).get(null);
			ATTACK_SPEED_MODIFIER_UUID  = (UUID) Item.class.getDeclaredField(mappingResolver.mapFieldName("intermediary", "net.minecraft.class_1792", "field_8001", descriptor)).get(null);
		} 
		catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Inject(method = "getTooltip",
			at = @At(value = "FIELD", target = "Lnet/minecraft/item/Item;ATTACK_DAMAGE_MODIFIER_UUID:Ljava/util/UUID;"),
			locals = LocalCapture.CAPTURE_FAILHARD)
	public void polar_populateFixContext(PlayerEntity player, TooltipContext tooltipContext, CallbackInfoReturnable<List<Text>> info, List<?> _1, int _2, EquipmentSlot[] _3, int _4, int _5, EquipmentSlot _6, Multimap<?, ?> _7, Iterator<?> _8, Map.Entry<?, ?> _9, EntityAttributeModifier modifier)
	{
		polar_equalityCheckContext.get().populate(player, modifier);
	}

	@Inject(method = "getTooltip", at = @At(value = "RETURN"))
	public void polar_clearFixContext(PlayerEntity player, TooltipContext tooltipContext, CallbackInfoReturnable<List<Text>> info)
	{
		polar_equalityCheckContext.get().clear();
	}


	@ModifyVariable(method = "getTooltip", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;MULTIPLY_BASE:Lnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;"))
	public boolean polar_modifySpecialCase(boolean specialCase)
	{
		FixModifierUuidEqualityCheckContext context = polar_equalityCheckContext.get();
		if (!context.isPopulated()) return specialCase;
		return context.modifier().getId().equals(ATTACK_DAMAGE_MODIFIER_UUID) 
				|| context.modifier().getId().equals(ATTACK_SPEED_MODIFIER_UUID);
	}

	@ModifyVariable(method = "getTooltip", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;MULTIPLY_BASE:Lnet/minecraft/entity/attribute/EntityAttributeModifier$Operation;"))
	public double polar_modifyAmount(double amount)
	{	
		FixModifierUuidEqualityCheckContext context = polar_equalityCheckContext.get();
		if (!context.isPopulated() || context.player() == null) return amount;
		if (context.modifier().getId() != ATTACK_DAMAGE_MODIFIER_UUID && context.modifier().getId().equals(ATTACK_DAMAGE_MODIFIER_UUID)) 
		{
			return amount + context.player().getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getBaseValue()
					+ EnchantmentHelper.getAttackDamage((ItemStack) (Object) this, EntityGroup.DEFAULT);
		} 
		else if (context.modifier().getId() != ATTACK_SPEED_MODIFIER_UUID && context.modifier().getId().equals(ATTACK_SPEED_MODIFIER_UUID))
			return amount + context.player().getAttributeInstance(EntityAttributes.ATTACK_SPEED).getBaseValue();
		return amount;
	}
}
