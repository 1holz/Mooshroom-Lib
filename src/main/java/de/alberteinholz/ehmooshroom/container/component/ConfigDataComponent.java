package de.alberteinholz.ehmooshroom.container.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import de.alberteinholz.ehmooshroom.MooshroomLib;
import io.github.cottonmc.component.data.DataProviderComponent;
import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import io.github.cottonmc.component.data.impl.SimpleDataElement;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ConfigDataComponent implements DataProviderComponent {
    protected final Map<Identifier, SimpleDataElement> configs = new HashMap<>();
    /*
    public SimpleDataElement configItem = new SimpleDataElement("xxxxxxxxxxxxxxxxxxxxxxxx");
    public SimpleDataElement configFluid = new SimpleDataElement("xxxxxxxxxxxxxxxxxxxxxxxx");
    public SimpleDataElement configPower = new SimpleDataElement("xxxxxxxxxxxxxxxxxxxxxxxx");
    */

    public void addConfig(Identifier... ids) {
        for (Identifier id : ids) configs.put(id, new SimpleDataElement(getDefault()));
    }

    public void removeConfig(Identifier id) {
        configs.remove(id);
    }

    @Override
    public void provideData(List<DataElement> data) {
        configs.forEach((id, config) -> data.add(config));
    }

    @Override
    public DataElement getElementFor(Unit unit) {
        return null;
    }

    //it's recommended to add all needed configs befor
    @Override
    public void fromTag(CompoundTag tag) {
        for (String id : tag.getCompound("Config").getKeys()) {
            
        }
        /*
        if (tag.contains("ItemConfig", NbtType.STRING)) configItem.withLabel(tag.getString("ItemConfig"));
        if (tag.contains("FluidConfig", NbtType.STRING)) configFluid.withLabel(tag.getString("FluidConfig"));
        if (tag.contains("PowerConfig", NbtType.STRING)) configPower.withLabel(tag.getString("PowerConfig"));
        */
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag configTag = new CompoundTag();
        configs.forEach((id, config) -> {
            if (!isDefault(config.getLabel().asString())) configTag.putString(id.toString(), config.getLabel().asString());
        });
        /*
        if (configItem.getLabel().asString() != "xxxxxxxxxxxxxxxxxxxxxxxx") tag.putString("ItemConfig", configItem.getLabel().asString());
        if (configFluid.getLabel().asString() != "xxxxxxxxxxxxxxxxxxxxxxxx") tag.putString("FluidConfig", configFluid.getLabel().asString());
        if (configPower.getLabel().asString() != "xxxxxxxxxxxxxxxxxxxxxxxx") tag.putString("PowerConfig", configPower.getLabel().asString());
        */
        tag.put("Config", configTag);
        return tag;
    }

    public boolean allowsConfig(Identifier id, ConfigBehavior behavior, Direction dir) {
        return Boolean.TRUE.equals(getConfig(type, behavior, dir));
    }

    public void changeConfig(Identifier id, ConfigBehavior behavior, Direction dir) {
        Boolean bl = getConfig(type, behavior, dir);
        if (bl != null) setConfig(type, behavior, dir, !bl);
    }

    //intersection mode
    public void setConfigAvailabilityIntersecting(Identifier[] ids, ConfigBehavior[] behaviors, Direction[] dirs, boolean available) {
        if (ids == null) ids = configs.keySet().toArray(new Identifier[configs.size()]);
        if (behaviors == null) behaviors = ConfigBehavior.values();
        if (dirs == null) dirs = Direction.values();
        for (Identifier cId : ids) for (ConfigBehavior cBehavior : behaviors) for (Direction cDir : dirs) {

            setConfig(cId, cBehavior, cDir, Character.toLowerCase(cBehavior.getForChar(getConfig(cId, cBehavior, cDir).c)));
        }
    }

    protected void setConfigAvailability(Identifier id, ConfigBehavior behavior, Direction dir, boolean available) {
        if (available) getConfig(id, behavior, dir).c
    }

    public ConfigState getConfig(Identifier id, ConfigBehavior behavior, Direction dir) {
        for (ConfigState state : ConfigState.values()) if (state.c == configs.get(id).getLabel().asString().charAt(getIndex(behavior, dir))) return state;
        return behavior.DEF;
    }

    protected void setConfig(Identifier id, ConfigBehavior behavior, Direction dir, ConfigState state) {
        char[] chars = configs.get(id).getLabel().asString().toCharArray();
        chars[getIndex(behavior, dir)] = state.c;
        configs.get(id).withLabel(new String(chars));
    }

    protected static int getIndex(ConfigBehavior behavior, Direction dir) {
        return dir.getId() * ConfigBehavior.values().length + behavior.ordinal();
    }

    protected static boolean isDefault(String str) {
        boolean ret = true;
        iterateConfig((dir, behavior) -> {
            int index = getIndex(behavior, dir);
            if (str.length() <= index || !behavior.isDefaultChar(str.charAt(index))) ret = false;
        });
        return ret;
    }

    protected static String getDefault() {
        StringBuilder sb = new StringBuilder();
        iterateConfig((dir, behavior) -> {
            sb.append(behavior.getDefaultChar());
        });
        return sb.toString();
    }

    protected static void iterateConfig(BiConsumer<Direction, ConfigBehavior> consumer) {
        for (Direction dir : Direction.values()) for (ConfigBehavior behavior : ConfigBehavior.values()) consumer.accept(dir, behavior);
    }

    public static enum ConfigBehavior {
        SELF_INPUT(ConfigState.AVAILABLE_FALSE),
        SELF_OUTPUT(ConfigState.AVAILABLE_FALSE),
        FOREIGN_INPUT(ConfigState.AVAILABLE_TRUE),
        FOREIGN_OUTPUT(ConfigState.AVAILABLE_TRUE);

        public final ConfigState DEF;

        private ConfigBehavior(ConfigState DEF) {
            this.DEF = DEF;
        }

        //XXX: remove if this turns out to be unused
        @Deprecated
        public ConfigBehavior next(int amount) {
            return values()[(ordinal() + amount) % values().length];
        }

        public ConfigState getForChar(char c) {
            for (ConfigState state : ConfigState.values()) if (c == state.c) return state;
            return DEF;
        }

        public boolean isDefaultChar(char c) {
            return getDefaultChar() == c;
        }

        public char getDefaultChar() {
            return DEF.c;
        }
    }

    public static enum ConfigState {
        AVAILABLE_TRUE('t'),
        AVAILABLE_FALSE('f'),
        RESTRICTED_TRUE('T'),
        RESTRICTED_FALSE('F');

        public final char c;

        private ConfigState(char c) {
            this.c = c;
        }

        public static ConfigState setAvailability(ConfigState state, boolean available) {

        }
    }
}