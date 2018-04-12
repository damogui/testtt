package com.gongsibao.rest.controller.v1.product;

import com.gongsibao.bd.base.IFileService;
import com.gongsibao.cms.base.IProductService;
import com.gongsibao.entity.acount.Account;
import com.gongsibao.entity.bd.File;
import com.gongsibao.entity.cms.AggregationResponse;
import com.gongsibao.entity.cms.Product;
import com.gongsibao.rest.common.apiversion.Api;
import com.gongsibao.rest.common.util.ProductUtils;
import com.gongsibao.rest.common.web.ResponseData;
import com.gongsibao.rest.service.product.IProductPriceService;
import com.gongsibao.utils.NumberUtils;
import org.netsharp.communication.ServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wx/{v}/icompany/product")
@Api(1)
public class ICompanyProductController {
    IProductService cmsProductService = ServiceFactory.create(IProductService.class);
    IFileService bdFileService =ServiceFactory.create(IFileService.class);

    @Autowired
    IProductPriceService productPriceService;
    /**
     * 包含产品cms信息，产品聚合信息，产品流程信息
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/cmsInfo",method = RequestMethod.GET)
    private ResponseData cmsInfo(HttpServletRequest request) {
        ResponseData data = new ResponseData();
        Map<String, Object> result = new HashMap<>();

        int productId = NumberUtils.toInt(request.getParameter("productId"));
        if (0 == productId) {
            data.setCode(-1);
            data.setMsg("商品不存在");
            return data;
        }
        try {
            // 获取cms基础信息
            Product cmsProduct = cmsProductService.findLastByProductId(productId);
            if (null == cmsProduct) {
                data.setCode(-1);
                data.setMsg("商品已下线");
                return data;
            }

            //PC产品图（可能有多个）
            cmsProduct.setPcProductImgList(bdFileService.getByTabNameFormId( "product_cms_lefttop",cmsProduct.getId()));

            File file = bdFileService.byId(cmsProduct.getAppPordImgUrlId());
            if (null != file) {
                cmsProduct.setAppPordImgUrl(file.getUrl());
            }

            // 聚合信息查询
            AggregationResponse aggregation = cmsProductService.findAggregationByProductId(productId);

            // 产品其他信息
            result.put("description", ProductUtils.getDescription(productId, "", 0));
            result.put("flowList", ProductUtils.getTaskFlow(productId, 1));
            result.put("materialList", ProductUtils.getTaskFlow(productId, 2));
            result.put("product", cmsProduct);
            result.put("aggregation", aggregation);
            result.put("showprice", cmsProduct.getShowprice());

            data.setData(result);
        } catch (Exception e) {
            e.printStackTrace();
            data.setCode(-1);
            data.setMsg("您的网络不稳定，请稍后再试。");
        }
        return data;
    }


    /**
     * 获取地区省/市/区
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/cities",method=RequestMethod.GET)
    public ResponseData cities(HttpServletRequest request) {
        ResponseData data = new ResponseData();

        try {
            int productId = NumberUtils.toInt(request.getParameter("productId"));
            if (productId == 0) {
                data.setMsg("产品不能为空");
                return data;
            }

            data.setData(productPriceService.findProductCities(productId, null));
        } catch (Exception e) {
            e.printStackTrace();
            data.setCode(-1);
            data.setMsg("您的网络不稳定，请稍后再试。");
        }
        return data;
    }
}