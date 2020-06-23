package de.alberteinholz.ehtech.blocks.components.container.machine;

import java.util.List;
import java.util.Optional;

import de.alberteinholz.ehtech.TechMod;
import de.alberteinholz.ehtech.blocks.components.container.ContainerDataProviderComponent;
import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import io.github.cottonmc.component.data.api.UnitManager;
import io.github.cottonmc.component.data.impl.SimpleDataElement;
import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MachineDataProviderComponent extends ContainerDataProviderComponent {
    public SimpleDataElement activationState = new SimpleDataElement(ActivationState.values()[0].name());
    public SimpleDataElement configItem = new SimpleDataElement("xxxxxxxxxxxxxxxxxxxxxxxx");
    public SimpleDataElement configFluid = new SimpleDataElement("xxxxxxxxxxxxxxxxxxxxxxxx");
    public SimpleDataElement configPower = new SimpleDataElement("xxxxxxxxxxxxxxxxxxxxxxxx");
    public SimpleDataElement efficiency = new SimpleDataElement(String.valueOf(1.0));
    public SimpleDataElement progress = new SimpleDataElement().withBar(0.0, 0.0, 100.0, UnitManager.PERCENT);
    public SimpleDataElement recipe = new SimpleDataElement((Text) null);
    //in percent per tick * fuelSpeed
    public SimpleDataElement speed = new SimpleDataElement(String.valueOf(1.0));

    public MachineDataProviderComponent(String name) {
        super(name);
    }

    @Override
    public void provideData(List<DataElement> data) {
        data.add(activationState);
        data.add(configItem);
        data.add(configFluid);
        data.add(configPower);
        data.add(efficiency);
        data.add(progress);
        data.add(recipe);
        data.add(speed);
    }

    @Override
    public DataElement getElementFor(Unit unit) {
        if (unit == progress.getBarUnit()) {
            return progress;
        } else {
            return super.getElementFor(unit);
        }
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        if (tag.contains("ActivationState", NbtType.STRING)) {
            setActivationState(tag.getString("ActivationState"));
        }
        if (tag.contains("ItemConfig", NbtType.STRING)) {
            configItem.withLabel(tag.getString("ItemConfig"));
        }
        if (tag.contains("FluidConfig", NbtType.STRING)) {
            configFluid.withLabel(tag.getString("FluidConfig"));
        }
        if (tag.contains("PowerConfig", NbtType.STRING)) {
            configPower.withLabel(tag.getString("PowerConfig"));
        }
        if (tag.contains("Efficiency", NbtType.NUMBER)) {
            setEfficiency(tag.getDouble("Efficiency"));
        }
        if (tag.contains("ProgressCurrent", NbtType.NUMBER)) {
            setProgress(tag.getDouble("ProgressCurrent"));
        }
        if (tag.contains("Recipe", NbtType.STRING)) {
            setRecipeById(new Identifier(tag.getString("Recipe")));
        }
        if (tag.contains("Speed", NbtType.NUMBER)) {
            setSpeed(tag.getDouble("Speed"));
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        if (getActivationState() != ActivationState.values()[0]) {
            tag.putString("ActivationState", String.valueOf(getActivationState()));
        }
        if (configItem.getLabel().asString() != "xxxxxxxxxxxxxxxxxxxxxxxx") {
            tag.putString("ItemConfig", configItem.getLabel().asString());
        }
        if (configFluid.getLabel().asString() != "xxxxxxxxxxxxxxxxxxxxxxxx") {
            tag.putString("FluidConfig", configFluid.getLabel().asString());
        }
        if (configPower.getLabel().asString() != "xxxxxxxxxxxxxxxxxxxxxxxx") {
            tag.putString("PowerConfig", configPower.getLabel().asString());
        }
        if (getEfficiency() != 1.0) {
            tag.putDouble("Efficiency", getEfficiency());
        }
        if (progress.getBarCurrent() > 0.0) {
            tag.putDouble("ProgressCurrent", progress.getBarCurrent());
        }
        if (recipe.hasLabel()) {
            tag.putString("Recipe", recipe.getLabel().asString());
        }
        if (getSpeed() != 1.0) {
            tag.putDouble("Speed", getSpeed());
        }
        return tag;
    }

    public ActivationState getActivationState() {
        return ActivationState.valueOf(activationState.getLabel().getString());
    }

    private void setActivationState(String string) {
        assert ActivationState.isValid(string);
        activationState.withLabel(string);
    }

    public void nextActivationState() {
        setActivationState(String.valueOf(getActivationState().next(1)));
    }

    public boolean getConfig(ConfigType type, ConfigBehavior behavior, Direction dir) {
        String string = getConfigElement(type).getLabel().asString();
        int i = getIndex(behavior, dir);
        if (behavior.def == true) {
            return string.charAt(i) == '0' ? false : true;
        } else {
            return string.charAt(i) == '1' ? true : false;
        }
    }

    public void changeConfig (ConfigType type, ConfigBehavior behavior, Direction dir) {
        setConfig(type, behavior, dir, !getConfig(type, behavior, dir));
    }

    protected void setConfig(ConfigType type, ConfigBehavior behavior, Direction dir, boolean bl) {
        char[] chars = getConfigElement(type).getLabel().asString().toCharArray();
        chars[getIndex(behavior, dir)] = bl ? '1' : '0';
        ((SimpleDataElement) getConfigElement(type)).withLabel(new String(chars));
    }

    protected int getIndex(ConfigBehavior behavior, Direction dir) {
        return dir.getId() * ConfigBehavior.values().length + behavior.ordinal();
    }

    protected DataElement getConfigElement(ConfigType type) {
        if (type == ConfigType.ITEM) {
            return configItem;
        } else if (type == ConfigType.FLUID) {
            return configFluid;
        } else if (type == ConfigType.POWER) {
            return configPower;
        } else {
            TechMod.LOGGER.smallBug();
            return null;
        }
    }
    
    public double getEfficiency() {
        return Double.valueOf(efficiency.getLabel().getString());
    }

    public void setEfficiency(double value) {
        efficiency.withLabel(String.valueOf(value));
    }

    public void addProgress(double value) {
        setProgress(progress.getBarCurrent() + value);
    }

    public void resetProgress() {
        setProgress(progress.getBarMinimum());
    }

    private void setProgress(double value) {
        value = value > progress.getBarMaximum() ? progress.getBarMaximum() : value < progress.getBarMinimum() ? progress.getBarMinimum() : value;
        progress.withBar(progress.getBarMinimum(), value, progress.getBarMaximum(), progress.getBarUnit());
    }

    public void setRecipe(Recipe<?> recipe) {
        if (recipe != null) {
            setRecipeById(recipe.getId());
        } else {
            resetRecipe();
        }
    }

    private void setRecipeById(Identifier id) {
        recipe.withLabel(id.toString());
    }

    @SuppressWarnings("unchecked")
    public Recipe<?> getRecipe(World world) {
        Optional<Recipe<?>> optional = (Optional<Recipe<?>>) world.getRecipeManager().get(new Identifier(recipe.getLabel().asString()));
        return optional.isPresent() ? optional.get() : null;
    }

    public void resetRecipe() {
        recipe.withLabel((Text) null);
    }

    public double getSpeed() {
        return Double.valueOf(speed.getLabel().getString());
    }

    public void setSpeed(double value) {
        speed.withLabel(String.valueOf(value));
    }
    
    public static enum ActivationState {
        ALWAYS_ON,
        REDSTONE_ON,
        REDSTONE_OFF,
        ALWAYS_OFF;

        public ActivationState next(int amount) {
            return values()[(ordinal() + amount) % values().length];
        }

        public static boolean isValid(String string) {
            try {
                toActivationState(string);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        public static String toString(ActivationState state) {
            return state.name();
        }

        public static ActivationState toActivationState(String string) {
            return valueOf(string);
        }
    }

    public static enum ConfigType {
        ITEM,
        FLUID,
        POWER;
    }

    public static enum ConfigBehavior {
        SELF_INPUT (false),
        FOREIGN_INPUT (true),
        SELF_OUTPUT (false),
        FOREIGN_OUTPUT (true);

        public boolean def;

        private ConfigBehavior(boolean def) {
            this.def = def;
        }

        public ConfigBehavior next(int amount) {
            return values()[(ordinal() + amount) % values().length];
        }
    }
}