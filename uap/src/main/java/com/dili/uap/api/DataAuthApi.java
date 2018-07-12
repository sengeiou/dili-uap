package com.dili.uap.api;

import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.uap.domain.DataAuthRef;
import com.dili.uap.sdk.component.DataAuthSource;
import com.dili.uap.sdk.domain.UserDataAuth;
import com.dili.uap.sdk.service.DataAuthSourceService;
import com.dili.uap.service.DataAuthRefService;
import com.dili.uap.service.UserDataAuthService;
import com.google.common.collect.Lists;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据权限Api
 */
@Api("/dataAuthApi")
@Controller
@RequestMapping("/dataAuthApi")
public class DataAuthApi {

    @Autowired
    private UserDataAuthService userDataAuthService;

    @Autowired
    private DataAuthSource dataAuthSource;

    @Autowired
    private DataAuthRefService dataAuthRefService;

    @ApiOperation(value = "根据条件查询用户数据权限")
    @ApiImplicitParams({ @ApiImplicitParam(name = "UserDataAuth", value = "UserDataAuth", required = true, dataType = "UserDataAuth") })
    @RequestMapping(value = "/listUserDataAuth.api", method = { RequestMethod.POST })
    @ResponseBody
    public BaseOutput<List<UserDataAuth>> listUserDataAuth(UserDataAuth userDataAuth) {
        return BaseOutput.success().setData(userDataAuthService.listByExample(userDataAuth));
    }

    @ApiOperation(value = "根据条件查询用户数据权限")
    @ApiImplicitParams({ @ApiImplicitParam(name = "UserDataAuth", value = "UserDataAuth", required = true, dataType = "UserDataAuth") })
    @RequestMapping(value = "/listUserDataAuthDetail.api", method = { RequestMethod.POST })
    @ResponseBody
    public BaseOutput<List<Map>> listUserDataAuthDetail(UserDataAuth userDataAuth) {
        List<UserDataAuth> userDataAuthList = userDataAuthService.listByExample(userDataAuth);
        //过滤获取不同的refCode
//        List<String> distinctRefCode = userDataAuthList.stream().map(t ->t.getRefCode()).distinct().collect(Collectors.toList());
        //key为refCode， value为values列表
        Map<String, List<String>> refCode2values = new HashMap<>();
        for(UserDataAuth uda : userDataAuthList){
            if(refCode2values.get(uda.getRefCode()) == null){
                refCode2values.put(uda.getRefCode(), Lists.newArrayList());
            }
            refCode2values.get(uda.getRefCode()).add(uda.getValue());
        }
//        Map key为value, 值为转义后的行数据
        List<Map<String, Map>> detailList = Lists.newArrayList();
        for(Map.Entry<String, List<String>> refCode2value : refCode2values.entrySet()){
            DataAuthRef dataAuthRef = DTOUtils.newDTO(DataAuthRef.class);
            dataAuthRef.setCode(refCode2value.getKey());
            List<DataAuthRef> dataAuthRefList = dataAuthRefService.list(dataAuthRef);
            if(dataAuthRefList == null || dataAuthRefList.isEmpty()){
                return null;
            }
            String springId = dataAuthRefList.get(0).getSpringId();
            DataAuthSourceService dataAuthSourceService = dataAuthSource.getDataAuthSourceServiceMap().get(springId);
            if(null == dataAuthSourceService){
                return null;
            }
            detailList.add(dataAuthSourceService.bindDataAuthes(dataAuthRef.getParam(), refCode2value.getValue()));
        }
        return BaseOutput.success().setData(detailList);
    }


}