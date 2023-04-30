package de.einholz.ehmooshroom.recipe.deprecated;

import de.einholz.ehmooshroom.recipe.Ingredient;
import net.minecraft.network.PacketByteBuf;

@Deprecated
public class Input {
    public final Ingredient<?>[] ingredients;

    public Input(Ingredient<?>... ingredients) {
        this.ingredients = ingredients;
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(ingredients.length);
        for (Ingredient<?> ingredient : ingredients) ingredient.write(buf);
    }

    public static Input read(PacketByteBuf buf) {
        Ingredient<?>[] ingredients = new Ingredient[buf.readInt()];
        for (int i = 0; i < ingredients.length; i++) ingredients[i] = Ingredient.read(buf);
        return new Input(ingredients);
    }
}
