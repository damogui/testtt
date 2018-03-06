System.Declare("com.gongsibao.trade.web");
com.gongsibao.trade.web.SoCreatOrderPerformanceFormPart = org.netsharp.panda.commerce.FormPart.Extends( {

    ctor: function () {
        this.base();
    },
    accountMobileChange:function(el){

        var me = this;
        var mobile = $(el).val().trim();

        //1.校验手机号格式
        if(System.isnull(mobile)){

            me.clearAccount();
            return false;
        }

        if(!/^(1[0-9])\d{9}$/.test(mobile)){

            IMessageBox.toast('【手机】格式错误',2);
            me.clearAccount();
            return false;
        }

        //2.验证是否已开户
        this.invokeService("getAccount", [mobile], function (data) {

            var account = data;
            if(account){

                //会员名称
                $('#accountName').val(account.name);

                //会员邮箱
                $('#email').val(account.email);

                //会员Id
                me.currentItem.accountId = account.id;
            }else{

                IMessageBox.toast('会员不存在',2);
                me.clearAccount();
            }
        });
    },
    clearAccount:function(){

        $('#accountName').val('');
        $('#email').val('');
        this.currentItem.accountId = null;
    },
    addExtraProp:function(entity){

        //处理金额，需要*100
        var fen = 100;
        entity.totalPrice = entity.totalPrice*fen;
        entity.discountPrice = entity.discountPrice*fen;
        entity.payablePrice = entity.payablePrice*fen;

        $(entity.products).each(function(i,p){

            p.price = p.price*fen;
            p.priceOriginal = p.priceOriginal*fen;
            p.settlePrice = 0;

            $(p.items).each(function(i,item){

                item.price = item.price*fen;
                item.priceOriginal = item.priceOriginal*fen;
            });
        });
    }
});
