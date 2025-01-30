package com.qurba.android.utils;

import com.qurba.android.BuildConfig;

public class Constants {
    public static final int RC_LOGOUT = 1881;
    public static final int RC_SIGN_IN = 1141;
    public static final int RC_UPDATE_REQUEST = 1131;
    public static final int EDIT_PROFILE = 1151;
    public static final int EDIT_PROFILE_SERVICE = 11213;

    //    public static final String EGYPT_COUNTRY_ID = "5bb4e2392e2979000f917b0e";

    public static final String DEFAULT_COUNTRY = "default-country";
    public static final String PLACE_CATEGORIES = "place-categories";
    public static final String EDIT_SERVICE = "EDIT_SERVICE";

    public static final String GUEST_MODEL = "guest-model";
    public static final String CART_ITEMS = "cart-items";

    public static final String MY_CART = "my-cart";

    public static final String VERSION = "version";
    public static final String LANGUAGE = "language";
    public static final String AUTHORIZATION = "Authorization";
    public static final String AUTH = "auth";
    public static final String LOGIN_GUEST = "login-guest";//
    public static final String REGISTER_PUSH_TOKEN = "register-push-token-guest";
    public static final String REGISTER_USER_PUSH_TOKEN = "register-push-token";
    public static final String APP_SETTINGS = "app-settings";//
    public static final String SELECTED_TAB = "trending";
    public static final String IS_RUNNTING = "is-running";
    public static final String IS_FROM_DETAILS = "is-from-details";

    public static final String ADD_MOBILE_TO_USER = "add-mobile-to-user";//
    public static final String VERIFY_CODE = "verify-code";//
    public static final String BRANCH_OBJECT = "branch-object";

    public static final String ANDROID = "ANDROID";
    public static final String LOGIN_FACEBOOK = "login-facebook";
    public static final String LOGIN_GOOGLE = "login-google";
    public static final String LOGIN_EMAIL = "login-email";
    public static final String SIGN_UP_EMAIL = "signup-email";
    public static final String SIGN_UP_EMAIL_VERIFY = "signup-email-verify";
    public static final String SIGN_UP_EMAIL_RESEND = "signup-email-resend";
    public static final String SIGN_UP_EMAIL_PASSWORD = "signup-email-password";
    public static final String LOGIN_PHONE = "login-mobile";
    public static final String SIGN_UP_PHONE = "signup-mobile";
    public static final String SIGN_UP_PHONE_RESEND = "signup-mobile-resend";
    public static final String SIGN_UP_PHONE_VERIFY = "signup-mobile-verify";
    public static final String SIGN_UP_PHONE_PASSWORD = "signup-mobile-password";
    public static final String GUEST = "guest";
    public static final String IS_MOBILE_VERIFIED = "isMobileVerified";
    public static final String REFRESH_ORDER_STATUS = "REFRESH_ORDER_STATUS";

    public static final String OFFERS_PRODUCTS = "offers-products";
    public static final String CART_PRODUCT = "cart-product";
    public static final String PRODUCT_ID = "product-id";
    public static final String ORDER_ID = "order-id";
    public static final String OFFERS = "offers";
    public static final String FAVORITED_OFFERS = "favoritedOffers";
    public static final String SPONSORED = "sponsored";
    public static final String PLACE = "place";
    public static final String PLACE_PRODUCTS = "get-place-products";
    public static final String MY_REVIEWS = "my-reviews";
    public static final String CLAIM = "claim";
    public static final String FAVORITE = "favorite";
    public static final String UNFAVORITE = "unfavorite";
    public static final String JWT = "JWT ";
    public static final String TOKEN = "token";
    public static final String CONGIO_TOKEN = "congito-token";
    public static final String LOCALE = "locale";
    public static final String FCM_TOKEN = "FCM_TOKEN";
    public static final String FIRST_TIME = "FIRST_TIME";
    public static final String ORDER_NOW = "order_now";
    public static final String COUNTRIES = "countries";
    public static final String ADDRESS = "address";
    public static final String APP_RUNNING = "APP_RUNNING";
    public static final String COMMENTS = "comments";
    public static final String IS_REPLY = "is-reply";
    public static final String IS_VOTED = "is-voted";
    public static final String COMMENT_ID = "comment-id";
    public static final String REPLY_ID = "reply-id";

    public static final String LIKE_PRODUCT = "like-product";
    public static final String DISLIKE_PRODUCT = "dislike-product";

    public static final String BASE_URL = BuildConfig.HOST;
    public static final String LOGGLY_BASE_URL = "https://logs-01.loggly.com/inputs/" + "06bf60aa-f876-4141-90c0-a5dec5c0c82f" + "/tag/";
    public static final String SHARE_BASE_URL = BuildConfig.SHARE_BASE_URL;
    public static final String BY_NAME_SHORT = "by-name-short";
    public static final String PLACES = "places";//products
    public static final String PRODUCTS = "products";//
    public static final String BY_NAME = "by-name";
    public static final String FOLLOW = "follow";
    public static final String FOLLOWED = "followed";
    public static final String UNFOLLOW = "unfollow";
    public static final String GET_PLACE_CITIES_AREAS = "get-place-cities-areas";

    public static final String USER_DATA = "user";
    public static final String Images = "images";
    public static final String QUERIES = "queries";
    public static final String NEARBY = "nearby";
    public static final String SEARCH = "search";
    public static final String CATEGORIES = "categories";
    public static final String REVIEWS = "reviews";
    public static final String HASH_TAGS = "hashtags";
    public static final String PAGE = "page";
    public static final String TRENDING_PLACE = "TRENDING_PLACE";
    public static final String TRENDING_HASHTAG = "TRENDING_HASHTAG";
    public static final String TRENDING_OFFER = "TRENDING_OFFER";
    public static final String SPONSORED_OFFER = "SPONSORED_OFFER";
    public static final String PLACE_JOINED = "PLACE_JOINED";
    public static final String PLACE_REVIEWS = "PLACE_REVIEWS";
    public static final String RECENT_SEARCHES = "recent-searches";

    public static final String OFFER_TYPE_DISCOUNT = "DISCOUNT";
    public static final String OFFER_TYPE_FREE_ITEMS = "FREE_ITEMS";
    public static final String OFFER_TYPE_FREE_DELIVERY = "FREE_DELIVERY";
    public static final String OFFER_TYPE_DISCOUNT_ON_ITEMS = "DISCOUNT_ON_ITEMS";
    public static final String OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU = "DISCOUNT_ON_ENTIRE_MENU";

     public static final String[] OFFER_TYPES = {
            OFFER_TYPE_DISCOUNT, OFFER_TYPE_FREE_ITEMS, OFFER_TYPE_DISCOUNT_ON_ITEMS, OFFER_TYPE_FREE_DELIVERY, OFFER_TYPE_DISCOUNT_ON_ENTIRE_MENU
    };

    public static final String OFFER_FILTER_CREATED_AT = "createdAt";
    public static final String OFFER_FILTER_NEAREST = "nearest";
    public static final String OFFER_FILTER_FAVORITES_COUNT = "favoritesCount";
    public static final String OFFER_FILTER_CLAIMS_COUNT = "claimsCount";

    public static final String CLAIMED_OFFER = "claimed_offer";
    public static final String FOLLOWED_PLACE = "followed_place";
    public static final String OFFER_POSITION = "position";
    public static final String PRODUCT_POSITION = "product_position";
    public static final String COMMENT_POSITION = "comment-position";

    public static final String PLACE_POSITION = "place_position";

    public static final String LAST_KNOWN_LAT = "lat";
    public static final String LAST_KNOWN_LNG = "lng";

    public static final int PAGE_START = 1;
    public static final int MAP = 0;
    public static final int LIST = 1;
    public static final String STATS = "stats";
    public static final String USER = "user";
    public static final String CHANGE_PASSWORD = "change-password";
    public static final String UPDATE_EMAIL = "update-email";
    public static final String UPDATE_EMAIL_VERIFY = "update-email-verify";
    public static final String CAT_ID = "cat_id";

    public static final String FORGOT_EMAIL_VERIFY = "forgot-password-email-verify";
    public static final String FORGOT_PASSWORD_EMAIL_PASSWORD = "forgot-password-email-password";
    public static final String FORGOT_PASSWORD_EMAIL = "forgot-password-email";

    public static final String FORGOT_PASSWORD_VERIFY = "forgot-password-mobile-verify";
    public static final String FORGOT_PASSWORD_MOBILE_PASSWORD = "forgot-password-mobile-password";
    public static final String FORGOT_PASSWORD_MOBILE = "forgot-password-mobile";

    public static final String ORDER_TYPE = "order-type";

    public static final String PLACE_LINK_TYPE = "place";
    public static final String OFFERS_LINK_TYPE = "offers";
    public static final String FILTER_MODEL = "filter-model";
    public static final String PLACE_FILTER_MODEL = "place-filter-model";

    public static final String SAVED_DELIVERY_ADDRESSES = "saved-delivery-address";

    public static final String SELECTED_DELIVERY_ADDRESS = "selected-delivery-address";
    public static final String SELECTED_COUNTRY = "selected-country";
    public static final String SELECTED_CITY = "selected-city";
    public static final String SELECTED_AREA = "selected-area";

    public static final String DELIVERY_COUNTRY = "delivery-country";
    public static final String DELIVERY_CITY = "delivery-city";
    public static final String DELIVERY_AREA = "delivery-area";

    public static final String ORDERED_OFFER = "ordered-offer";
    public static final String IS_ORDERING = "is-ordering";
    public static final String IS_PROFILE_EDITING = "is-profile-editing";
    public static final String IS_ORDER_FINISHED = "is-order-finished";
    public static final String PLACE_ID = "place-id";
    public static final String PLACE_UNIQUE_NAME = "unique-name";

    public static final String ORDERS = "orders";
    public static final String USER_ORDERS = "user-orders";///
    public static final String USER_LAST_ORDER = "get-user-last-order";
    public static final String USER_LAST_ADDRESS = "get-user-last-address";
    public static final String ORDER = "order";//
    public static final String DEFAULT_REALM_NAME = "myrealm.realm";//

    public static final String USER_GOOGLE_LOGIN_API_CALL = "USER_GOOGLE_LOGIN_API_CALL ";
    public static final String USER_GOOGLE_LOGIN_SUCCESS = "USER_GOOGLE_LOGIN_SUCCESS ";
    public static final String USER_FACEBOOK_LOGIN_SUCCESS = "USER_FACEBOOK_LOGIN_SUCCESS";

    public static final String USER_FACEBOOK_LOGIN_API_CALL = "USER_FACEBOOK_LOGIN_API_CALL";

    public static final String USER_GOOGLE_LOGIN_ERROR = "USER_GOOGLE_LOGIN_ERROR ";
    public static final String USER_FACEBOOK_LOGIN_ERROR = "USER_FACEBOOK_LOGIN_ERROR";

    public static final String USER_GOOGLE_LOGIN_CANCELED = "USER_GOOGLE_LOGIN_CANCELED ";
    public static final String USER_FACEBOOK_LOGIN_CANCELED = "USER_FACEBOOK_LOGIN_CANCELED";

    public static final String USER_BRANCH_IO_SUCCESS = "USER_BRANCH_IO_SUCCESS ";
    public static final String USER_BRANCH_IO_FAILED = "USER_BRANCH_IO_FAIL";

    public static final String USER_GOOGLE_LOGIN_BACKEND_SUCCESS = "USER_GOOGLE_LOGIN_BACKEND_SUCCESS ";
    public static final String USER_FACEBOOK_LOGIN_BACKEND_SUCCESS = "USER_FACEBOOK_LOGIN_BACKEND_SUCCESS";

    public static final String USER_GOOGLE_LOGIN_BACKEND_FAIL = "USER_GOOGLE_LOGIN_BACKEND_FAIL";
    public static final String USER_FACEBOOK_LOGIN_BACKEND_FAIL = "USER_FACEBOOK_LOGIN_BACKEND_FAIL";

    public static final String USER_RETRIEVE_NEAREST_AREA_ATTEMPT = "USER_RETRIEVE_NEAREST_AREA_ATTEMPT";
    public static final String USER_RETRIEVE_NEAREST_AREA_FAIL = "USER_RETRIEVE_NEAREST_AREA_FAIL";
    public static final String USER_RETRIEVE_NEAREST_AREA_SUCCESS = "USER_RETRIEVE_NEAREST_AREA_SUCCESS";


    public static final String USER_NO_INTERNET_CONNECTION = "USER_NO_INTERNET_CONNECTION";
    public static final String USER_ORDER_LIMIT_EXCEED = "USER_ORDER_LIMIT_EXCEED";
    public static final String USER_ORDER_SUBMIT_COMPLETE = "USER_ORDER_SUBMIT_COMPLETE";
    public static final String USER_ORDER_SUBMIT_SUCCESS = "USER_ORDER_SUBMIT_SUCCESS";
    public static final String USER_ORDER_SUBMIT_FAIL = "USER_ORDER_SUBMIT_FAIL";
    public static final String USER_ORDER_DELIVERY_AREA_SELECT_SUCCESS = "USER_ORDER_DELIVERY_AREA_SELECT_SUCCESS";
    public static final String USER_ORDER_DELIVERY_AREA_SELECT_FAIL = "USER_ORDER_DELIVERY_AREA_SELECT_FAIL";
    public static final String USER_NOTIFICATIONS_CLICK_SUCCESS = "USER_NOTIFICATIONS_CLICK_SUCCESS";
    public static final String USER_NOTIFICATIONS_CLICK_FAIL = "USER_NOTIFICATIONS_CLICK_FAIL";
    public static final String USER_ORDER_MY_ORDERS_CLICK_SUCCESS = "USER_ORDER_MY_ORDERS_CLICK_SUCCESS";
    public static final String USER_ORDER_MY_ORDERS_CLICK_FAIL = "USER_ORDER_MY_ORDERS_CLICK_FAIL";
    public static final String USER_VERIFY_MOBILE_SUCCESS = "USER_VERIFY_MOBILE_SUCCESS";
    public static final String USER_VERIFY_MOBILE_FAIL = "USER_VERIFY_MOBILE_FAIL";

    public static final String USER_SEND_SMS_MOBILE_SUCCESS = "USER_SEND_SMS_MOBILE_SUCCESS";
    public static final String USER_SEND_SMS_MOBILE_FAIL = "USER_SEND_SMS_MOBILE_FAIL";

    public static final String USER_RETRIEVE_OFFER_DETAILS_SUCCESS = "USER_RETRIEVE_OFFER_DETAILS_SUCCESS";
    public static final String USER_RETRIEVE_OFFER_DETAILS_FAIL = "USER_RETRIEVE_OFFER_DETAILS_SUCCESS";
    public static final String USER_RETRIEVE_OFFER_DETAILS_ATTEMPT = "USER_RETRIEVE_OFFER_DETAILS_ATTEMPT";

    public static final String USER_RETRIEVE_PRODUCT_DETAILS_SUCCESS = "USER_RETRIEVE_PRODUCT_DETAILS_SUCCESS";
    public static final String USER_RETRIEVE_PRODUCT_DETAILS_FAIL = "USER_RETRIEVE_PRODUCT_DETAILS_SUCCESS";
    public static final String USER_RETRIEVE_PRODUCT_DETAILS_ATTEMPT = "USER_RETRIEVE_PRODUCT_DETAILS_ATTEMPT";

    public static final String USER_RETRIEVE_PLACE_DETAILS_SUCCESS = "USER_RETRIEVE_PLACE_DETAILS_SUCCESS";
    public static final String USER_RETRIEVE_PLACE_DETAILS_FAIL = "USER_RETRIEVE_PLACE_DETAILS_SUCCESS";
    public static final String USER_RETRIEVE_PLACE_DETAILS_ATTEMPT = "USER_RETRIEVE_PLACE_DETAILS_ATTEMPT";

    public static final String USER_OFFERS_CUSTOMIZATION_SELECT_SUCCESS = "USER_OFFERS_CUSTOMIZATION_SELECT_SUCCESS";
    public static final String USER_OFFERS_CUSTOMIZATION_SELECT_FAIL = "USER_OFFERS_CUSTOMIZATION_SELECT_FAIL";
    public static final String USER_PRODUCT_CUSTOMIZATION_SELECT_SUCCESS = "USER_PRODUCT_CUSTOMIZATION_SELECT_SUCCESS";
    public static final String USER_PRODUCT_CUSTOMIZATION_SELECT_FAIL = "USER_PRODUCT_CUSTOMIZATION_SELECT_FAIL";
    public static final String USER_CART_ADD_ITEM_SUCCESS = "USER_CART_ADD_ITEM_SUCCESS";
    public static final String USER_CART_ADD_ITEM_FAIL = "USER_CART_ADD_ITEM_FAIL";
    public static final String USER_CART_EDIT_SUCCESS = "USER_CART_EDIT_SUCCESS";
    public static final String USER_CART_EDIT_FAIL = "USER_CART_EDIT_FAIL";
    public static final String USER_CART_CHECK_OUT_SUCCESS = "USER_CART_CHECK_OUT_SUCCESS";
    public static final String USER_CART_CHECK_OUT_FAIL = "USER_CART_CHECK_OUT_FAIL";
    public static final String USER_VIEW_CART_SUCCESS = "USER_VIEW_CART_SUCCESS";
    public static final String USER_VIEW_CART_FAIL = "USER_VIEW_CART_FAIL";
    public static final String USER_OFFER_ORDER_SUCCESS = "USER_OFFER_ORDER_SUCCESS";
    public static final String USER_OFFER_ORDER_FAIL = "USER_OFFER_ORDER_FAIL";
    public static final String USER_PRODUCT_ORDER_SUCCESS = "USER_PRODUCT_ORDER_SUCCESS";
    public static final String USER_PRODUCT_ORDER_FAIL = "USER_PRODUCT_ORDER_FAIL";

    public static final String USER_COGNITO_INIT_SUCCESS = "USER_COGNITO_INIT_SUCCESS";
    public static final String USER_COGNITO_INIT_FAIL = "USER_COGNITO_INIT_FAIL";


    public static final String USER_ONBOARDING_GET_STARTED_SUCCESS = "USER_ONBOARDING_GET_STARTED_SUCCESS";
    public static final String USER_ONBOARDING_GET_STARTED_FAIL = "USER_ONBOARDING_GET_STARTED_FAIL";
    public static final String USER_QURBA_ACCESS_LOCATION_ALLOW_SUCCESS = "USER_QURBA_ACCESS_LOCATION_ALLOW_SUCCESS";
    public static final String USER_QURBA_ACCESS_LOCATION_ALLOW_FAIL = "USER_QURBA_ACCESS_LOCATION_ALLOW_FAIL";
    public static final String USER_QURBA_ACCESS_LOCATION_DENY_SUCCESS = "USER_QURBA_ACCESS_LOCATION_DENY_SUCCESS";
    public static final String USER_QURBA_ACCESS_LOCATION_DENY_FAIL = "USER_QURBA_ACCESS_LOCATION_DENY_FAIL";
    public static final String USER_ADDRESS_COMPONENT_CONFIRM_ADDRESS_SUCCESS = "USER_ADDRESS_COMPONENT_CONFIRM_ADDRESS_SUCCESS";
    public static final String USER_ADDRESS_COMPONENT_CONFIRM_ADDRESS_FAIL = "USER_ADDRESS_COMPONENT_CONFIRM_ADDRESS_FAIL";
    public static final String USER_ADDRESS_COMPONENT_CHANGE_ADDRESS_SUCCESS = "USER_ADDRESS_COMPONENT_CHANGE_ADDRESS_SUCCESS";
    public static final String USER_ADDRESS_COMPONENT_CHANGE_ADDRESS_FAIL = "USER_ADDRESS_COMPONENT_CHANGE_ADDRESS_FAIL";
    public static final String USER_ADDRESS_COMPONENT_SEARCH_SUCCESS = "USER_ADDRESS_COMPONENT_SEARCH_SUCCESS";
    public static final String USER_ADDRESS_COMPONENT_SEARCH_FAIL = "USER_ADDRESS_COMPONENT_SEARCH_FAIL";
    public static final String USER_ADDRESS_COMPONENT_SELECT_AREA_SUCCESS = "USER_ADDRESS_COMPONENT_SELECT_AREA_SUCCESS";
    public static final String USER_ADDRESS_COMPONENT_SELECT_AREA_FAIL = "USER_ADDRESS_COMPONENT_SELECT_AREA_FAIL";
    public static final String USER_ADDRESS_COMPONENT_SELECT_CITY_SUCCESS = "USER_ADDRESS_COMPONENT_SELECT_CITY_SUCCESS";
    public static final String USER_ADDRESS_COMPONENT_SELECT_CITY_FAIL = "USER_ADDRESS_COMPONENT_SELECT_CITY_FAIL";
    public static final String USER_ADDRESS_ADD_SUCCESS = "USER_ADDRESS_ADD_SUCCESS";
    public static final String USER_ADDRESS_ADD_FAIL = "USER_ADDRESS_ADD_FAIL";
    public static final String USER_ADDRESS_ADD_SUBMIT = "USER_ADDRESS_ADD_SUBMIT";

    public static final String USER_ADDRESS_UPDATE_SUCCESS = "USER_ADDRESS_UPDATE_SUCCESS";
    public static final String USER_ADDRESS_UPDATE_FAIL = "USER_ADDRESS_UPDATE_FAIL";
    public static final String USER_ADDRESS_UPDATE_SUBMIT = "USER_ADDRESS_UPDATE_SUBMIT";

    public static final String USER_ADDRESS_DELETE_SUCCESS = "USER_ADDRESS_DELETE_SUCCESS";
    public static final String USER_ADDRESS_DELETE_FAIL = "USER_ADDRESS_DELETE_FAIL";
    public static final String USER_ADDRESS_DELETE_SUBMIT = "USER_ADDRESS_DELETE_SUBMIT";

    public static final String USER_GET_CITIES_SUCCESS = "USER_GET_CITIES_SUCCESS";
    public static final String USER_GET_CITIES_FAIL = "USER_GET_CITIES_FAIL";

    public static final String USER_DEFAULT_COUNTRY_SUCCESS = "USER_DEFAULT_COUNTRY_SUCCESS";
    public static final String USER_DEFAULT_COUNTRY_FAIL = "USER_DEFAULT_COUNTRY_FAIL";
    public static final String USER_DEFAULT_COUNTRY_ATTEMPT = "USER_DEFAULT_COUNTRY_ATTEMPT";

    public static final String USER_VERSION_CHECKING_SUCCESS = "USER_VERSION_CHECKING_SUCCESS";
    public static final String USER_VERSION_CHECKING_FAIL = "USER_VERSION_CHECKING_SUCCESS";
    public static final String USER_VERSION_CHECKING_ATTEMPT = "USER_VERSION_CHECKING_ATTEMPT";

    public static final String USER_GET_CATEGORIES_ATTEMPT = "USER_GET_CATEGORIES_ATTEMPT";
    public static final String USER_GET_CATEGORIES_FAIL = "USER_GET_CATEGORIES_FAIL";
    public static final String USER_GET_CATEGORIES_SUCCESS = "USER_GET_CATEGORIES_SUCCESS";

    public static final String USER_GET_AREA_BY_CITY_SUCCESS = "USER_GET_AREA_BY_CITY_SUCCESS";
    public static final String USER_GET_AREA_BY_CITY_FAIL = "USER_GET_AREA_BY_CITY_FAIL";
    public static final String USER_GET_AREA_BY_CITY_ATTEMPT = "USER_GET_AREA_BY_CITY_ATTEMPT";

    public static final String USER_LOGIN_GUEST_SUCCESS = "USER_LOGIN_GUEST_SUCCESS";
    public static final String USER_LOGIN_GUEST_FAIL = "USER_LOGIN_GUEST_FAIL";

    public static final String USER_SEND_FCM_TOKEN_SUCCESS = "USER_SEND_FCM_TOKEN_SUCCESS";
    public static final String USER_SEND_FCM_TOKEN_FAIL = "USER_SEND_FCM_TOKEN_FAIL";

    public static final String USER_SEARCH_AREA_SUCCESS = "USER_SEARCH_AREA_SUCCESS";
    public static final String USER_SEARCH_AREA_FAIL = "USER_SEARCH_AREA_FAIL";

    public static final String USER_DETECT_IF_AREA_IS_SUPPORTED_SUCCESS = "USER_DETECT_IF_AREA_IS_SUPPORTED_SUCCESS";
    public static final String USER_DETECT_IF_AREA_IS_SUPPORTED_FAIL = "USER_DETECT_IF_AREA_IS_SUPPORTED_FAIL";

    public static final String USER_ORDER_DETAILS_FAIL = "USER_ORDER_DETAILS_FAIL";
    public static final String USER_ORDER_DETAILS_SUCCESS = "USER_ORDER_DETAILS_SUCCESS";

    public static final String USER_GET_DELIVERY_FEES_SUCCESS = "USER_GET_DELIVERY_FEES_SUCCESS";
    public static final String USER_GET_DELIVERY_FEES_FAIL = "USER_GET_DELIVERY_FEES_FAIL";

    public static final String USER_VOTE_AREA_FAIL = "USER_VOTE_AREA_FAIL";
    public static final String USER_VOTE_AREA_SUCCESS = "USER_VOTE_AREA_SUCCESS";
    public static final String USER_VOTE_AREA_ATTEMPT = "USER_VOTE_AREA_ATTEMPT";

    public static final String USER_GET_TOTAL_SAVINGS_ATTEMPT = "USER_GET_TOTAL_SAVINGS_ATTEMPT";
    public static final String USER_GET_TOTAL_SAVINGS_SUCCESS = "USER_GET_TOTAL_SAVINGS_SUCCESS";
    public static final String USER_GET_TOTAL_SAVINGS_FAIL = "USER_GET_TOTAL_SAVINGS_FAIL";

    public static final String USER_VALIDATE_ORDER_ATTEMPT = "USER_VALIDATE_ORDER_ATTEMPT";
    public static final String USER_VALIDATE_ORDER_SUCCESS = "USER_VALIDATE_ORDER_SUCCESS";
    public static final String USER_VALIDATE_ORDER_FAIL = "USER_VALIDATE_ORDER_FAIL";

    public static final String USER_LIKE_OFFER_ATTEMPT = "USER_LIKE_OFFER_ATTEMPT";
    public static final String USER_LIKE_OFFER_SUCCESS = "USER_LIKE_OFFER_SUCCESS";
    public static final String USER_LIKE_OFFER_FAIL = "USER_LIKE_OFFER_FAIL";

    public static final String USER_UNLIKE_OFFER_ATTEMPT = "USER_UNLIKE_OFFER_ATTEMPT";
    public static final String USER_UNLIKE_OFFER_SUCCESS = "USER_UNLIKE_OFFER_SUCCESS";
    public static final String USER_UNLIKE_OFFER_FAIL = "USER_UNLIKE_OFFER_FAIL";

    public static final String USER_SHARE_OFFER_ATTEMPT = "USER_SHARE_OFFER_ATTEMPT";
    public static final String USER_SHARE_OFFER_SUCCESS = "USER_SHARE_OFFER_SUCCESS";
    public static final String USER_SHARE_OFFER_FAIL = "USER_SHARE_OFFER_FAIL";

    public static final String USER_SHARE_PRODUCT_ATTEMPT = "USER_SHARE_PRODUCT_ATTEMPT";
    public static final String USER_SHARE_PRODUCT_SUCCESS = "USER_SHARE_PRODUCT_SUCCESS";
    public static final String USER_SHARE_PRODUCT_FAIL = "USER_SHARE_PRODUCT_FAIL";

    public static final String USER_GET_ACTIVE_ORDERS_ATTEMPT = "USER_GET_ACTIVE_ORDERS_ATTEMPT";
    public static final String USER_GET_ACTIVE_ORDERS_FAIL = "USER_GET_ACTIVE_ORDERS_FAIL";
    public static final String USER_GET_ACTIVE_ORDERS_SUCCESS = "USER_GET_ACTIVE_ORDERS_SUCCESS";

    public static final String USER_GET_OFFERS_ATTEMPT = "USER_GET_ORDER_NOW_ATTEMPT";
    public static final String USER_GET_OFFERS_FAIL = "USER_GET_OFFERS_FAIL";
    public static final String USER_GET_OFFERS_SUCCESS = "USER_GET_OFFERS_SUCCESS";

    public static final String USER_GET_PLACES_ATTEMPT = "USER_GET_PLACES_ATTEMPT";
    public static final String USER_GET_PLACES_FAIL = "USER_GET_PLACES_FAIL";
    public static final String USER_GET_PLACES_SUCCESS = "USER_GET_PLACES_SUCCESS";

    public static final String USER_GET_ORDER_NOW_ATTEMPT = "USER_GET_ORDER_NOW_ATTEMPT";
    public static final String USER_GET_ORDER_NOW_FAIL = "USER_GET_ORDER_NOW_FAIL";
    public static final String USER_GET_ORDER_NOW_SUCCESS = "USER_GET_ORDER_NOW_SUCCESS";

    public static final String USER_GET_FEATURED_PLACES_ATTEMPT = "USER_GET_FEATURED_PLACES_ATTEMPT";
    public static final String USER_GET_FEATURED_PLACES_FAIL = "USER_GET_FEATURED_PLACES_FAIL";
    public static final String USER_GET_FEATURED_PLACES_SUCCESS = "USER_GET_FEATURED_PLACES_SUCCESS";

    public static final String USER_GET_SIMILAR_PLACES_ATTEMPT = "USER_GET_SIMILAR_PLACES_ATTEMPT";
    public static final String USER_GET_SIMILAR_PLACES_FAIL = "USER_GET_SIMILAR_PLACES_FAIL";
    public static final String USER_GET_SIMILAR_PLACES_SUCCESS = "USER_GET_SIMILAR_PLACES_SUCCESS";

    public static final String USER_GET_PLACE_OFFERS_ATTEMPT = "USER_GET_PLACE_OFFERS_ATTEMPT";
    public static final String USER_GET_PLACE_OFFERS_FAIL = "USER_GET_PLACE_OFFERS_FAIL";
    public static final String USER_GET_PLACE_OFFERS_SUCCESS = "USER_GET_PLACE_OFFERS_SUCCESS";

    public static final String USER_GET_PLACE_INFO_ATTEMPT = "USER_GET_PLACE_INFO_ATTEMPT";
    public static final String USER_GET_PLACE_INFO_FAIL = "USER_GET_PLACE_INFO_FAIL";
    public static final String USER_GET_PLACE_INFO_SUCCESS = "USER_GET_PLACE_INFO_SUCCESS";

    public static final String USER_GET_PLACE_PRODUCTS_ATTEMPT = "USER_GET_PLACE_PRODUCTS_ATTEMPT";
    public static final String USER_GET_PLACE_PRODUCTS_FAIL = "USER_GET_PLACE_PRODUCTS_FAIL";
    public static final String USER_GET_PLACE_PRODUCTS_SUCCESS = "USER_GET_PLACE_PRODUCTS_SUCCESS";

    public static final String USER_OPEN_IMAGE_GALLERY = "USER_OPEN_IMAGE_GALLERY";
    public static final String USER_OPEN_IMAGE_PREVEIEW = "USER_OPEN_IMAGE_PREVEIEW";

    public static final String USER_GET_OFFER_COMMENTS_ATTEMPT = "USER_GET_OFFER_COMMENTS_ATTEMPT";
    public static final String USER_GET_OFFER_COMMENTS_SUCCESS = "USER_GET_OFFER_COMMENTS_SUCCESS";
    public static final String USER_GET_OFFER_COMMENTS_FAIL = "USER_GET_OFFER_COMMENTS_FAIL";

    public static final String USER_ADD_OFFER_COMMENT_ATTEMPT = "USER_ADD_OFFER_COMMENT_ATTEMPT";
    public static final String USER_ADD_OFFER_COMMENT_SUCCESS = "USER_ADD_OFFER_COMMENT_SUCCESS";
    public static final String USER_ADD_OFFER_COMMENT_FAIL = "USER_ADD_OFFER_COMMENT_FAIL";

    public static final String USER_UPDATE_OFFER_COMMENT_ATTEMPT = "USER_UPDATE_OFFER_COMMENT_ATTEMPT";
    public static final String USER_UPDATE_OFFER_COMMENT_SUCCESS = "USER_UPDATE_OFFER_COMMENT_SUCCESS";
    public static final String USER_UPDATE_OFFER_COMMENT_FAIL = "USER_UPDATE_OFFER_COMMENT_FAIL";

    public static final String USER_DELETE_OFFER_COMMENT_ATTEMPT = "USER_DELETE_OFFER_COMMENT_ATTEMPT";
    public static final String USER_DELETE_OFFER_COMMENT_SUCCESS = "USER_DELETE_OFFER_COMMENT_SUCCESS";
    public static final String USER_DELETE_OFFER_COMMENT_FAIL = "USER_DELETE_OFFER_COMMENT_FAIL";

    public static final String USER_GET_OFFER_COMMENT_REPLIES_ATTEMPT = "USER_GET_OFFER_COMMENT_REPLIES_ATTEMPT";
    public static final String USER_GET_OFFER_COMMENT_REPLIES_SUCCESS = "USER_GET_OFFER_COMMENT_REPLIES_SUCCESS";
    public static final String USER_GET_OFFER_COMMENT_REPLIES_FAIL = "USER_GET_OFFER_COMMENT_REPLIES_SUCCESS";

    public static final String USER_ADD_OFFER_COMMENT_REPLY_ATTEMPT = "USER_ADD_OFFER_COMMENT_REPLY_ATTEMPT";
    public static final String USER_ADD_OFFER_COMMENT_REPLY_SUCCESS = "USER_ADD_OFFER_COMMENT_REPLY_SUCCESS";
    public static final String USER_ADD_OFFER_COMMENT_REPLY_FAIL = "USER_ADD_OFFER_COMMENT_REPLY_FAIL";

    public static final String USER_UPDATE_OFFER_COMMENT_REPLY_ATTEMPT = "USER_UPDATE_OFFER_COMMENT_REPLY_ATTEMPT";
    public static final String USER_UPDATE_OFFER_COMMENT_REPLY_SUCCESS = "USER_UPDATE_OFFER_COMMENT_REPLY_SUCCESS";
    public static final String USER_UPDATE_OFFER_COMMENT_REPLY_FAIL = "USER_UPDATE_OFFER_COMMENT_REPLY_FAIL";

    public static final String USER_DELETE_OFFER_COMMENT_REPLY_ATTEMPT = "USER_DELETE_OFFER_COMMENT_REPLY_ATTEMPT";
    public static final String USER_DELETE_OFFER_COMMENT_REPLY_SUCCESS = "USER_DELETE_OFFER_COMMENT_REPLY_SUCCESS";
    public static final String USER_DELETE_OFFER_COMMENT_REPLY_FAIL = "USER_DELETE_OFFER_COMMENT_REPLY_FAIL";

    public static final String USER_GET_PRODUCT_COMMENTS_ATTEMPT = "USER_GET_PRODUCT_COMMENTS_ATTEMPT";
    public static final String USER_GET_PRODUCT_COMMENTS_SUCCESS = "USER_GET_PRODUCT_COMMENTS_SUCCESS";
    public static final String USER_GET_PRODUCT_COMMENTS_FAIL = "USER_GET_PRODUCT_COMMENTS_FAIL";

    public static final String USER_ADD_PRODUCT_COMMENT_ATTEMPT = "USER_ADD_PRODUCT_COMMENT_ATTEMPT";
    public static final String USER_ADD_PRODUCT_COMMENT_SUCCESS = "USER_ADD_PRODUCT_COMMENT_SUCCESS";
    public static final String USER_ADD_PRODUCT_COMMENT_FAIL = "USER_ADD_PRODUCT_COMMENT_FAIL";

    public static final String USER_UPDATE_PRODUCT_COMMENT_ATTEMPT = "USER_UPDATE_PRODUCT_COMMENT_ATTEMPT";
    public static final String USER_UPDATE_PRODUCT_COMMENT_SUCCESS = "USER_UPDATE_PRODUCT_COMMENT_SUCCESS";
    public static final String USER_UPDATE_PRODUCT_COMMENT_FAIL = "USER_UPDATE_PRODUCT_COMMENT_FAIL";

    public static final String USER_DELETE_PRODUCT_COMMENT_ATTEMPT = "USER_DELETE_PRODUCT_COMMENT_ATTEMPT";
    public static final String USER_DELETE_PRODUCT_COMMENT_SUCCESS = "USER_DELETE_PRODUCT_COMMENT_SUCCESS";
    public static final String USER_DELETE_PRODUCT_COMMENT_FAIL = "USER_DELETE_PRODUCT_COMMENT_FAIL";

    public static final String USER_GET_PRODUCT_COMMENT_REPLIES_ATTEMPT = "USER_GET_PRODUCT_COMMENT_REPLIES_ATTEMPT";
    public static final String USER_GET_PRODUCT_COMMENT_REPLIES_SUCCESS = "USER_GET_PRODUCT_COMMENT_REPLIES_SUCCESS";
    public static final String USER_GET_PRODUCT_COMMENT_REPLIES_FAIL = "USER_GET_PRODUCT_COMMENT_REPLIES_FAIL";

    public static final String USER_ADD_PRODUCT_COMMENT_REPLY_ATTEMPT = "USER_ADD_PRODUCT_COMMENT_REPLY_ATTEMPT";
    public static final String USER_ADD_PRODUCT_COMMENT_REPLY_SUCCESS = "USER_ADD_PRODUCT_COMMENT_REPLY_SUCCESS";
    public static final String USER_ADD_PRODUCT_COMMENT_REPLY_FAIL = "USER_ADD_PRODUCT_COMMENT_REPLY_FAIL";

    public static final String USER_UPDATE_PRODUCT_COMMENT_REPLY_ATTEMPT = "USER_UPDATE_PRODUCT_COMMENT_REPLY_ATTEMPT";
    public static final String USER_UPDATE_PRODUCT_COMMENT_REPLY_SUCCESS = "USER_UPDATE_PRODUCT_COMMENT_REPLY_SUCCESS";
    public static final String USER_UPDATE_PRODUCT_COMMENT_REPLY_FAIL = "USER_UPDATE_PRODUCT_COMMENT_REPLY_FAIL";

    public static final String USER_DELETE_PRODUCT_COMMENT_REPLY_ATTEMPT = "USER_DELETE_PRODUCT_COMMENT_REPLY_ATTEMPT";
    public static final String USER_DELETE_PRODUCT_COMMENT_REPLY_SUCCESS = "USER_DELETE_PRODUCT_COMMENT_REPLY_SUCCESS";
    public static final String USER_DELETE_PRODUCT_COMMENT_REPLY_FAIL = "USER_DELETE_PRODUCT_COMMENT_REPLY_FAIL";

    public static final String USER_LIKE_OFFER_COMMENT_ATTEMPT = "USER_LIKE_OFFER_COMMENT_ATTEMPT";
    public static final String USER_LIKE_OFFER_COMMENT_SUCCESS = "USER_LIKE_OFFER_COMMENT_SUCCESS";
    public static final String USER_LIKE_OFFER_COMMENT_FAIL = "USER_LIKE_OFFER_COMMENT_FAIL";

    public static final String USER_UNLIKE_OFFER_COMMENT_ATTEMPT = "USER_UNLIKE_OFFER_COMMENT_ATTEMPT";
    public static final String USER_UNLIKE_OFFER_COMMENT_SUCCESS = "USER_UNLIKE_OFFER_COMMENT_ATTEMPT";
    public static final String USER_UNLIKE_OFFER_COMMENT_FAIL = "USER_UNLIKE_OFFER_COMMENT_FAIL";

    public static final String USER_LIKE_PRODUCT_COMMENT_ATTEMPT = "USER_LIKE_PRODUCT_COMMENT_ATTEMPT";
    public static final String USER_LIKE_PRODUCT_COMMENT_SUCCESS = "USER_LIKE_PRODUCT_COMMENT_SUCCESS";
    public static final String USER_LIKE_PRODUCT_COMMENT_FAIL = "USER_LIKE_PRODUCT_COMMENT_FAIL";

    public static final String USER_UNLIKE_PRODUCT_COMMENT_ATTEMPT = "USER_UNLIKE_PRODUCT_COMMENT_ATTEMPT";
    public static final String USER_UNLIKE_PRODUCT_COMMENT_SUCCESS = "USER_UNLIKE_PRODUCT_COMMENT_ATTEMPT";
    public static final String USER_UNLIKE_PRODUCT_COMMENT_FAIL = "USER_UNLIKE_PRODUCT_COMMENT_FAIL";

    public static final String USER_LIKE_PLACE_COMMENT_ATTEMPT = "USER_LIKE_PLACET_COMMENT_ATTEMPT";
    public static final String USER_LIKE_PLACE_COMMENT_SUCCESS = "USER_LIKE_PLACE_COMMENT_SUCCESS";
    public static final String USER_LIKE_PLACE_COMMENT_FAIL = "USER_LIKE_PLACE_COMMENT_FAIL";

    public static final String USER_UNLIKE_PLACE_COMMENT_ATTEMPT = "USER_UNLIKE_PLACE_COMMENT_ATTEMPT";
    public static final String USER_UNLIKE_PLACE_COMMENT_SUCCESS = "USER_UNLIKE_PLACE_COMMENT_ATTEMPT";
    public static final String USER_UNLIKE_PLACE_COMMENT_FAIL = "USER_UNLIKE_PLACE_COMMENT_FAIL";

    public static final String USER_GET_PLACE_COMMENTS_ATTEMPT = "USER_GET_PLACE_COMMENTS_ATTEMPT";
    public static final String USER_GET_PLACE_COMMENTS_SUCCESS = "USER_GET_PLACE_COMMENTS_SUCCESS";
    public static final String USER_GET_PLACE_COMMENTS_FAIL = "USER_GET_PLACE_COMMENTS_FAIL";

    public static final String USER_ADD_PLACE_COMMENT_ATTEMPT = "USER_ADD_PLACE_COMMENT_ATTEMPT";
    public static final String USER_ADD_PLACE_COMMENT_SUCCESS = "USER_ADD_PLACE_COMMENT_SUCCESS";
    public static final String USER_ADD_PLACE_COMMENT_FAIL = "USER_ADD_PLACE_COMMENT_FAIL";

    public static final String USER_UPDATE_PLACE_COMMENT_ATTEMPT = "USER_UPDATE_PLACE_COMMENT_ATTEMPT";
    public static final String USER_UPDATE_PLACE_COMMENT_SUCCESS = "USER_UPDATE_PLACE_COMMENT_SUCCESS";
    public static final String USER_UPDATE_PLACE_COMMENT_FAIL = "USER_UPDATE_PLACE_COMMENT_FAIL";

    public static final String USER_DELETE_PLACE_COMMENT_ATTEMPT = "USER_DELETE_PLACE_COMMENT_ATTEMPT";
    public static final String USER_DELETE_PLACE_COMMENT_SUCCESS = "USER_DELETE_PLACE_COMMENT_SUCCESS";
    public static final String USER_DELETE_PLACE_COMMENT_FAIL = "USER_DELETE_PLACE_COMMENT_FAIL";

    public static final String USER_GET_PLACE_COMMENT_REPLIES_ATTEMPT = "USER_GET_PLACE_COMMENT_REPLIES_ATTEMPT";
    public static final String USER_GET_PLACE_COMMENT_REPLIES_SUCCESS = "USER_GET_PLACE_COMMENT_REPLIES_SUCCESS";
    public static final String USER_GET_PLACE_COMMENT_REPLIES_FAIL = "USER_GET_PLACE_COMMENT_REPLIES_FAIL";

    public static final String USER_DELETE_PLACE_COMMENT_REPLY_ATTEMPT = "USER_DELETE_PLACE_COMMENT_REPLY_ATTEMPT";
    public static final String USER_DELETE_PLACE_COMMENT_REPLY_SUCCESS = "USER_DELETE_PLACE_COMMENT_REPLY_SUCCESS";
    public static final String USER_DELETE_PLACE_COMMENT_REPLY_FAIL = "USER_DELETE_PLACE_COMMENT_REPLY_FAIL";

    public static final String USER_UPDATE_PLACE_COMMENT_REPLY_ATTEMPT = "USER_UPDATE_PLACE_COMMENT_REPLY_ATTEMPT";
    public static final String USER_UPDATE_PLACE_COMMENT_REPLY_SUCCESS = "USER_UPDATE_PLACE_COMMENT_REPLY_SUCCESS";
    public static final String USER_UPDATE_PLACE_COMMENT_REPLY_FAIL = "USER_UPDATE_PLACE_COMMENT_REPLY_FAIL";

    public static final String USER_ADD_PLACE_COMMENT_REPLY_ATTEMPT = "USER_ADD_PLACE_COMMENT_REPLY_ATTEMPT";
    public static final String USER_ADD_PLACE_COMMENT_REPLY_SUCCESS = "USER_ADD_PLACE_COMMENT_REPLY_SUCCESS";
    public static final String USER_ADD_PLACE_COMMENT_REPLY_FAIL = "USER_ADD_PLACE_COMMENT_REPLY_FAIL";

    public static final String USER_CHANGE_LANGUAGE_SUCCESS = "USER_CHANGE_LANGUAGE_SUCCESS";
    public static final String USER_CHANGE_LANGUAGE_FAIL = "USER_CHANGE_LANGUAGE_FAIL";
    public static final String USER_CHANGE_LANGUAGE_ATTEMPT = "USER_CHANGE_LANGUAGE_ATTEMPT";

    public static final String USER_BRANCH_DEEP_LINK_SUCCESS = "USER_BRANCH_DEEP_LINK_SUCCESS";
    public static final String USER_BRANCH_DEEP_LINK_FAIL = "USER_BRANCH_DEEP_LINK_FAIL";

    public static final String USER_RETRIEVE_HIS_OFFER_DETAILS_SUCCESS = "USER_RETRIEVE_HIS_OFFER_DETAILS_SUCCESS";
    public static final String USER_RETRIEVE_HIS_OFFER_DETAILS_FAIL = "USER_RETRIEVE_HIS_OFFER_DETAILS_FAIL";
    public static final String USER_RETRIEVE_HIS_OFFER_DETAILS_ATTEMPT = "USER_RETRIEVE_HIS_OFFER_DETAILS_ATTEMPT";

    public static final String USER_RETRIEVE_HIS_PLACES_DETAILS_SUCCESS = "USER_RETRIEVE_HIS_PLACES_DETAILS_SUCCESS";
    public static final String USER_RETRIEVE_HIS_PLACES_DETAILS_FAIL = "USER_RETRIEVE_HIS_PLACES_DETAILS_FAIL";
    public static final String USER_RETRIEVE_HIS_PLACES_DETAILS_ATTEMPT = "USER_RETRIEVE_HIS_PLACES_DETAILS_ATTEMPT";

    public static final String USER_RETRIEVE_LIKED_MEALS_SUCCESS = "USER_RETRIEVE_LIKED_MEALS_SUCCESS";
    public static final String USER_RETRIEVE_LIKED_MEALS_FAIL = "USER_RETRIEVE_LIKED_MEALS_FAIL";
    public static final String USER_RETRIEVE_LIKED_MEALS_ATTEMPT = "USER_RETRIEVE_LIKED_MEALS_ATTEMPT";

    public static final String USER_CLEAR_CART_SUCCESS = "USER_CLEAR_CART_SUCCESS";
    public static final String USER_CLEAR_CART_FAIL = "USER_CLEAR_CART_SUCCESS";
    public static final String USER_CLEAR_CART_ATTEMPT = "USER_CLEAR_CART_ATTEMPT";

    public static final String USER_GET_CART_SUCCESS = "USER_GET_CART_SUCCESS";
    public static final String USER_GET_CART_FAIL = "USER_GET_CART_FAIL";
    public static final String USER_GET_CART_ATTAMPT = "USER_GET_CART_ATTAMPT";

    public static final String USER_UPDATE_CART_NOTE_SUCCESS = "USER_UPDATE_CART_NOTE_SUCCESS";
    public static final String USER_UPDATE_CART_NOTE_FAIL = "USER_UPDATE_CART_NOTE_FAIL";
    public static final String USER_UPDATE_CART_NOTE_ATTEMPT = "USER_UPDATE_CART_NOTE_ATTEMPT";

    public static final String USER_UPDATE_CART_ITEM_QTY_SUCCESS = "USER_UPDATE_CART_ITEM_QTY_SUCCESS";
    public static final String USER_UPDATE_CART_ITEM_QTY_FAIL = "USER_UPDATE_CART_ITEM_QTY_FAIL";
    public static final String USER_UPDATE_CART_ITEM_QTY_ATTEMPT = "USER_UPDATE_CART_ITEM_QTY_ATTEMPT";

    public static final String USER_ADD_OFFER_TO_CART_SUCCESS = "USER_ADD_OFFER_TO_CART_SUCCESS";
    public static final String USER_ADD_OFFER_TO_CART_FAIL = "USER_ADD_OFFER_TO_CART_FAIL";
    public static final String USER_ADD_OFFER_TO_CART_ATTEMPT = "USER_ADD_OFFER_TO_CART_ATTEMPT";

    public static final String USER_DELETE_ITEM_FROM_CART_SUCCESS = "USER_DELETE_ITEM_FROM_CART_SUCCESS";
    public static final String USER_DELETE_ITEM_FROM_CART_FAIL = "USER_DELETE_ITEM_FROM_CART_FAIL";
    public static final String USER_DELETE_ITEM_FROM_CART_ATTEMPT = "USER_DELETE_ITEM_FROM_CART_ATTEMPT";

    public static final String USER_ADD_PRODUCT_TO_CART_SUCCESS = "USER_ADD_PRODUCT_TO_CART_SUCCESS";
    public static final String USER_ADD_PRODUCT_TO_CART_FAIL = "USER_ADD_PRODUCT_TO_CART_FAIL";
    public static final String USER_ADD_PRODUCT_TO_CART_ATTEMPT = "USER_ADD_PRODUCT_TO_CART_ATTEMPT";

    public static final String USER_UPDATE_CART_AREA_SUCCESS = "USER_UPDATE_CART_AREA_SUCCESS";
    public static final String USER_UPDATE_CART_AREA_FAIL = "USER_UPDATE_CART_AREA_FAIL";
    public static final String USER_UPDATE_CART_AREA_ATTEMPT = "USER_UPDATE_CART_AREA_ATTEMPT";

    public static final String UPDATE_PLACE_CRASH = "UPDATE_PLACE_CRASH";
    public static final String GET_NEAREST_AREAS_CRASH = "GET_NEAREST_AREAS_CRASH";

    public static final String USER_UPDATE_PROFILE_SUCCESS = "USER_UPDATE_PROFILE_SUCCESS";
    public static final String USER_UPDATE_PROFILE_FAIL = "USER_UPDATE_PROFILE_FAIL";
    public static final String USER_UPDATE_PROFILE_ATTEMPT = "USER_UPDATE_PROFILE_ATTEMPT";

    public static final String USER_GET_CONGITO_ATTEMPT = "USER_GET_CONGITO_ATTEMPT";
    public static final String USER_GET_CONGITO_SUCCESS = "USER_GET_CONGITO_SUCCESS";
    public static final String USER_GET_CONGITO_FAIL = "USER_GET_CONGITO_FAIL";

    public static final String USER_GET_PROFILE_ATTEMPT = "USER_GET_PROFILE_ATTEMPT";
    public static final String USER_GET_PROFILE_SUCCESS = "USER_GET_PROFILE_SUCCESS";
    public static final String USER_GET_PROFILE_FAIL = "USER_GET_PROFILE_FAIL";

    public static final String USER_UPLOAD_IMAGE_ATTEMPT = "USER_UPLOAD_IMAGE_ATTEMPT";
    public static final String USER_UPLOAD_IMAGE_SUCCESS = "USER_UPLOAD_IMAGE_SUCCESS";
    public static final String USER_UPLOAD_IMAGE_FAIL = "USER_UPLOAD_IMAGE_FAIL";
    public static final String USER_UPLOAD_IMAGE_FILE_NOT_EXIST = "USER_UPLOAD_IMAGE_FILE_NOT_EXIST";

}
