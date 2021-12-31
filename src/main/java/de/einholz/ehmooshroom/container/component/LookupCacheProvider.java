package de.einholz.ehmooshroom.container.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;

//XXX is this needed?
public interface LookupCacheProvider {
    <C extends Component> BlockApiCache<C, ?> getCache(ComponentKey<C> key);
}
