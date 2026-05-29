package dk.sdu.cbse.common.data;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class World {
    private final Map<String, Entity> entityMap = new ConcurrentHashMap<>();

    public void addEntity(Entity entity) { entityMap.put(entity.getID(), entity); }
    public void removeEntity(Entity entity) { entityMap.remove(entity.getID()); }
    public Collection<Entity> getEntities() { return entityMap.values(); }
}