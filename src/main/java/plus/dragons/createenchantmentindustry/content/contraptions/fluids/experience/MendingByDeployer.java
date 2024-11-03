package plus.dragons.createenchantmentindustry.content.contraptions.fluids.experience;

import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import org.jetbrains.annotations.NotNull;

public class MendingByDeployer {
    public static boolean canItemBeMended(ItemStack stack) {
        return false; // 禁用经验修补
    }

    public static int getRequiredAmountForItem(ItemStack stack) {
		// Hard coded, not sure if fabric has a replacement sadly
        return Mth.ceil(stack.getDamageValue() / 2f);
    }
    public static int getNewXp(int xpAmount, ItemStack stack) {
        int requiredAmount = getRequiredAmountForItem(stack);
        int afterXp = 0;

        if(requiredAmount % 2 != 0) {
            requiredAmount -= 1;
        }

        if(requiredAmount == 1) {
            afterXp = xpAmount;
        }

        else if(requiredAmount > 1 && requiredAmount < xpAmount) {
            afterXp = xpAmount - requiredAmount;
        }

        return afterXp;
    }

    public static @NotNull ItemStack mendItem(int xpAmount, ItemStack stack) {
		int requiredAmount = getRequiredAmountForItem(stack);
		int damage = stack.getDamageValue();
		if(requiredAmount % 2 != 0) {
			requiredAmount -= 1;
		}
		damage -= xpAmount * 2;
		stack.setDamageValue(damage);
		return stack;
    }
}
