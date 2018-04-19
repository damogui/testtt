package com.gongsibao.rest.service.user;

import com.gongsibao.account.base.IAccountWeiXinService;
import com.gongsibao.entity.acount.Account;
import com.gongsibao.entity.acount.AccountWeiXin;
import com.gongsibao.rest.base.user.IAccountService;
import com.gongsibao.utils.sms.SmsHelper;
import org.netsharp.communication.ServiceFactory;
import org.netsharp.wx.pa.base.ICustomService;
import org.netsharp.wx.pa.entity.Fans;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class AccountService implements IAccountService{
    com.gongsibao.account.base.IAccountService accountService= ServiceFactory.create(com.gongsibao.account.base.IAccountService.class);
    IAccountWeiXinService accountWeiXinService=ServiceFactory.create(IAccountWeiXinService.class);
    ICustomService customService=ServiceFactory.create(ICustomService.class);

    /**
     * @Description:TODO 登录验证
     * @param  openId
     * @return com.gongsibao.entity.acount.Account
     * @author hbpeng <hbpeng@gongsibao.com>
     * @date 2018/4/16 14:39
     */
    @Override
    public Account login(String openId) {
        Fans weiXin=accountWeiXinService.queryFansByOpenId(openId);
        if(null==weiXin){
            //创建微信账号
            this.createFans(openId);
            return null;
        }else if(null==weiXin.getUserId()){
            return null;
        }else{
            return accountService.byId(weiXin.getUserId());
        }
    }
    /**
     * @Description:TODO 发送验证码
     * @param  mobile, code
     * @return void
     * @author hbpeng <hbpeng@gongsibao.com>
     * @date 2018/4/12 19:00
     */
    @Override
    public void sendSms(String mobile, String code) {
        // 发送短信
        new Thread() {
            @Override
            public void run() {
                SmsHelper.send(mobile, code);
            }
        }.start();
    }

    @Override
    public void updateAccount(String mobile,String openId) {
        Account accountOld=accountService.byMobile(mobile);
        if(null==accountOld){
            //更新uc_account 新增一条
            Account account = new Account();{
                account.toNew();
                account.setPasswd("");
                account.setCreateTime(new Date());
                account.setMobilePhone(mobile);
                account.setTicket(UUID.randomUUID().toString());
                account.setEmail("");
                account.setIdentityCard("");
                account.setTelephone("");
                account.setRealName("");
                account.setCreateTime(new Date());
                account.setIsBbk("");
                account.setName("");
                //来源微信
                account.setSourceClientId(1036);
                account.setHeadThumbFileId(0);
            }
            Account result = accountService.save(account);
            //更新uc_account_weixin 表 更新 account_id
            accountWeiXinService.bandMobile(result.getId(),openId);
        }else{
            //更新uc_account_weixin 表 更新 account_id
            accountWeiXinService.bandMobile(accountOld.getId(),openId);
        }
    }

    @Override
    public Boolean createFans(String openId) {
        //新增粉丝表数据
        Fans fans=accountWeiXinService.createFans(openId);
        return fans.getId()==null?false:true;
    }

    @Override
    public void sendTextMessage(String content, String openId, String originalId) {
        customService.sendTextMessage(content,openId,originalId);
    }

    @Override
    public Account queryByMobile(String mobile) {
        Account account=accountService.byMobile(mobile);
        if(null==account){
            return null;
        }
        Fans weiXin=accountWeiXinService.queryFansByUserId(account.getId());
        if(null==weiXin){
            return null;
        }
        account.setOpenid(weiXin.getOpenId());
        return account;
    }

    @Override
    public void updateTicket(Integer id, String ticket) {
        accountService.updateTicket(id,ticket);
    }
    /**
     * @Description:TODO
     * @param  mobile, orderPorudctId
     * @return void
     * @author hbpeng <hbpeng@gongsibao.com>
     * @date 2018/4/18 17:25
     */
    @Override
    public void pushOrderStateMsg(String mobile, Integer orderPorudctId) {
        accountWeiXinService.pushOrderStateMsg(mobile,orderPorudctId);
    }
}