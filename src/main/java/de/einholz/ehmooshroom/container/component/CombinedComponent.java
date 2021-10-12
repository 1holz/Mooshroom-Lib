package de.einholz.ehmooshroom.container.component;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import de.einholz.ehmooshroom.MooshroomLib;
import dev.onyxstudios.cca.api.v3.component.Component;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;

//TODO revise whether this is needed
@Deprecated
public class CombinedComponent<T extends Component> {
    private Map<Identifier, T> childComps = new HashMap<>();

    //XXX: make sure the inputed list only contains objects of the right type
    public CombinedComponent<T> of(Map<Identifier, T> childComps) {
        this.childComps = childComps;
        return this;
    }

    public Map<Identifier, T> getComps() {
        return childComps;
    }

    public T getComp(Identifier id) {
        return childComps.get(id);
    }

	public static void writeNbt(NbtCompound nbt, String keyNbt, Map<Identifier, ? extends Component> childComps) {
        NbtCompound compsNbt = new NbtCompound();
        childComps.forEach((id, comp) -> {
            NbtCompound nbtComp = new NbtCompound();
            comp.writeToNbt(nbtComp);
            if (!nbtComp.isEmpty()) compsNbt.put(id.toString(), nbtComp);
        });
        if (!compsNbt.isEmpty()) nbt.put(keyNbt, compsNbt);
	}
    
	public static void readNbt(NbtCompound nbt, String keyNbt, Map<Identifier, ? extends Component> childComps) {
        NbtCompound compsNbt = nbt.getCompound(keyNbt);
        for (String key : compsNbt.getKeys()) {
            Identifier id = new Identifier(key);
            if (!compsNbt.contains(key, NbtType.COMPOUND) || compsNbt.getCompound(key).isEmpty()) continue;
            if (!childComps.containsKey(id)) {
                MooshroomLib.LOGGER.smallBug(new NoSuchElementException("There is no Component with the id " + key + " in a " + keyNbt));
                continue;
            }
            NbtCompound nbtComp = compsNbt.getCompound(key);
            childComps.get(id).readFromNbt(nbtComp);
        }
	}
}
