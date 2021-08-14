package de.alberteinholz.ehmooshroom.container.component.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public void addConfig(Identifier id) {
        configs.put(id, new SimpleDataElement(getDefault()));
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

    //it's recommended to add all needed configs before
    @Override
    public void fromTag(CompoundTag tag) {
        if (!tag.contains("Config", NbtType.COMPOUND)) return;
        for (String sId : tag.getCompound("Config").getKeys()) {
            if (!tag.contains(sId, NbtType.STRING) || !tag.getString(sId).matches("^[tfTF]{24}$")) {
                MooshroomLib.LOGGER.smallBug(new IllegalArgumentException("The config string for " + sId + " is malformated and will be ignored."));
                continue;
            }
            Identifier id = new Identifier(sId);
            if (!configs.containsKey(id)) addConfig(id);
            configs.get(id).withLabel(tag.getString(sId));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        CompoundTag configTag = new CompoundTag();
        configs.forEach((id, config) -> {
            if (!isDefault(config.getLabel().asString())) configTag.putString(id.toString(), config.getLabel().asString());
        });
        if (!configTag.isEmpty()) tag.put("Config", configTag);
        return tag;
    }

    public List<Identifier> getIds() {
        return new ArrayList<>(configs.keySet());
    }

    public boolean allowsConfig(Identifier id, ConfigBehavior behavior, Direction dir) {
        return ConfigState.AVAILABLE_TRUE.equals(getConfig(id, behavior, dir));
    }

    public void changeConfig(Identifier id, ConfigBehavior behavior, Direction dir) {
        ConfigState temp = getConfig(id, behavior, dir);
        if (ConfigState.AVAILABLE_TRUE.equals(temp)) temp = ConfigState.AVAILABLE_FALSE;
        else if (ConfigState.AVAILABLE_FALSE.equals(temp)) temp = ConfigState.AVAILABLE_TRUE;
        else MooshroomLib.LOGGER.smallBug(new UnsupportedOperationException("The Config" + id + "probably is not available and couldn't be changed."));
        setConfig(id, behavior, dir, temp);
    }

    //intersection mode
    public void setConfigAvailability(Identifier[] ids, ConfigBehavior[] behaviors, Direction[] dirs, boolean available) {
        if (ids == null) ids = configs.keySet().toArray(new Identifier[configs.size()]);
        if (behaviors == null) behaviors = ConfigBehavior.values();
        if (dirs == null) dirs = Direction.values();
        for (Identifier id : ids) for (ConfigBehavior behavior : behaviors) for (Direction dir : dirs) {
            setConfig(id, behavior, dir, getConfig(id, behavior, dir).setAvailability(available));
        }
    }

    protected char getRawConfig(Identifier id, ConfigBehavior behavior, Direction dir) {
        return configs.get(id).getLabel().asString().charAt(getIndex(behavior, dir));
    }

    public ConfigState getConfig(Identifier id, ConfigBehavior behavior, Direction dir) {
        for (ConfigState state : ConfigState.values()) if (state.c == getRawConfig(id, behavior, dir)) return state;
        return behavior.def;
    }

    public boolean isAvailable(Identifier id, ConfigBehavior behavior, Direction dir) {
        return Character.isLowerCase(getRawConfig(id, behavior, dir));
    }

    public boolean isTrue(Identifier id, ConfigBehavior behavior, Direction dir) {
        return Character.toLowerCase(getRawConfig(id, behavior, dir)) == 't';
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
        for (Direction dir : Direction.values()) for (ConfigBehavior behavior : ConfigBehavior.values()) {
            int index = getIndex(behavior, dir);
            if (str.length() <= index || !behavior.isDefaultChar(str.charAt(index))) ret = false;
        }
        return ret;
    }

    protected static String getDefault() {
        StringBuilder sb = new StringBuilder();
        for (@SuppressWarnings("unused") Direction dir : Direction.values()) for (ConfigBehavior behavior : ConfigBehavior.values()) {
            sb.append(behavior.getDefaultChar());
        }
        return sb.toString();
    }

    public static enum ConfigBehavior {
        SELF_INPUT(ConfigState.AVAILABLE_FALSE),
        SELF_OUTPUT(ConfigState.AVAILABLE_FALSE),
        FOREIGN_INPUT(ConfigState.AVAILABLE_TRUE),
        FOREIGN_OUTPUT(ConfigState.AVAILABLE_TRUE);

        public final ConfigState def;

        private ConfigBehavior(ConfigState def) {
            this.def = def;
        }

        //XXX: remove if this turns out to be unused
        @Deprecated
        public ConfigBehavior next(int amount) {
            return values()[(ordinal() + amount) % values().length];
        }

        public ConfigState getForChar(char c) {
            for (ConfigState state : ConfigState.values()) if (c == state.c) return state;
            return def;
        }

        public boolean isDefaultChar(char c) {
            return getDefaultChar() == c;
        }

        public char getDefaultChar() {
            return def.c;
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

        public ConfigState setAvailability(boolean available) {
            char cTemp = available ? Character.toLowerCase(c) : Character.toUpperCase(c);
            for (ConfigState state : ConfigState.values()) if (cTemp == state.c) return state;
            MooshroomLib.LOGGER.smallBug(new EnumConstantNotPresentException(ConfigState.class, Character.toString(cTemp)));
            return this;
        }
    }
}