package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.ActHiProcinst;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2022-02-15
 */
public interface IActHiProcinstService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public ActHiProcinst selectActHiProcinstById(String id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param actHiProcinst 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ActHiProcinst> selectActHiProcinstList(ActHiProcinst actHiProcinst);

    /**
     * 新增【请填写功能名称】
     * 
     * @param actHiProcinst 【请填写功能名称】
     * @return 结果
     */
    public int insertActHiProcinst(ActHiProcinst actHiProcinst);

    /**
     * 修改【请填写功能名称】
     * 
     * @param actHiProcinst 【请填写功能名称】
     * @return 结果
     */
    public int updateActHiProcinst(ActHiProcinst actHiProcinst);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteActHiProcinstByIds(String[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteActHiProcinstById(String id);
}
