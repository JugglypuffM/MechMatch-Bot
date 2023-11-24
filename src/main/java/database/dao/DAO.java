package database.dao;

public interface DAO<Entity, Id> {
    void create(Entity e);
    Entity read(Id i);
    void update(Entity e);
    void delete(Entity e);
}

