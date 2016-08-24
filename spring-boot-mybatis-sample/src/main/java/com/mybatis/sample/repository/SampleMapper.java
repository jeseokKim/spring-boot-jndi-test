package com.mybatis.sample.repository;

import org.springframework.stereotype.Repository;

import com.mybatis.sample.model.SampleModel;

@Repository
public interface SampleMapper {

    SampleModel getData();
}
