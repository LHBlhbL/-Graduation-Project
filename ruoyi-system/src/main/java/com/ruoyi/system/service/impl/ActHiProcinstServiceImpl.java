package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ActHiProcinstMapper;
import com.ruoyi.system.domain.ActHiProcinst;
import com.ruoyi.system.service.IActHiProcinstService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-02-15
 */
@Service
public class ActHiProcinstServiceImpl implements IActHiProcinstService 
{
    @Autowired
    private ActHiProcinstMapper actHiProcinstMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ActHiProcinst selectActHiProcinstById(String id)
    {
        return actHiProcinstMapper.selectActHiProcinstById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param actHiProcinst 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ActHiProcinst> selectActHiProcinstList(ActHiProcinst actHiProcinst)
    {
        return actHiProcinstMapper.selectActHiProcinstList(actHiProcinst);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param actHiProcinst 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertActHiProcinst(ActHiProcinst actHiProcinst)
    {
        return actHiProcinstMapper.insertActHiProcinst(actHiProcinst);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param actHiProcinst 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateActHiProcinst(ActHiProcinst actHiProcinst)
    {
        return actHiProcinstMapper.updateActHiProcinst(actHiProcinst);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteActHiProcinstByIds(String[] ids)
    {
        return actHiProcinstMapper.deleteActHiProcinstByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteActHiProcinstById(String id)
    {
        return actHiProcinstMapper.deleteActHiProcinstById(id);
    }
}
