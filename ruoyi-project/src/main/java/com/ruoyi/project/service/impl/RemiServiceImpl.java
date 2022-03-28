package com.ruoyi.project.service.impl;

import com.ruoyi.project.domain.Project;
import com.ruoyi.project.domain.ProjectFlow;
import com.ruoyi.project.mapper.ProjectFlowMapper;
import com.ruoyi.project.mapper.ProjectMapper;
import com.ruoyi.project.service.IRemiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RemiServiceImpl implements IRemiService {

    @Autowired
    ProjectMapper mapper;

    @Autowired
    private ProjectFlowMapper projectFlowMapper;

    @Override
    public List<Project> listRemi() {
        List<Project> projects = mapper.selectRemiList();
        List<Project> returnList = new ArrayList<>();
        for(Project project:projects)
        {
            ProjectFlow projectFlow = projectFlowMapper.selectProjectFlowByPId(project.getProjectId());
            if(projectFlow!=null)
            {
                returnList.add(project);
            }
        }
        return returnList;
    }

    @Override
    public List<Project> queryList(Project project) {
        List<Project> projects = mapper.selectRemiQueryList(project);

        List<Project> returnList = new ArrayList<>();
        for(Project project1:projects)
        {
            ProjectFlow projectFlow = projectFlowMapper.selectProjectFlowByPId(project1.getProjectId());
            if(projectFlow!=null)
            {
                returnList.add(project1);
            }
        }
        return returnList;
    }


}
