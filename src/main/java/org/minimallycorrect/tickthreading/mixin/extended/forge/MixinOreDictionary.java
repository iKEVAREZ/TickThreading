package org.minimallycorrect.tickthreading.mixin.extended.forge;

import lombok.val;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;
import org.minimallycorrect.mixin.Mixin;
import org.minimallycorrect.mixin.OverrideStatic;

import java.util.*;

@Mixin
public abstract class MixinOreDictionary extends OreDictionary {
	@OverrideStatic
	public static int[] getOreIDs(ItemStack stack) {
		if (stack == null)
			throw new IllegalArgumentException("Stack can not be null!");

		val item = stack.getItem();
		if (item == null)
			throw new IllegalArgumentException("Stack can not be null!");

		val registryName = item.delegate.name();
		if (registryName == null) {
			FMLLog.log(Level.DEBUG, "Attempted to find the oreIDs for an unregistered object (%s). This won't work very well.", new Object[]{stack});
			return new int[0];
		}

		// TODO: cache by registryName. Need to add ability for static constructors

		Set<Integer> set = new HashSet<>();
		int id = GameData.getItemRegistry().getId(registryName);
		List<Integer> ids = stackToId.get(id);
		if (ids != null) {
			set.addAll(ids);
		}

		ids = stackToId.get(id | stack.getItemDamage() + 1 << 16);
		if (ids != null) {
			set.addAll(ids);
		}

		val tmp = set.toArray(new Integer[0]);
		val ret = new int[tmp.length];
		for (int x = 0; x < tmp.length; ++x) {
			ret[x] = tmp[x];
		}

		return ret;
	}
}
