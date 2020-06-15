package de.alberteinholz.ehtech.blocks.components.container.machine;

import java.util.List;
import java.util.Optional;

import de.alberteinholz.ehtech.TechMod;
import de.alberteinholz.ehtech.blocks.components.container.ContainerDataProviderComponent;
import io.github.cottonmc.component.data.api.DataElement;
import io.github.cottonmc.component.data.api.Unit;
import io.github.cottonmc.component.data.api.UnitManager;
import io.github.cottonmc.component.data.impl.SimpleDataElement;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.Recipe;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class MachineDataProviderComponent extends ContainerDataProviderComponent {
    public SimpleDataElement activationState = new SimpleDataElement(ActivationState.ALWAYS_ON.name());
    public SimpleDataElement configItem = new SimpleDataElement().withLabel("000000000000000000000000");
    public SimpleDataElement configFluid = new SimpleDataElement().withLabel("000000000000000000000000");
    public SimpleDataElement configPower = new SimpleDataElement().withLabel("000000000000000000000000");
    public SimpleDataElement efficiency = new SimpleDataElement(String.valueOf(1.0));
    public SimpleDataElement powerPerTick = new SimpleDataElement(String.valueOf(0));
    public SimpleDataElement progress = new SimpleDataElement().withBar(0.0, 0.0, 100.0, UnitManager.PERCENT);
    public SimpleDataElement recipe = new SimpleDataElement().withLabel((Text) null);
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
        data.add(powerPerTick);
        data.add(progress);
        data.add(recipe);
        data.add(speed);
    }

    @Override
    public DataElement getElementFor(Unit unit) {
        if (unit == progress.getBarUnit()) {
            return progress;
        } else if (unit == UnitManager.WU_PER_TICK) {
            return powerPerTick;
        } else {
            return super.getElementFor(unit);
        }
    }

    @Override
    public void fromTag(CompoundTag tag) {
        super.fromTag(tag);
        setActivationState((tag.getString("ActivationState")));
        setEfficiency(tag.getDouble("Efficiency"));
        setPowerPerTick(tag.getInt("PowerPerTick"));
        setProgress(tag.getDouble("Process"));
        if (tag.contains("Recipe", 8)) {
            setRecipeById(new Identifier(tag.getString("Recipe")));
        }
        setSpeed(tag.getDouble("Speed"));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.putString("ActivationState", String.valueOf(getActivationState()));
        tag.putDouble("Efficiency", getEfficiency());
        tag.putInt("PowerPerTick", getPowerPerTick());
        tag.putDouble("Progress", progress.getBarCurrent());
        if (recipe.hasLabel()) {
            tag.putString("Recipe", recipe.getLabel().asString());
        }
        tag.putDouble("Speed", getSpeed());
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
        if (i % 2 != 0) {
            return string.charAt(i) == '1' ? true : false;
        } else {
            return string.charAt(i) == '1' ? false : true;
        }
    }

    public void setConfig(ConfigType type, ConfigBehavior behavior, Direction dir, boolean bl) {
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
        
    public int getPowerPerTick() {
        return Integer.valueOf(powerPerTick.getLabel().getString());
    }

    public void setPowerPerTick(int value) {
        powerPerTick.withLabel(String.valueOf(value));
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
        SELF_INPUT,
        FOREIGEN_INPUT,
        SELF_OUTPUT,
        FOREIGN_OUTPUT;

        public ConfigBehavior next(int amount) {
            return values()[(ordinal() + amount) % values().length];
        }
    }
}