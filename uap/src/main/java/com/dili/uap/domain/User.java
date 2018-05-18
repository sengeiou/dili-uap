package com.dili.uap.domain;

import com.dili.ss.domain.BaseDomain;
import com.dili.ss.metadata.FieldEditor;
import com.dili.ss.metadata.annotation.EditMode;
import com.dili.ss.metadata.annotation.FieldDef;
import java.util.Date;
import javax.persistence.*;

/**
 * 由MyBatis Generator工具自动生成
 * 
 * This file was generated on 2018-05-18 10:46:46.
 */
@Table(name = "`user`")
public class User extends BaseDomain {
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名
     */
    @Column(name = "`user_name`")
    private String userName;

    /**
     * 密码
     */
    @Column(name = "`password`")
    private String password;

    /**
     * 归属市场
     */
    @Column(name = "`firm_id`")
    private Long firmId;

    /**
     * 归属部门
     */
    @Column(name = "`department_id`")
    private Long departmentId;

    /**
     * 职位
     */
    @Column(name = "`position`")
    private String position;

    /**
     * 卡号
     */
    @Column(name = "`card_number`")
    private String cardNumber;

    /**
     * 最后登录ip
     */
    @Column(name = "`last_login_ip`")
    private String lastLoginIp;

    /**
     * 最后登录时间
     */
    @Column(name = "`last_login_time`")
    private Date lastLoginTime;

    /**
     * 创建时间
     */
    @Column(name = "`created`")
    private Date created;

    /**
     * 修改时间
     */
    @Column(name = "`modified`")
    private Date modified;

    /**
     * 状态##状态##{data:[{value:1,text:"启用"},{value:0,text:"停用"}]}
     */
    @Column(name = "`state`")
    private Integer state;

    /**
     * 真实姓名
     */
    @Column(name = "`real_name`")
    private String realName;

    /**
     * 用户编号
     */
    @Column(name = "`serial_number`")
    private String serialNumber;

    /**
     * 手机号码
     */
    @Column(name = "`cellphone`")
    private String cellphone;

    /**
     * 邮箱
     */
    @Column(name = "`email`")
    private String email;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    @FieldDef(label="主键")
    @EditMode(editor = FieldEditor.Number, required = true)
    public Long getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名
     *
     * @return user_name - 用户名
     */
    @FieldDef(label="用户名", maxLength = 50)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名
     *
     * @param userName 用户名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取密码
     *
     * @return password - 密码
     */
    @FieldDef(label="密码", maxLength = 128)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取归属市场
     *
     * @return firm_id - 归属市场
     */
    @FieldDef(label="归属市场")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getFirmId() {
        return firmId;
    }

    /**
     * 设置归属市场
     *
     * @param firmId 归属市场
     */
    public void setFirmId(Long firmId) {
        this.firmId = firmId;
    }

    /**
     * 获取归属部门
     *
     * @return department_id - 归属部门
     */
    @FieldDef(label="归属部门")
    @EditMode(editor = FieldEditor.Number, required = false)
    public Long getDepartmentId() {
        return departmentId;
    }

    /**
     * 设置归属部门
     *
     * @param departmentId 归属部门
     */
    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * 获取职位
     *
     * @return position - 职位
     */
    @FieldDef(label="职位", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getPosition() {
        return position;
    }

    /**
     * 设置职位
     *
     * @param position 职位
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * 获取卡号
     *
     * @return card_number - 卡号
     */
    @FieldDef(label="卡号", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getCardNumber() {
        return cardNumber;
    }

    /**
     * 设置卡号
     *
     * @param cardNumber 卡号
     */
    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    /**
     * 获取最后登录ip
     *
     * @return last_login_ip - 最后登录ip
     */
    @FieldDef(label="最后登录ip", maxLength = 20)
    @EditMode(editor = FieldEditor.Text, required = false)
    public String getLastLoginIp() {
        return lastLoginIp;
    }

    /**
     * 设置最后登录ip
     *
     * @param lastLoginIp 最后登录ip
     */
    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    /**
     * 获取最后登录时间
     *
     * @return last_login_time - 最后登录时间
     */
    @FieldDef(label="最后登录时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * 设置最后登录时间
     *
     * @param lastLoginTime 最后登录时间
     */
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * 获取创建时间
     *
     * @return created - 创建时间
     */
    @FieldDef(label="创建时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Date getCreated() {
        return created;
    }

    /**
     * 设置创建时间
     *
     * @param created 创建时间
     */
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * 获取修改时间
     *
     * @return modified - 修改时间
     */
    @FieldDef(label="修改时间")
    @EditMode(editor = FieldEditor.Datetime, required = true)
    public Date getModified() {
        return modified;
    }

    /**
     * 设置修改时间
     *
     * @param modified 修改时间
     */
    public void setModified(Date modified) {
        this.modified = modified;
    }

    /**
     * 获取状态##状态##{data:[{value:1,text:"启用"},{value:0,text:"停用"}]}
     *
     * @return state - 状态##状态##{data:[{value:1,text:"启用"},{value:0,text:"停用"}]}
     */
    @FieldDef(label="状态")
    @EditMode(editor = FieldEditor.Combo, required = true, params="{\"data\":[{\"text\":\"启用\",\"value\":1},{\"text\":\"停用\",\"value\":0}]}")
    public Integer getState() {
        return state;
    }

    /**
     * 设置状态##状态##{data:[{value:1,text:"启用"},{value:0,text:"停用"}]}
     *
     * @param state 状态##状态##{data:[{value:1,text:"启用"},{value:0,text:"停用"}]}
     */
    public void setState(Integer state) {
        this.state = state;
    }

    /**
     * 获取真实姓名
     *
     * @return real_name - 真实姓名
     */
    @FieldDef(label="真实姓名", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getRealName() {
        return realName;
    }

    /**
     * 设置真实姓名
     *
     * @param realName 真实姓名
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * 获取用户编号
     *
     * @return serial_number - 用户编号
     */
    @FieldDef(label="用户编号", maxLength = 128)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getSerialNumber() {
        return serialNumber;
    }

    /**
     * 设置用户编号
     *
     * @param serialNumber 用户编号
     */
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    /**
     * 获取手机号码
     *
     * @return cellphone - 手机号码
     */
    @FieldDef(label="手机号码", maxLength = 24)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getCellphone() {
        return cellphone;
    }

    /**
     * 设置手机号码
     *
     * @param cellphone 手机号码
     */
    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    /**
     * 获取邮箱
     *
     * @return email - 邮箱
     */
    @FieldDef(label="邮箱", maxLength = 64)
    @EditMode(editor = FieldEditor.Text, required = true)
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }
}