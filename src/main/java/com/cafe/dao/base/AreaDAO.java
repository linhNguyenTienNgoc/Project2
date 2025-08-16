package com.cafe.dao.base;

import com.cafe.model.entity.Area;
import java.util.List;
import java.util.Optional;

public interface AreaDAO {
    boolean save(Area area);
    boolean update(Area area);
    boolean delete(int areaId);
    Optional<Area> findById(Integer areaId);
    List<Area> findAll();
    List<Area> findActiveAreas();
}