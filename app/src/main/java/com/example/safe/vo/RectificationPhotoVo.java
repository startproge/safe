package com.example.safe.vo;

import com.example.safe.entity.PhotoEntity;
import com.example.safe.entity.RectificationEntity;
import java.util.List;


public class RectificationPhotoVo {
    private RectificationEntity entity;
    private List<PhotoEntity> photoEntityList;

    public RectificationEntity getEntity() {
        return entity;
    }

    public void setEntity(RectificationEntity entity) {
        this.entity = entity;
    }

    public List<PhotoEntity> getPhotoEntityList() {
        return photoEntityList;
    }

    public void setPhotoEntityList(List<PhotoEntity> photoEntityList) {
        this.photoEntityList = photoEntityList;
    }
}
