package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.dao.SysDichinaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SysDichinaryService {

    @Autowired
    SysDichinaryRepository sysDichinaryRepository;

    public SysDictionary findBydType(String type) {
        return sysDichinaryRepository.findBydType(type);
    }
}
