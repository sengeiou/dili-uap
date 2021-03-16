package com.dili.uap.service.impl;

import com.dili.ss.base.BaseServiceImpl;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.dao.PositionMapper;
import com.dili.uap.dao.UserMapper;
import com.dili.uap.domain.Position;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.service.PositionService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 由MyBatis Generator工具自动生成
 * This file was generated on 2020-12-01 15:23:34.
 */
@Service
public class PositionServiceImpl extends BaseServiceImpl<Position, Long> implements PositionService {

    public PositionMapper getActualDao() {
        return (PositionMapper)getDao();
    }

    @Resource
    public UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseOutput save(Position position) {

        Position queryPositionName = DTOUtils.newInstance(Position.class);
        if(StringUtils.isBlank(position.getFirmCode())){
            if(null == position.getId()){
                return BaseOutput.failure("新增职位市场不能为空!");
            }
            Position positionById = getActualDao().selectByPrimaryKey(position.getId());
            position.setFirmCode(positionById.getFirmCode());
        }

        queryPositionName.setPositionName(position.getPositionName());
        queryPositionName.setFirmCode(position.getFirmCode());
        List<Position> positionNameList = getActualDao().select(queryPositionName);

        Position queryPositionCode = DTOUtils.newInstance(Position.class);
        queryPositionCode.setPositionCode(position.getPositionCode());
        queryPositionCode.setFirmCode(position.getFirmCode());
        List<Position> positionCodeList = getActualDao().select(queryPositionCode);
        int resultCount = 0;
        //职位新增
        if(null == position.getId()){
            if (CollectionUtils.isNotEmpty(positionNameList)) {
                return BaseOutput.failure("该市场下职位名称已存在");
            }
            if (CollectionUtils.isNotEmpty(positionCodeList)) {
                return BaseOutput.failure("该市场下职位编码已存在");
            }
            resultCount = this.insertExactSimple(position);

        }else {
        //职位修改
            if (CollectionUtils.isNotEmpty(positionNameList)) {
                boolean result = positionNameList.stream().anyMatch(p -> p.getId().equals(position.getId()));
                if(!result){
                    return BaseOutput.failure("该市场下职位名称已存在");
                }
            }
            if (CollectionUtils.isNotEmpty(positionCodeList)) {
                boolean result = positionCodeList.stream().anyMatch(p -> p.getId().equals(position.getId()));
                if(!result){
                    return BaseOutput.failure("该市场下职位编码已存在");
                }
            }
            resultCount = this.updateExactSimple(position);
        }
        if(resultCount>0){
            return BaseOutput.success();
        }
        return BaseOutput.failure();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BaseOutput deletePosition(Long id) {
        this.delete(id);
        User user = DTOUtils.newDTO(User.class);
        user.setPositionId(id);
        userMapper.updateByUser(user);
        return BaseOutput.success();
    }
}