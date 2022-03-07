package com.ruoyi.project.service.impl;

import com.ruoyi.project.domain.Project;
import com.ruoyi.project.mapper.ProjectMapper;
import com.ruoyi.project.service.IRemiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemiServiceImpl implements IRemiService {

    @Autowired
    ProjectMapper mapper;
    @Override
    public List<Project> listRemi() {
        return mapper.selectRemiList();
    }
}
