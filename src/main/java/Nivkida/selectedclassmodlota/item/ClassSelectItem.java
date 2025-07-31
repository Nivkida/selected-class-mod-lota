package Nivkida.selectedclassmodlota.item;

import Nivkida.selectedclassmodlota.UI.ClassSelectScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ClassSelectItem extends Item {
    public ClassSelectItem(Properties props) {
        super(props);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            // Открываем экран в главном потоке рендеринга
            Minecraft.getInstance().execute(() -> {
                Minecraft.getInstance().setScreen(new ClassSelectScreen(() -> {
                    // Действия после закрытия экрана (если нужны)
                }));
            });
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }
}