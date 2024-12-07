package mod.bespectacled.modernbetaforge.util.chunk;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.gen.structure.StructureComponent;

public class ComponentChunk {
    private final List<StructureComponent> components;
    
    public ComponentChunk(int chunkX, int chunkZ) {
        this.components = new LinkedList<>();
    }
    
    public List<StructureComponent> getComponents() {
        return this.components;
    }
    
    public boolean addComponent(StructureComponent component) {
        if (!this.components.contains(component)) {
            this.components.add(component);
            
            return true;
        }
        
        return false;
    }
}
