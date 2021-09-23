package de.einholz.ehmooshroom.container.component.data;

//XXX is this needed otherwise delete
@Deprecated
public class NameDataComponent /*implements DataProviderComponent*/ {
    /*
    protected SimpleDataElement containerName = new SimpleDataElement();
    protected final String defaultName;
    
    public NameDataComponent(Identifier id) {
        this(id.toString());
    }
    
    public NameDataComponent(String name) {
        setName(name);
        defaultName = name;
    }

    @Override
    public void provideData(List<DataElement> data) {
        data.add(containerName);
    }

    @Override
    public DataElement getElementFor(Unit unit) {
        return null;
    }

    @Override
    public void readFromNbt(NbtCompound nbt) {
        if (nbt.contains("Name", NbtType.STRING)) setName(nbt.getString("Name"));
    }

    @Override
    public void writeToNbt(NbtCompound nbt) {
        if (getName() != defaultName) nbt.putString("Name", getName());
    }

    public String getName() {
        Identifier id = new Identifier(containerName.getLabel().asString());
        return "block." + id.getNamespace() + "." + id.getPath();
    }

    public void setName(Identifier id) {
        setName(id.toString());
    }

    public void setName(String name) {
        containerName.withLabel(name);
    }
    */
}