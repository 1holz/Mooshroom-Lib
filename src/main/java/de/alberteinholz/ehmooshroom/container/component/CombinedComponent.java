package de.alberteinholz.ehmooshroom.container.component;

import java.util.Map;
import java.util.NoSuchElementException;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import nerdhub.cardinal.components.api.component.Component;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;

//XXX: is this actually needed?
public class CombinedComponent<T extends Component> {
    protected final Map<Identifier, T> childComps;

    public CombinedComponent(Map<Identifier, T> childComps) {
        this.childComps = childComps;
    }

    public Map<Identifier, T> getComps() {
        return childComps;
    }

    public T getComp(Identifier id) {
        return childComps.get(id);
    }

	public static CompoundTag toTag(CompoundTag tag, String tagKey, Map<Identifier, ? extends Component> childComps) {
        CompoundTag compsTag = new CompoundTag();
        childComps.forEach((id, comp) -> {
            CompoundTag compTag = new CompoundTag();
            comp.toTag(compTag);
            if (!compTag.isEmpty()) compsTag.put(id.toString(), compTag);
        });
        if (!compsTag.isEmpty()) tag.put(tagKey, compsTag);
        return tag;
	}
    
	public static void fromTag(CompoundTag tag, String tagKey, Map<Identifier, ? extends Component> childComps) {
        CompoundTag compsTag = tag.getCompound(tagKey);
        for (String key : compsTag.getKeys()) {
            Identifier id = new Identifier(key);
            if (!compsTag.contains(key, NbtType.COMPOUND) || compsTag.getCompound(key).isEmpty()) continue;
            if (!childComps.containsKey(id)) {
                MooshroomLib.LOGGER.smallBug(new NoSuchElementException("There is no Component with the id " + key + " in a " + tagKey));
                continue;
            }
            CompoundTag compTag = compsTag.getCompound(key);
            childComps.get(id).fromTag(compTag);
        }
	}
}
