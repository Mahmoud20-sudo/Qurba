package com.qurba.android.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.facebook.appevents.AppEventsConstants;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.mazenrashed.logdnaandroidclient.models.Line;
import com.orhanobut.hawk.Hawk;
import com.qurba.android.R;
import com.qurba.android.network.QurbaLogger;
import com.qurba.android.network.models.AddAddressModel;
import com.qurba.android.network.models.AreasModel;
import com.qurba.android.network.models.CartItems;
import com.qurba.android.network.models.CartModel;
import com.qurba.android.network.models.CitiesModel;
import com.qurba.android.network.models.CountriesModel;
import com.qurba.android.network.models.DeliveryAddress;
import com.qurba.android.network.models.NearbyPlaceModel;
import com.qurba.android.network.models.OfferIngradients;
import com.qurba.android.network.models.Offers;
import com.qurba.android.network.models.OffersFilteringModel;
import com.qurba.android.network.models.OffersModel;
import com.qurba.android.network.models.OrderProductsRequests;
import com.qurba.android.network.models.Payment;
import com.qurba.android.network.models.PlaceCategoryModel;
import com.qurba.android.network.models.ProductData;
import com.qurba.android.network.models.SectionItems;
import com.qurba.android.network.models.Sections;
import com.qurba.android.network.models.SectionsGroupRequests;
import com.qurba.android.network.models.UserDataModel;
import com.qurba.android.network.models.request_models.CreateOrdersPayload;
import com.qurba.android.network.models.response_models.DeliveryAreaResponse;
import com.qurba.android.network.models.response_models.GuestLoginResponseModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.qurba.android.utils.Constants.OFFER_TYPE_FREE_DELIVERY;
import static com.qurba.android.utils.extenstions.ExtesionsKt.showToastMsg;

public class SharedPreferencesManager {
    private String cartId;

    private static final SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager();

    private SharedPreferencesManager() {
    }

    public static SharedPreferencesManager getInstance() {
        return sharedPreferencesManager;
    }

    public SharedPreferencesManager initialize(Context context) {
        Hawk.init(context).build();
        return sharedPreferencesManager;
    }

    public void setGuestModel(GuestLoginResponseModel.GuestModel guestModel) {
        Hawk.put(Constants.GUEST_MODEL, guestModel);
    }

    public GuestLoginResponseModel.GuestModel getGuestModel() {
        return Hawk.get(Constants.GUEST_MODEL);
    }

    public CartModel getCart() {
        if (!Hawk.contains(Constants.MY_CART)) return null;
        return new Gson().fromJson(Hawk.get(Constants.MY_CART).toString(), CartModel.class);
    }

    public CartModel getCart(String cartId) {
        if (getCart() == null) return null;
        if (cartId == null) return null;
        CartModel cartModel = new Gson().fromJson(Hawk.get(Constants.MY_CART).toString(), CartModel.class);
        return cartId.equalsIgnoreCase(cartModel.getPlaceModel().get_id()) ? cartModel : null;
    }

    public void setCart(CartModel cart, boolean... isFromBackend) {
        if (cart == null) {
            Hawk.delete(Constants.MY_CART);
            return;
        }
        List<CartItems> items = new ArrayList<>();
        if (isFromBackend.length > 0) {
            cartId = cart.getPlaceModel().get_id();
            if (cart.getOffers() != null)
                for (CartModel.ClassZyada34anTar2 model : cart.getOffers()) {
                    CartItems obj = copyOrUpdate(model.getOffer(), isFromBackend);
                    setCartItemData(model, obj);
                    items.add(obj);
                }
            if (cart.getProducts() != null)
                for (CartModel.ClassZyada34anTar2 model : cart.getProducts()) {
                    CartItems obj = copyOrUpdate(model.getProduct(), isFromBackend);
                    setCartItemData(model, obj);
                    items.add(obj);
                }

            cart.setCartItems(items);
            cart.setId(cart.getPlaceModel().get_id());
        }
        Hawk.put(Constants.MY_CART, new Gson().toJson(cart, CartModel.class));
    }

    private CartItems setCartItemData(CartModel.ClassZyada34anTar2 model, CartItems obj) {
        obj.setCartItemId(model.get_id());
        obj.setQuantity(model.getQuantity());
        obj.setPrice(model.getSingleOfferPrice() > 0 ? model.getSingleOfferPrice() : model.getSingleProductPrice());
        obj.setTotalPrice(model.getTotalProductPrice());
        obj.setSections(model.getSections());
        obj.setSectionsGroup(model.getSectionGroups());
        return obj;
    }

    public String getFCMToken() {
        return Hawk.get(Constants.FCM_TOKEN);
    }

    public void setFCMToken(String token) {
        Hawk.put(Constants.FCM_TOKEN, token);
    }

    public DeliveryAreaResponse getDefaultCountry() {
        return Hawk.get(Constants.DEFAULT_COUNTRY);
    }

    public void setDefaultCountry(DeliveryAreaResponse token) {
        Hawk.put(Constants.DEFAULT_COUNTRY, token);
    }

    public List<PlaceCategoryModel> getPlaceCatgories() {
        return Hawk.get(Constants.PLACE_CATEGORIES);
    }

    public void setPlaceCatgories(List<PlaceCategoryModel> categoryModels) {
        Hawk.put(Constants.PLACE_CATEGORIES, categoryModels);
    }

    public void setFirstTimeRunning(boolean token) {
        Hawk.put(Constants.FIRST_TIME, token);
    }

    public boolean isFirstTimeRunning() {
        return Hawk.contains(Constants.FIRST_TIME) ? Hawk.get(Constants.FIRST_TIME) : true;
    }

    public void setAreaVoting(boolean token) {
        Hawk.put(Constants.IS_VOTED, token);
    }

    public boolean isAreaVoted() {
        return Hawk.contains(Constants.IS_VOTED) ? Hawk.get(Constants.IS_VOTED) : true;
    }

    public void setIsAppRunning(boolean token) {
        Hawk.put(Constants.APP_RUNNING, token);
    }

    public boolean isAppRunning() {
        return Hawk.contains(Constants.APP_RUNNING) ? Hawk.get(Constants.APP_RUNNING) : true;
    }

    public String getToken() {
        return Hawk.get(Constants.TOKEN);
    }

    public void setToken(String token) {
        Hawk.put(Constants.TOKEN, token);
    }

    public String getIdentityId() {
        return Hawk.get(Constants.CONGIO_TOKEN);
    }

    public void setIdentityId(String token) {
        Hawk.put(Constants.CONGIO_TOKEN, token);
    }

    public String getCatID() {
        return Hawk.get(Constants.CAT_ID);
    }

    public void setCatID(String catID) {
        Hawk.put(Constants.CAT_ID, catID);
    }

    public Double getLastLat() {
        return Hawk.get(Constants.LAST_KNOWN_LAT);
    }

    public void setLastLat(Double lat) {
        Hawk.put(Constants.LAST_KNOWN_LAT, lat);
    }

    public Double getLastLng() {
        return Hawk.get(Constants.LAST_KNOWN_LNG);
    }

    public void setLastLng(Double lng) {
        Hawk.put(Constants.LAST_KNOWN_LNG, lng);
    }

    public void setOfferPosition(int position) {
        Hawk.put(Constants.OFFER_POSITION, position);
    }

    public int getOfferPosition() {
        return Hawk.get(Constants.OFFER_POSITION);
    }

    public void setProductPosition(int position) {
        Hawk.put(Constants.PRODUCT_POSITION, position);
    }

    public int getProductPosition() {
        return Hawk.get(Constants.PRODUCT_POSITION);
    }

    public void setPlacePosition(int position) {
        Hawk.put(Constants.PLACE_POSITION, position);
    }

    public int getPlacePosition() {
        return Hawk.get(Constants.PLACE_POSITION);
    }

    public String getVersion() {
        return Hawk.get(Constants.VERSION);
    }

    public CitiesModel getSelectedCity() {
        return Hawk.get(Constants.SELECTED_CITY);
    }

    public AreasModel getSelectedArea() {
        return Hawk.get(Constants.SELECTED_AREA);
    }

    public AddAddressModel getSelectedCachedArea() {
        return Hawk.get(Constants.SELECTED_DELIVERY_ADDRESS);
    }

    public OffersFilteringModel getFilterModel() {
        return Hawk.get(Constants.FILTER_MODEL);
    }

    public void setPlaceFilterModel(OffersFilteringModel filterModel) {
        if (filterModel == null && filterModel.getCanDeliver() == null && filterModel.getActiveNow() == null &&
                (filterModel.getOfferType() == null || filterModel.getOfferType().isEmpty()) && filterModel.getPrice() == 0)
            Hawk.put(Constants.FILTER_MODEL, null);
        else
            Hawk.put(Constants.PLACE_FILTER_MODEL, filterModel);
    }

    public OffersFilteringModel getPlaceFilterModel() {
        return Hawk.get(Constants.PLACE_FILTER_MODEL);
    }

    public void setFilterModel(OffersFilteringModel filterModel) {
        if (filterModel == null && filterModel.getCanDeliver() == null && filterModel.getActiveNow() == null &&
                (filterModel.getOfferType() == null || filterModel.getOfferType().isEmpty()) && filterModel.getPrice() == 0)
            Hawk.put(Constants.FILTER_MODEL, null);
        else
            Hawk.put(Constants.FILTER_MODEL, filterModel);
    }

    public void clearFilterModel() {
        Hawk.put(Constants.FILTER_MODEL, null);
    }

    public void clearPlaceFilterModel() {
        Hawk.put(Constants.PLACE_FILTER_MODEL, null);
    }

    public void setSelectedCachedArea(AddAddressModel deliveryResponse) {
        Hawk.put(Constants.SELECTED_DELIVERY_ADDRESS, deliveryResponse);
    }

    public void setSavedDeliveryAddress(List<AddAddressModel> deliveryResponse) {
        Hawk.put(Constants.SAVED_DELIVERY_ADDRESSES, deliveryResponse);
    }

    public ArrayList<AddAddressModel> getSavedDeliveryAddress() {
        return Hawk.get(Constants.SAVED_DELIVERY_ADDRESSES) == null ? new ArrayList<>() : Hawk.get(Constants.SAVED_DELIVERY_ADDRESSES);
    }

    public OffersModel getOrderedOFfer() {
        return Hawk.get(Constants.ORDERED_OFFER);
    }

    public void setSelectedCountry(CountriesModel countriesModel) {
        Hawk.put(Constants.SELECTED_COUNTRY, countriesModel);
    }

    public void setDeliverCountry(CountriesModel countriesModel) {
        Hawk.put(Constants.DELIVERY_COUNTRY, countriesModel);
    }

    public DeliveryAddress getLastAddress() {
        return Hawk.get(Constants.USER_LAST_ADDRESS);
    }

    public void setLastAddress(DeliveryAddress lastAddress) {
        Hawk.put(Constants.USER_LAST_ADDRESS, lastAddress);
    }

    public CountriesModel getSelectedCountry() {
        return Hawk.get(Constants.SELECTED_COUNTRY);
    }

    public CountriesModel getDeliveryCountry() {
        return Hawk.get(Constants.DELIVERY_COUNTRY);
    }

    public void setVersion(String version) {
        Hawk.put(Constants.VERSION, version);
    }

    public void setCity(CitiesModel city) {
        Hawk.put(Constants.SELECTED_CITY, city);
    }

    public void setDeliveryCity(CitiesModel city) {
        Hawk.put(Constants.DELIVERY_CITY, city);
    }

    public CitiesModel getDeliveryCity() {
        return Hawk.get(Constants.DELIVERY_CITY);
    }

    public AreasModel getDliveryArea() {
        return Hawk.get(Constants.DELIVERY_AREA);
    }

    public void setArea(AreasModel area) {
        Hawk.put(Constants.SELECTED_AREA, area);
    }

    public void setDeliveryArea(AreasModel area) {
        Hawk.put(Constants.DELIVERY_AREA, area);
    }

    public void setOrderedOFfer(OffersModel oFfer) {
        Hawk.put(Constants.ORDERED_OFFER, oFfer);
    }

    public String getLanguage() {
        if (Hawk.get(Constants.LANGUAGE) == null) {
            String locale = "en";
            if (Locale.getDefault().getLanguage().equalsIgnoreCase("ar"))
                locale = "ar";

            setLanguage(locale);
        }

        return Hawk.get(Constants.LANGUAGE);
    }

    public void setLanguage(String language) {
        Hawk.put(Constants.LANGUAGE, language);
    }

    public List<PlaceCategoryModel> getCategories() {
        return Hawk.get(Constants.CATEGORIES);
    }

    public void setCategories(List<PlaceCategoryModel> categories) {
        Hawk.put(Constants.CATEGORIES, categories);
    }

    public List<NearbyPlaceModel> getPlaces() {
        return Hawk.get(Constants.PLACES);
    }

    public void setPlaces(List<NearbyPlaceModel> places) {
        Hawk.put(Constants.PLACES, places);
    }

    public UserDataModel getUser() {
        return Hawk.get(Constants.USER_DATA);
    }

    public void setUser(UserDataModel user) {
        Hawk.put(Constants.USER_DATA, user);
    }

    public ArrayList<String> getImages() {
        return Hawk.get(Constants.Images);
    }

    public void setImages(List<String> images) {
        Hawk.put(Constants.Images, images);
    }

    public HashMap<Integer, Boolean> getOfferClaimed() {
        return Hawk.get(Constants.CLAIMED_OFFER);
    }

    public void setOfferClaimed(HashMap<Integer, Boolean> offerData) {
        Hawk.put(Constants.CLAIMED_OFFER, offerData);
    }

    public HashMap<Integer, Boolean> getPlaceFollowed() {
        return Hawk.get(Constants.FOLLOWED_PLACE);
    }

    public void setPlaceFollowed(HashMap<Integer, Boolean> placeData) {
        Hawk.put(Constants.FOLLOWED_PLACE, placeData);
    }

    public List<String> getRecentSearchQueriesPlaces() {
        return Hawk.get(Constants.QUERIES);
    }

    public void setRecentSearchQueriesPlaces(List<String> queries) {
        Hawk.put(Constants.QUERIES, queries);
    }

    public boolean isGuest() {
        return Hawk.get(Constants.GUEST);
    }

    public void setGuest(boolean guest) {
        Hawk.put(Constants.GUEST, guest);
    }

    public boolean isORdering() {
        return Hawk.get(Constants.IS_ORDERING);
    }

    public void setOrdering(boolean ordring) {
        Hawk.put(Constants.IS_ORDERING, ordring);
    }


    public void clearCart() {
//        openTransaction();
//        RealmResults<CartModel> rows = realm.where(CartModel.class).findAll();
//        rows.deleteAllFromRealm();
//        realm.commitTransaction();
        cartId = null;
        setCart(null);
    }

//    private List<CartItems> updateItem(List<CartItems> cartItemsRealmList, CartItems cartItems) {
//        boolean isFound = false;
//        for (int i = 0; i < cartItemsRealmList.size(); i++) {
//            if (cartItemsRealmList.get(i).getHashKey().equals(cartItems.getHashKey())) {
//                cartItemsRealmList.get(i).setQuantity(cartItemsRealmList.get(i).getQuantity() + 1 > 10 ? 10
//                        : cartItemsRealmList.get(i).getQuantity() + 1);
//                isFound = true;
//            }
//        }
//
//        if (!isFound) {
//            cartItems.setQuantity(1);
//            cartItemsRealmList.add(cartItems);
//        }
//
//        return cartItemsRealmList;
//    }

    private List<CartItems> updateItemWithQty(List<CartItems> cartItemsRealmList, CartItems cartItems) {
        boolean isFound = false;
        for (int i = 0; i < cartItemsRealmList.size(); i++) {
            if (cartItemsRealmList.get(i).getHashKey().equals(cartItems.getHashKey())) {
                cartItemsRealmList.get(i).setQuantity(cartItems.getQuantity() == 0 ?
                        cartItemsRealmList.get(i).getQuantity() + 1 : cartItems.getQuantity());
                isFound = true;
            }
        }

        if (!isFound) {
            cartItems.setQuantity(1);
            cartItemsRealmList.add(cartItems);
        }

        return cartItemsRealmList;
    }

    public void copyOrUpdate(int pos, int newQty) {
        CartModel cartModel = getCart();
        if (cartModel != null && pos < cartModel.getCartItems().size()) {
            cartModel.getCartItems().get(pos).setQuantity(newQty);
            setCart(cartModel);
        } else
            QurbaLogger.Companion.logging(
                    QurbaApplication.getContext(),
                    Constants.USER_UPDATE_CART_ITEM_QTY_FAIL, Line.LEVEL_ERROR,
                    "User failed to update his cart itemQty ", "Cart is null where it shouldn't be"
            );
    }

    public void copyOrUpdate(List<CartItems> offersModels) {
        CartModel cartModel = getCart();
        if (cartModel != null) {
            cartModel.setCartItems(offersModels);
            setCart(cartModel);
        } else
            QurbaLogger.Companion.logging(
                    QurbaApplication.getContext(),
                    Constants.USER_UPDATE_CART_ITEM_QTY_FAIL, Line.LEVEL_ERROR,
                    "User failed to update his cart itemQty ", "Cart is null where it shouldn't be"
            );
    }

    public void removeItemFromCart(String id) {
        try {
            CartModel cartModel = getCart();
            List<CartItems> items = getCart().getCartItems();
            for (CartItems item : items) {
                if (item.get_id().equalsIgnoreCase(id))
                    items.remove(item);
            }
            cartModel.setCartItems(items);
            if (items.isEmpty())
                clearCart();
            else
                setCart(cartModel);
        } catch (Exception exception) {
            QurbaLogger.Companion.logging(
                    QurbaApplication.getContext(),
                    Constants.USER_ADD_OFFER_TO_CART_FAIL, Line.LEVEL_ERROR,
                    "User failed to add to his cart ", exception.getStackTrace().toString());
        }
    }

    public boolean checkIfLimitQtyReached(BaseActivity activity, String id) {
        //max qty per item is 10
        if (getCart() == null || getCart().getCartItems() == null) return true;

        int qty = 0;
        for (CartItems item : getCart().getCartItems()) {
            if (id.equalsIgnoreCase(item.get_id())) {
                qty = item.getQuantity() + 1;
                break;
            }
        }

        if (qty > 10) {
            showToastMsg(activity, activity.getString(R.string.max_quantity_hint));
            return false;
        }
        return true;
    }

    public CartItems copyOrUpdate(OffersModel offersModel, boolean... isFromBackend) {
//        openTransaction();
        final CartModel obj = new CartModel();
        obj.setId(offersModel.getPlaceId().get_id());
        obj.setPlaceModel(offersModel.getPlaceId());
        obj.setBranchName(offersModel.getPlaceId().getBranchName());

        obj.setHashKey(offersModel.getHashKey());
        obj.setUnique_name(offersModel.getPlaceId().getUniqueName());
        obj.setPlaceModel(offersModel.getPlaceId());

        CartItems cartItemObj = new CartItems();
        cartItemObj.set_id(offersModel.get_id());
        cartItemObj.setHashKey(offersModel.getHashKey() != null ? offersModel.getHashKey() : offersModel.get_id());
        cartItemObj.setPlaceId(offersModel.getPlaceId());
        cartItemObj.setName(offersModel.getUniqueName());
        cartItemObj.setItemType(Constants.OFFERS);
        cartItemObj.setOfferType(offersModel.getType());
        cartItemObj.setPrice(offersModel.getPrice());
        cartItemObj.setBeforePrice(offersModel.getBeforePrice());
        cartItemObj.setTitle(offersModel.getTitle());
        cartItemObj.setOfferType(offersModel.getType());
        cartItemObj.setSections(offersModel.getSections());
        cartItemObj.setImageUrl(offersModel.getPictureUrl());
        cartItemObj.setAvailable(true);

        CartModel cartObject = getCart(obj.getId() == null ? cartId : obj.getId());
        if (cartObject != null) {
            obj.setSpecialNote(cartObject.getSpecialNote());
            obj.setTimeStamp(cartObject.getTimeStamp());
            if (validateCartItemsSize(cartObject.getCartItems()))
                obj.setCartItems(updateItemWithQty(cartObject.getCartItems(), cartItemObj));
            else
                obj.setCartItems(cartObject.getCartItems());
        } else {
            obj.setTimeStamp(Calendar.getInstance().getTimeInMillis());
            List<CartItems> cartItems = new ArrayList<>();
            cartItemObj.setQuantity(1);
            cartItems.add(cartItemObj);
            obj.setCartItems(cartItems);
        }
//        realm.copyToRealmOrUpdate(obj);
//        realm.commitTransaction();
        if (isFromBackend.length == 0)
            setCart(obj);
//        refresh();

        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, offersModel.get_id());
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, Constants.OFFERS);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "EGP");

        QurbaLogger.Companion.standardFacebookEventsLogging(QurbaApplication.getContext(),
                AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
                offersModel.getPrice(), params);

        Bundle bundle = new Bundle();
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, offersModel.getPrice());
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "EGP");
        QurbaLogger.Companion.standardFirebaseAnalyticsEventsLogging(QurbaApplication.getContext(), FirebaseAnalytics.Event.ADD_TO_CART, bundle);

        return cartItemObj;
    }

    public CartItems copyOrUpdate(ProductData products, boolean... isFromBackend) {
//        openTransaction();

        final CartModel obj = new CartModel();
        obj.setId(products.getPlaceInfo().get_id());
        obj.setPlaceModel(products.getPlaceInfo());
        obj.setBranchName(products.getPlaceInfo().getBranchName());
        obj.setHashKey(products.get_id());
        obj.setUnique_name(products.getPlaceInfo().getUniqueName());
        obj.setPlaceModel(products.getPlaceInfo());

        CartItems cartItemObj = new CartItems();
        cartItemObj.set_id(products.get_id());
        cartItemObj.setHashKey(products.getHashKey() != null ? products.getHashKey() : products.get_id());
        cartItemObj.setPlaceId(products.getPlaceInfo());
        cartItemObj.setName(products.getUniqueName());
        cartItemObj.setSections(products.getSections());
        cartItemObj.setTitle(products.getName());
        cartItemObj.setItemType(Constants.PRODUCTS);
        cartItemObj.setQuantity(products.getQuantity());
        cartItemObj.setPrice(products.getPrice());
        cartItemObj.setBeforePrice(products.getBeforePrice());
        cartItemObj.setImageUrl(products.getImageURL());
        cartItemObj.setDeliverFees(products.getDeliveryFees());
        cartItemObj.setAvailable(true);

        CartModel cartObject = getCart(cartId == null ? obj.getId() : cartId);
        if (cartObject != null) {
            obj.setSpecialNote(cartObject.getSpecialNote());
            obj.setTimeStamp(cartObject.getTimeStamp());
            if (validateCartItemsSize(cartObject.getCartItems()))
                obj.setCartItems(updateItemWithQty(cartObject.getCartItems(), cartItemObj));
            else
                obj.setCartItems(cartObject.getCartItems());
        } else {
            List<CartItems> cartItems = new ArrayList<>();
            cartItemObj.setQuantity(1);
            obj.setTimeStamp(Calendar.getInstance().getTimeInMillis());
            cartItems.add(cartItemObj);
            obj.setCartItems(cartItems);
        }
//        realm.copyToRealmOrUpdate(obj);
//        realm.commitTransaction();
        if (isFromBackend.length == 0)
            setCart(obj);
        //        refresh();

        Bundle params = new Bundle();
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_ID, products.get_id());
        params.putString(AppEventsConstants.EVENT_PARAM_CONTENT_TYPE, Constants.PRODUCTS);
        params.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, "EGP");

        QurbaLogger.Companion.standardFacebookEventsLogging(QurbaApplication.getContext(),
                AppEventsConstants.EVENT_NAME_ADDED_TO_CART,
                products.getPrice(), params);

        Bundle bundle = new Bundle();
        bundle.putDouble(FirebaseAnalytics.Param.VALUE, products.getPrice());
        bundle.putString(FirebaseAnalytics.Param.CURRENCY, "EGP");
        QurbaLogger.Companion.standardFirebaseAnalyticsEventsLogging(QurbaApplication.getContext(), FirebaseAnalytics.Event.ADD_TO_CART, bundle);

        return cartItemObj;
    }

    private boolean validateCartItemsSize(List<CartItems> offersModels) {
        if (offersModels.size() == 10) {
            Toast.makeText(QurbaApplication.getContext(), QurbaApplication.getContext().getString(R.string.max_quantity_hint), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean checkIfFreeDeliveryCanceled() {
        boolean isHaveFreeDeliveryOnly = false;

        if (getCart() == null)
            return false;

        //for offers only
        for (CartItems cartItems : getCart().getCartItems()) {
            if (cartItems.getItemType().equalsIgnoreCase(Constants.OFFERS)
                    && cartItems.getOfferType().equalsIgnoreCase(OFFER_TYPE_FREE_DELIVERY)) {
                isHaveFreeDeliveryOnly = true;
                break;
            }
        }

        return isHaveFreeDeliveryOnly;
    }

//    public CreateOrdersPayload prepareOrderRequest(JsonArray jsonArray) {
//        List<CartItems> cartItemsList = getCart().getCartItems();
//        CreateOrdersPayload payload = new CreateOrdersPayload();
//
//        ArrayList<OrderProductsRequests> productArrayList = new ArrayList<>();
//        ArrayList<Offers> offersArrayList = new ArrayList<>();
//
//        for (int i = 0; i < cartItemsList.size(); ++i) {
//            ArrayList sectionsList = new ArrayList();
//            ArrayList groupList = new ArrayList();
//
//            payload.setTotalPrice(payload.getTotalPrice() +
//                    cartItemsList.get(i).getPrice() * cartItemsList.get(i).getQuantity());
//
//            if (cartItemsList.get(i).getSections() != null)
//                for (Sections sections : cartItemsList.get(i).getSections()) {
//                    OfferIngradients offerIngradients = new OfferIngradients();
//                    offerIngradients.setSection(sections.get_id());
//
//                    JSONObject json = new JSONObject();
//                    try {
//                        json.put("id", cartItemsList.get(i).get_id());
//                        json.put("quantity", cartItemsList.get(i).getQuantity());
//                        jsonArray.add(String.valueOf(json));
//                    } catch (Exception e) {
//                        // TODO Auto-generated catch block
//                        e.printStackTrace();
//                    }
//
//                    ArrayList itemsList = new ArrayList();
//                    for (SectionItems sectionItems : sections.getItems()) {
//                        String itemIds = sectionItems.get_id();
//                        itemsList.add(itemIds);
//                    }
//                    offerIngradients.setItemsIds(itemsList);
//
//                    if (sections.getSectionsGroup() != null) {
//                        SectionsGroupRequests sectionsGroup = new SectionsGroupRequests(sections.getSectionsGroup().get_id(), offerIngradients);
//                        groupList.add(sectionsGroup);
//                    } else
//                        sectionsList.add(offerIngradients);
//                }
//
//            if (cartItemsList.get(i).getItemType().equalsIgnoreCase(Constants.OFFERS)) {
//                Offers offers = new Offers(cartItemsList.get(i).get_id(), cartItemsList.get(i).getQuantity()
//                        , (List<OfferIngradients>) groupList, (List<OfferIngradients>) sectionsList);
//                offersArrayList.add(offers);
//            } else {
//                OrderProductsRequests orderProductsRequests = new OrderProductsRequests();
//                orderProductsRequests.setProduct(cartItemsList.get(i).get_id());
//                orderProductsRequests.setQuantity(cartItemsList.get(i).getQuantity());
////                orderProductsRequests.setProductName(this.cartItemsList.get(i).getTitle());
//                orderProductsRequests.setSections((List<OfferIngradients>) sectionsList);
//                orderProductsRequests.setSectionGroups((List<SectionsGroupRequests>) groupList);
//                productArrayList.add(orderProductsRequests);
//            }
//        }
//
//        if (!offersArrayList.isEmpty())
//            payload.setOffers(offersArrayList);
//
//        if (!productArrayList.isEmpty())
//            payload.setProducts(productArrayList);
//
//        payload.setPlace(getCart().getId());
//        payload.setCartSize(cartItemsList.size());
//
//        Payment payment = new Payment();
//        payment.setPaid(true);
//        payment.setMethod("CASH");
//
//        DeliveryAddress deliveryAddress = new DeliveryAddress(
//                getSelectedCachedArea().getCountry().get_id(),
//                getSelectedCachedArea().getCity().get_id(),
//                getSelectedCachedArea().getArea().get_id(),
//                getSelectedCachedArea().getStreet(),
//                getSelectedCachedArea().getBuilding(),
//                getSelectedCachedArea().getFloor(),
//                getSelectedCachedArea().getFlat(),
//                getSelectedCachedArea().getNearestLandmark(),
//                getSelectedCachedArea().getBranchedStreet());
//        payload.setPayment(payment);
//        payload.setDeliveryAddress(deliveryAddress);
//
//        return payload;
//    }
}