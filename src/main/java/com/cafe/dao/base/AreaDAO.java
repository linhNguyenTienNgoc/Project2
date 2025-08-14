package com.cafe.dao.base;

import com.cafe.model.entity.Area;

import java.util.List;

public interface AreaDAO {
    boolean addArea(Area area);
    boolean updateArea(Area area);
    boolean deleteArea(int areaId);
    Area getAreaById(int areaId);
    List<Area> getAllAreas();
}
