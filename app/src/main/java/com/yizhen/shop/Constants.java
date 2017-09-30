package com.yizhen.shop;


import com.yizhen.shop.util.FileUtil;

import java.io.File;

/**
 * Created by lewis on 16/6/22.
 */
public class Constants {
    public static String getApiConfig() {
        return getConfig() + "/v1_0.";
    }

    public static String getConfig() {
        int httpConfig = MainApplication.getInstance().getResources().getInteger(R.integer.HTTP_CONFIG);
        if (httpConfig == 1) {
            return API_URL_RELEASE;
        } else if (httpConfig == 2) {
            return API_URL_DEBUG;
        } else {
            return API_URL_RELEASE;
        }
    }

    public static final String API_URL_RELEASE = "http://api.putijiaye.com";
    public static final String API_URL_DEBUG = "http://api.putijiaye.com";
    public static final String IMG_HEAD = API_URL_RELEASE + "/";
    public static final String ADMIN_ID = "admin";
    public static final String GOODS_DETAIL_WEB = "http://mobile.hnido.com/goods/content.html?goods_id=";
    public static final String AUCTION_DETAIL_WEB = "http://api.putijiaye.com/v1_0.Auction/get_goods_detail?goods_id=";

    /**
     * ====login=======
     */
    public static final String _LOGIN = getApiConfig() + "Login/";
    public static final String REGISTER = _LOGIN + "register";
    public static final String LOGIN = _LOGIN + "auth";
    public static final String GET_START_PAGE = _LOGIN + "get_startpage";
    public static final String GET_EDIT_PASSWORD_CODE = _LOGIN + "get_find_password_code";
    public static final String FIND_PASSWORD = _LOGIN + "find_password";
    public static final String GET_REGISTER_CODE = _LOGIN + "get_register_code";

    /**
     * ====User========
     */
    public static final String _USER = getApiConfig() + "User/";
    public static final String GET_INFO = _USER + "get_info";
    public static final String UPDATE_USER_INFO = _USER + "update_user_info";
    public static final String GET_COUPON = _USER + "get_coupon";
    public static final String ADD_COLLECTION_GOODS = _USER + "add_collection_goods";
    public static final String CANCEL_COLLECTION_GOODS = _USER + "cancel_collection_goods";
    public static final String ADD_ADDRESS = _USER + "add_address";
    public static final String GET_ADDRESS_LIST = _USER + "get_address_list";
    public static final String GET_ADDRESS_DETAIL = _USER + "get_address_detail";
    public static final String UPDATE_ADDRESS = _USER + "update_address";
    public static final String ADD_CONSULT = _USER + "add_Consult";
    public static final String ORDER_LIST = _USER + "order_list";
    public static final String CONSULT_LIST = _USER + "consult_list";
    public static final String COLLECTION_LIST = _USER + "collection_list";
    public static final String MY_COUPON_LIST = _USER + "coupon_list";
    public static final String DEL_ADDRESS = _USER + "del_address";
    public static final String GET_BILL_INFO = _USER + "get_bill_info";
    public static final String GET_BILL_TYPE = _USER + "get_bill_type";
    public static final String SAVE_BILL = _USER + "save_bill";
    public static final String UPDATE_USER_PASSWORD = _USER + "update_user_password";
    public static final String ADD_FEEDBACK = _USER + "add_feedback";
    /**
     * ====GOODS========
     */
    public static final String _GOODS = getApiConfig() + "Goods/";
    public static final String GET_INDEX_LIST = _GOODS + "get_index_list";
    public static final String GET_GOODS_DETAIL = _GOODS + "get_goods_detail";
    public static final String GET_RECOMMEND_LIST = _GOODS + "get_recommend_list";
    public static final String GOODS_DETAIL_RECOMMEND = _GOODS + "goods_detail_recommend";
    public static final String GET_GOODS_CATEGORY_LIST = _GOODS + "get_goods_category_list";
    public static final String GET_EVALUATE_LIST = _GOODS + "get_evaluate_list";
    public static final String GET_CONSULT_LIST = _GOODS + "get_consult_list";
    public static final String GET_CATEGORY_GOODS = _GOODS + "get_category_goods";
    public static final String GET_CATEGORY_ADV_LIST = _GOODS + "get_category_adv_list";
    public static final String GET_TOPIC_DETAIL = _GOODS + "get_topic_detail";
    public static final String GET_RECOMMEND_TOPIC = _GOODS + "get_recommend_topic";
    public static final String GOODS_CART_RECOMMEND = _GOODS + "goods_cart_recommend";
    public static final String GET_NEW_GOODS = _GOODS + "get_new_goods";
    public static final String GET_OPENING_GOODS = _GOODS + "get_opening_goods";
    public static final String GET_SEARCH_GOODS = _GOODS + "get_search_goods";
    /**
     * ====ADV========
     */
    public static final String _ADV = getApiConfig() + "Adv/";
    public static final String GET_ADV_LIST = _ADV + "get_Adv_list";
    public static final String GET_AUCTION_ADV_LIST = _ADV + "get_Auction_Adv_list";
    /**
     * ====Coupon========
     */
    public static final String _COUPON = getApiConfig() + "Coupon/";
    public static final String COUPON_LIST = _COUPON + "coupon_list";
    /**
     * ====Auction========
     */
    public static final String _AUCTION = getApiConfig() + "Auction/";
    public static final String GET_AUCTION_INDEX_LIST = _AUCTION + "get_index_list";
    public static final String GET_AUCTION_THEME_LIST = _AUCTION + "get_theme_list";
    public static final String GET_AUCTION_CATEGORY_GOODS = _AUCTION + "get_category_goods";
    public static final String GET_AUCTION_GOODS_CATEGORY_LIST = _AUCTION + "get_goods_category_list";
    /**
     * ====Buy========
     */
    public static final String _BUY = getApiConfig() + "Buy/";
    public static final String PAYMENT_ORDER = _BUY + "payment_order";
    public static final String CREATE_ORDER = _BUY + "create_order";
    public static final String PAY_ORDER = _BUY + "pay_order";
    /**
     * ====Order========
     */
    public static final String _ORDER = getApiConfig() + "Order/";
    public static final String GET_ORDER_DETAIL = _ORDER + "get_order_detail";
    public static final String UNIFY_PAY = _ORDER + "unify_pay";
    public static final String APPLY_REFUND = _ORDER + "apply_refund";
    public static final String ORDER_CLOSE = _ORDER + "order_close";
    public static final String ORDER_TAKE_DELIVERY = _ORDER + "order_take_delivery";
    public static final String DELETE_ORDER = _ORDER + "delete_order";
    public static final String ADD_GOODS_EVALUATE = _ORDER + "add_goods_evaluate";
    public static final String GET_REFUND_DETAIL = _ORDER + "get_refund_detail";
    public static final String CANCEL_REFUND_APPLY = _ORDER + "cancel_refund_apply";
    public static final String ADD_REFUND_EXPRESS = _ORDER + "add_refund_express";
    /**
     * ====CART========
     */
    public static final String _CART = getApiConfig() + "Cart/";
    public static final String ADD_CART = _CART + "add_cart";
    public static final String GET_CART_NUM = _CART + "get_cart_num";
    public static final String GET_CART = _CART + "get_cart";
    public static final String DEL_CART = _CART + "del_cart";
    public static final String UPDATE_CART = _CART + "update_cart";
    /**
     * ====sms========
     */
    public static final String _SMS = getApiConfig() + "sms/";
    public static final String SMS_ACTIVITY_LIST = _SMS + "activity_list";

    /**
     * ====Upload=========
     */
    public static final String _Upload = getApiConfig() + "Upload/";
    //public static final String UPLOAD_IMGES = _Upload + "upload_images";
    public static final String UPLOAD_IMGES = _USER + "upload_avator";
    public static final String UPDATE_EVALUATE_IMG = _USER + "upload_evaluate_img";
    /**
     * ====WEB==========
     */
    public static final String _WEB = "http://oaweb.weihainan.com/";
    public static final String WEB_NOTICE_DETAIL = _WEB + "mycenter/notice_detail.html";
    public static final String WEB_ARTICLE_DETAIL = _WEB + "mycenter/article_detail.html";
    public static final String WEB_CREATE_COMPANY = _WEB + "open/create_company.html";
    public static final String WEB_HELP = _WEB + "open/content_page/alias/user_help.html";
    public static final String WEB_SHARE = _WEB + "mycenter/share.html";
    public static final String WEB_TASK_DISCUSS = _WEB + "open/task_discuss_info.html";
    public static final String WEB_ABOUT = _WEB + "open/content_page/alias/about.html";
    public static final String WEB_AGREEMENT = _WEB + "open/content_page/alias/user_agreement.html";
    public static final String WEB_EXPRESS = "http://mobile.hnido.com/index/express.html?order_id=";


    /**
     * ====图片地址=======
     */
    public static final String APP_PATH_ROOT = FileUtil.getRootPath().getAbsolutePath() + File.separator + "OA_splash";
}