package com.example.safe.vo;

import com.example.safe.entity.DangerEntity;
import com.example.safe.entity.PhotoEntity;

import java.util.List;

public class DangerPhotoVo {
    private DangerEntity dangerEntity;
    private List<PhotoEntity> photoEntityList;

    public DangerEntity getDangerEntity() {
        return dangerEntity;
    }

    public void setDangerEntity(DangerEntity dangerEntity) {
        this.dangerEntity = dangerEntity;
    }

    public List<PhotoEntity> getPhotoEntityList() {
        return photoEntityList;
    }

    public void setPhotoEntityList(List<PhotoEntity> photoEntityList) {
        this.photoEntityList = photoEntityList;
    }
}
