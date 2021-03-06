package com.dili.uap.controller;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dili.assets.sdk.dto.BankDto;
import com.dili.assets.sdk.dto.BankUnionInfoDto;
import com.dili.assets.sdk.dto.CityDto;
import com.dili.assets.sdk.dto.CityLevelType;
import com.dili.logger.sdk.annotation.BusinessLogger;
import com.dili.logger.sdk.base.LoggerContext;
import com.dili.logger.sdk.glossary.LoggerConstant;
import com.dili.ss.domain.BaseOutput;
import com.dili.ss.dto.DTOUtils;
import com.dili.ss.dto.IDTO;
import com.dili.uap.constants.UapConstants;
import com.dili.uap.domain.dto.DataDictionaryDto;
import com.dili.uap.domain.dto.EditFirmAdminUserDto;
import com.dili.uap.domain.dto.FirmAddDto;
import com.dili.uap.domain.dto.FirmQueryDto;
import com.dili.uap.domain.dto.FirmUpdateDto;
import com.dili.uap.rpc.BankRpc;
import com.dili.uap.rpc.BankUnionInfoRpc;
import com.dili.uap.rpc.CityRpc;
import com.dili.uap.sdk.domain.DataDictionaryValue;
import com.dili.uap.sdk.domain.Firm;
import com.dili.uap.sdk.domain.User;
import com.dili.uap.sdk.domain.UserTicket;
import com.dili.uap.sdk.session.SessionContext;
import com.dili.uap.service.DataDictionaryValueService;
import com.dili.uap.service.FirmService;
import com.dili.uap.service.RoleService;
import com.dili.uap.service.UserService;

/**
 * ???MyBatis Generator?????????????????? This file was generated on 2019-04-09 14:35:13.
 */
@Controller
@RequestMapping("/firm")
public class FirmController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FirmController.class);

	@Autowired
	FirmService firmService;
	@Autowired
	private DataDictionaryValueService ddValueService;
	@Autowired
	private CityRpc cityRpc;
	@Autowired
	private BankRpc bankRpc;
	@Autowired
	private BankUnionInfoRpc bankUnionInfoRpc;
	@Autowired
	private UserService userService;
	@Autowired
	private RoleService roleService;

	/**
	 * ?????????Firm??????
	 * 
	 * @param modelMap
	 * @return
	 */
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public String index(ModelMap modelMap) {
		return "firm/index";
	}

	/**
	 * ??????Firm
	 * 
	 * @param firm
	 * @return
	 */
	@RequestMapping(value = "/list.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody Firm list(Firm firm) {
		return firm;
	}

	/**
	 * ????????????Firm
	 * 
	 * @param firm
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/listPage.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody String listPage(FirmQueryDto firm) throws Exception {
		firm.setDeleted(false);
		return firmService.listEasyuiPageByExample(firm, true).toString();
	}

	/**
	 * ????????????
	 * 
	 * @return
	 */
	@GetMapping("/add.html")
	public String addView() {
		return "firm/add";
	}

	/**
	 * ??????Firm
	 * 
	 * @param firmAddDto
	 * @return
	 */
	@BusinessLogger(businessType = "firm_management", content = "????????????", operationType = "add", systemCode = "UAP")
	@RequestMapping(value = "/insert.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput insert(@Validated FirmAddDto firmAddDto) {
		String validator = (String) firmAddDto.aget(IDTO.ERROR_MSG_KEY);
		if (StringUtils.isNotBlank(validator)) {
			return BaseOutput.failure(validator);
		}
		BaseOutput<Object> output = firmService.insertAndBindUserDataAuth(firmAddDto);
		if (output.isSuccess()) {
			Firm firm = (Firm) output.getData();
			LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, firm.getCode());
			LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, firm.getId());
			UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
			if (userTicket != null) {
				LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, userTicket.getId());
				LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, userTicket.getFirmId());
			}
		}
		return output;
	}

	/**
	 * ????????????
	 * 
	 * @param id
	 * @param modelMap
	 * @return
	 */
	@GetMapping("/update.html")
	public String updateView(Long id, ModelMap modelMap) {
		Firm firm = this.firmService.get(id);
		modelMap.addAttribute("firm", firm);
		if (firm.getDepositBankUnionInfoId() != null) {
			BankUnionInfoDto query = new BankUnionInfoDto();
			query.setId(firm.getDepositBankUnionInfoId());
			BaseOutput<List<BankUnionInfoDto>> output = this.bankUnionInfoRpc.list(query);
			if (CollectionUtils.isNotEmpty(output.getData())) {
				modelMap.addAttribute("bankUnionInfo", output.getData().get(0));
			}
		}
		return "firm/update";
	}

	/**
	 * ??????Firm
	 * 
	 * @param dto
	 * @return
	 */
	@RequestMapping(value = "/update.action", method = { RequestMethod.GET, RequestMethod.POST })
	@BusinessLogger(businessType = "firm", content = "??????id:${businessId!},??????id:${operatorId!}, ??????id:${marketId!}????????????:${name!}???", operationType = "edit", notes = "????????????", systemCode = "UAP")
	public @ResponseBody BaseOutput update(FirmUpdateDto dto) {
		String validator = (String) dto.aget(IDTO.ERROR_MSG_KEY);
		if (StringUtils.isNotBlank(validator)) {
			return BaseOutput.failure(validator);
		}
		BaseOutput<Object> output = firmService.updateSelectiveAfterCheck(dto);
		if (output.isSuccess()) {
			Firm firm = (Firm) output.getData();
			LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, firm.getCode());
			LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, firm.getId());
			UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
			if (userTicket != null) {
				LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, userTicket.getId());
				LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, userTicket.getFirmId());
			}
		}
		return output;
	}

	/**
	 * ??????Firm
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/delete.action", method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody BaseOutput delete(Long id) {
		Firm firm = this.firmService.get(id);
		firmService.delete(id);
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_CODE_KEY, firm.getCode());
		LoggerContext.put(LoggerConstant.LOG_BUSINESS_ID_KEY, firm.getId());
		UserTicket userTicket = SessionContext.getSessionContext().getUserTicket();
		if (userTicket != null) {
			LoggerContext.put(LoggerConstant.LOG_OPERATOR_ID_KEY, userTicket.getId());
			LoggerContext.put(LoggerConstant.LOG_MARKET_ID_KEY, userTicket.getFirmId());
		}
		return BaseOutput.success("????????????");
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/listByLoggedUser.action")
	public List<Firm> listByLoggedUser() {
		UserTicket user = SessionContext.getSessionContext().getUserTicket();
		if (!user.getFirmCode().equals(UapConstants.GROUP_CODE)) {
			Firm query = DTOUtils.newInstance(Firm.class);
			query.setCode(user.getFirmCode());
			return this.firmService.list(query);
		}
		return this.firmService.list(DTOUtils.newInstance(Firm.class));
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/industryList.action")
	public List<DataDictionaryValue> industryList() {
		DataDictionaryDto dd = this.ddValueService.findByCode(UapConstants.DD_CODE_INDUSTRY, UapConstants.PARK_SYSTEM_CODE);
		return dd.getDataDictionaryValues();
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/certificateTypeList.action")
	public List<DataDictionaryValue> certificateTypeList() {
		DataDictionaryDto dd = this.ddValueService.findByCode(UapConstants.DD_CODE_CERTIFICATE_TYPE, UapConstants.PARK_SYSTEM_CODE);
		return dd.getDataDictionaryValues();
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/provinceList.action")
	public List<CityDto> provinceList() {
		CityDto query = new CityDto();
		query.setLevelType(CityLevelType.PROVINCE.getValue());
		BaseOutput<List<CityDto>> output = this.cityRpc.list(query);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			return null;
		}
		return output.getData();
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/cityList.action")
	public List<CityDto> cityList(@RequestParam Long parentId) {
		CityDto query = new CityDto();
		query.setParentId(parentId);
		query.setLevelType(CityLevelType.CITY.getValue());
		BaseOutput<List<CityDto>> output = this.cityRpc.list(query);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			return null;
		}
		return output.getData();
	}

	/**
	 * ???????????????
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/districtList.action")
	public List<CityDto> districtList(@RequestParam Long parentId) {
		CityDto query = new CityDto();
		query.setParentId(parentId);
		query.setLevelType(CityLevelType.DISTRICT.getValue());
		BaseOutput<List<CityDto>> output = this.cityRpc.list(query);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			return null;
		}
		return output.getData();
	}

	/**
	 * ??????????????????????????????
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/legalPersonCertificateTypeList.action")
	public List<DataDictionaryValue> legalPersonCertificateTypeList() {
		DataDictionaryDto dd = this.ddValueService.findByCode(UapConstants.DD_CODE_LEGAL_PERSON_CERTIFICATE_TYPE, UapConstants.PARK_SYSTEM_CODE);
		return dd.getDataDictionaryValues();
	}

	/**
	 * ??????????????????
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/bankList.action")
	public List<BankDto> bankList() {
		BankDto query = new BankDto();
		BaseOutput<List<BankDto>> output = this.bankRpc.list(query);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			return null;
		}
		return output.getData();
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param id       ??????id
	 * @param modelMap
	 * @return
	 */
	@GetMapping("/editAdminUser.html")
	public String editAdminUserView(@RequestParam Long id, ModelMap modelMap) {
		Firm firm = this.firmService.get(id);
		if (firm.getUserId() != null) {
			modelMap.addAttribute("user", this.userService.get(firm.getUserId()));
		} else {
			User user = DTOUtils.newInstance(User.class);
			user.setCellphone(firm.getTelephone());
			user.setEmail(firm.getEmail());
			modelMap.addAttribute("user", user);
		}
		if (firm.getRoleId() != null) {
			modelMap.addAttribute("role", this.roleService.get(firm.getRoleId()));
		}
		modelMap.addAttribute("firm", firm);
		return "firm/editAdminUser";
	}

	/**
	 * ???????????????????????????
	 * 
	 * @param id       ??????id
	 * @param modelMap
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/editAdminUser.action", consumes = MediaType.APPLICATION_JSON_VALUE)
	public BaseOutput<Object> editAdminUser(EditFirmAdminUserDto dto) {
		return this.firmService.updateAdminUser(dto);
	}

	/**
	 * ????????????????????????
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/bankUnionInfoList.action")
	public List<BankUnionInfoDto> bankUnionInfoList(BankUnionInfoDto query) {
		BaseOutput<List<BankUnionInfoDto>> output = this.bankUnionInfoRpc.list(query);
		if (!output.isSuccess()) {
			LOGGER.error(output.getMessage());
			return null;
		}
		return output.getData();
	}

	/**
	 * ??????
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/enable.action", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> enable(@RequestParam Long id) {
		return this.firmService.enable(id);
	}

	/**
	 * ??????
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/disable.action", method = { RequestMethod.GET, RequestMethod.POST })
	public BaseOutput<Object> disable(@RequestParam Long id) {
		return this.firmService.disable(id);
	}

	/**
	 * ????????????
	 * 
	 * @param id
	 * @return
	 */
	@ResponseBody
	@PostMapping(value = "/logicalDelete.action")
	public BaseOutput<Object> logicalDelete(@RequestParam Long id) {
		return this.firmService.logicalDelete(id);
	}
}