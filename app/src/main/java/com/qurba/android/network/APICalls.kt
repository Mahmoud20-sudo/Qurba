/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  com.qurba.android.network.models.LikeProductPayload
 *  com.qurba.android.utils.SharedPreferencesManager
 *  java.lang.Object
 *  java.lang.String
 *  java.lang.StringBuilder
 *  java.util.ArrayList
 *  java.util.List
 *  org.json.JSONObject
 *  retrofit2.Response
 *  rx.Observable
 */
package com.qurba.android.network

import com.qurba.android.network.models.*
import com.qurba.android.network.models.request_models.*
import com.qurba.android.network.models.request_models.auth.*
import com.qurba.android.network.models.response_models.*
import com.qurba.android.utils.SharedPreferencesManager
import org.json.JSONObject
import retrofit2.Response
import rx.Observable

class APICalls {
    private val endPoints = RetrofitClient.service

    fun addOfferComment(
        string: String,
        commetnsRequest: CommetnsRequest
    ): Observable<Response<AddCommentPayload>> {
        return endPoints.addOfferComments(string, commetnsRequest)
    }

    fun updateOfferComment(
        offerId: String,
        commentId: String,
        commetnsRequest: CommetnsRequest
    ): Observable<Response<UpdateCommentPayload>> {
        return endPoints.updateOfferComments(offerId, commentId, commetnsRequest)
    }

    fun deleteOfferComment(
        offerId: String,
        commentId: String
    ): Observable<Response<UpdateCommentPayload>> {
        return endPoints.deleteOfferComment(offerId, commentId)
    }

    fun addProductComments(
        string: String?,
        commetnsRequest: CommetnsRequest
    ): Observable<Response<AddCommentPayload>> {
        return endPoints.addProductComments(string, commetnsRequest)
    }

    fun updateProductComments(
        productId: String?,
        commentId: String?,
        commetnsRequest: CommetnsRequest
    ): Observable<Response<UpdateCommentPayload>> {
        return endPoints.updateProductComments(productId, commentId, commetnsRequest)
    }

    fun deleteProductComment(
        productId: String?,
        commentId: String?
    ): Observable<Response<UpdateCommentPayload>> {
        return endPoints.deleteProductComment(productId, commentId)
    }

    fun addPlaceComment(
        string: String,
        commetnsPayload: CommetnsRequest
    ): Observable<Response<AddCommentPayload>> {
        return endPoints.addPlaceComment(string, commetnsPayload)
    }

    fun updatePlaceComments(
        productId: String,
        commentId: String,
        commetnsRequest: CommetnsRequest
    ): Observable<Response<UpdateCommentPayload>> {
        return endPoints.updatePlaceComments(productId, commentId, commetnsRequest)
    }

    fun deletePlaceComment(
        placeId: String,
        commentId: String
    ): Observable<Response<UpdateCommentPayload>> {
        return endPoints.deletePlaceComment(placeId, commentId)
    }

    fun replyPlaceComment(
        placeId: String,
        commentId: String,
        commetnsRequest: CommetnsRequest
    ): Observable<Response<AddCommentPayload>> {
        return endPoints.replyPlaceComment(placeId, commentId, commetnsRequest)
    }

    fun getPlaceCommentReplies(
        offerId: String,
        commentId: String,
        map: Map<String, Any>
    ): Observable<Response<RepliesPayload>> {
        return endPoints.getPlaceCommentReplies(offerId, commentId, map)
    }

    fun likeOffer(string: String): Observable<Response<StringOnlyResponse>> {
        return endPoints.likeOffer(string)
    }

    fun shareOffer(string: String): Observable<Response<StringOnlyResponse>> {
        return endPoints.shareOffer(string)
    }

    fun shareProduct(string: String?): Observable<Response<StringOnlyResponse>> {
        return endPoints.shareProduct(string)
    }

    fun sharePlace(string: String): Observable<Response<StringOnlyResponse>> {
        return endPoints.sharePlace(string)
    }

    fun addMobileToUser(signUpPhoneRequestModel: SignUpPhoneRequestModel): Observable<Response<BaseModel>> {
        return endPoints.addMobileToUser(signUpPhoneRequestModel)
    }

    fun changePassword(changePasswordRequest: ChangePasswordRequest): Observable<Response<LoginResponseModel>> {
        return endPoints.changePassword(changePasswordRequest)
    }

    fun createOrder(createOrdersRequestModel: CreateOrdersRequestModel): Observable<Response<OrderResponseModel>> {
        return endPoints.createOrder(createOrdersRequestModel)
    }

    fun dislikeProduct(string: String?): Observable<Response<LikeProductPayload>> {
        return endPoints.disLikeProduct(string)
    }

    fun followPlace(followUnFollowRequestModel: FollowUnFollowRequestModel): Observable<Response<StringOnlyResponse>> {
        return endPoints.followPlace(followUnFollowRequestModel)
    }

    fun forgotPasswordEmail(signUpEmailRequestModel: SignUpEmailRequestModel): Observable<Response<LoginResponseModel>> {
        return endPoints.forgotPasswordEmail(signUpEmailRequestModel)
    }

    fun forgotPasswordMobile(signUpPhoneRequestModel: SignUpPhoneRequestModel): Observable<Response<LoginResponseModel>> {
        return endPoints.forgotPasswordMobile(signUpPhoneRequestModel)
    }

    fun forgotPasswordMobileReset(forgotPhoneVerifyRequestModel: ForgotPhoneVerifyRequestModel): Observable<Response<LoginResponseModel>> {
        return endPoints.forgotPasswordMobileReset(forgotPhoneVerifyRequestModel)
    }

    fun forgotPasswordMobileVerify(forgotPhoneVerifyRequestModel: ForgotPhoneVerifyRequestModel): Observable<Response<LoginResponseModel>> {
        return endPoints.forgotPasswordMobileVerify(forgotPhoneVerifyRequestModel)
    }

    fun forgotPasswordReset(forgotEmailVerifyRequestModel: ForgotEmailVerifyRequestModel): Observable<Response<LoginResponseModel>> {
        return endPoints.forgotPasswordEmailReset(forgotEmailVerifyRequestModel)
    }

    fun forgotPasswordVerify(forgotEmailVerifyRequestModel: ForgotEmailVerifyRequestModel): Observable<Response<LoginResponseModel>> {
        return endPoints.forgotPasswordEmailVerify(forgotEmailVerifyRequestModel)
    }

    fun gePlaceOffers(
        string: String,
        map: Map<String, Any>
    ): Observable<Response<OffersResponseModel>> {
        return endPoints.getPlaceOffers(
            string,
            map,
            SharedPreferencesManager.getInstance().selectedCachedArea?.area?._id,
        )
    }

    fun gePlaceProducts(string: String, n: Int): Observable<Response<ProductsResponseModel>> {
        return endPoints.getPlaceProducts(
            string,
            n,
            SharedPreferencesManager.getInstance().selectedCachedArea?.area?._id
        )
    }

    val appSettings: Observable<Response<AppSettingsPayload>>
        get() = endPoints.appSettings

    //    public Observable<Response<CategoriesResponseModel>> getCategories() {
    //        return this.endPoints.getCategories();
    //    }
    fun getOfferComments(
        string: String,
        map: Map<String, Any>
    ): Observable<Response<CommentsPayload>> {
        return endPoints.getOfferComments(string, map)
    }

    fun getProductComments(
        string: String,
        map: Map<String, Any>
    ): Observable<Response<CommentsPayload>> {
        return endPoints.getProductComments(string, map)
    }

    fun getPlaceComments(
        string: String,
        map: Map<String, Any>
    ): Observable<Response<CommentsPayload>> {
        return endPoints.getPlaceComments(string, map)
    }

    val likedOffers: Observable<Response<SearchOffersResponseModel>>
        get() = endPoints.likedOffers
    val followedPlaces: Observable<Response<NearbyPlacesResponseModel>>
        get() = endPoints.followedPlaces

    fun getOrderDetails(id: String): Observable<Response<OrderResponseModel>> {
        return endPoints.getOrderDetails(id)
    }

    fun getOfferDetails(string: String): Observable<Response<OfferDetailsResponseModel>> {
        return endPoints.getOfferDetails(
            string,
            SharedPreferencesManager.getInstance().selectedCachedArea?.area?._id
        )
    }

    fun getOffers(
        page: Int,
        map: Map<String, Any?>?
    ): Observable<Response<OffersResponseModel>> {
        return endPoints.getOffers(page, map)
    }

    suspend fun getPlaces(
        page: Int,
        map: Map<String, Any>
    ): Response<NearbyPlacesResponseModel> {
        return endPoints.getPlaces(page, map)
    }

    fun searchPlaces(
        page: Int,
        search: String,
        map: Map<String, Any>
    ): Observable<Response<NearbyPlacesResponseModel>> {
        return endPoints.searchPlaces(page, search, map)
    }

    val placeCategories: Observable<Response<CategoriesResponseModel>>
        get() = endPoints.placeCategories

    fun getOffers(
        page: Int,
        area: String,
        city: String,
        country: String
    ): Observable<Response<OffersResponseModel>> {
        return endPoints.getOffers(page, area, city, country)
    }

    fun getOrderNow(page: Int, map: Map<String, Any?>?): Observable<Response<OrderNowModel>> {
        return endPoints.getOrderNow(map, page, 20)
    }

    fun getOrders(n: Int): Observable<Response<OrdersPayload>> {
        return endPoints.getOrders(n)
    }

    fun getActiveOrders(n: Int): Observable<Response<OrdersPayload>> {
        return endPoints.getActiveOrders(n, "ACTIVE")
    }

    fun getPlaceDetails(string: String): Observable<Response<PlaceDetailsResponseModel>> {
        return endPoints.getPlaceDetails(string)
    }

    fun getPlaceDetailsHeader(string: String): Observable<Response<PlaceDetailsHeaderResponse>> {
        return endPoints.getPlaceDetailsHeader(
            string,
            SharedPreferencesManager.getInstance().selectedCachedArea?.area?._id
        )
    }

    fun likeProduct(string: String?): Observable<Response<LikeProductPayload>> {
        return endPoints.likeProduct(string)
    }

    suspend fun loginGuest(model: GuestAuthModel): Response<GuestLoginResponseModel> {
        return endPoints.loginGuest(model)
    }

    fun changeLanguage(languageModel: LanguageModel): Observable<Response<GuestLoginResponseModel>> {
        return endPoints.changeLanguage(languageModel)
    }

    fun loginUserFacebook(loginFacebookRequestModel: LoginFacebookPayload?): Observable<Response<LoginResponseModel>> {
        return endPoints.loginUserByFacebook(loginFacebookRequestModel)
    }

    fun loginUserGoogle(loginFacebookPayload: LoginFacebookPayload?): Observable<Response<LoginResponseModel>> {
        return endPoints.loginUserByGoogle(loginFacebookPayload)
    }

    fun registerPushToken(pushNotificationAuthModel: PushNotificationAuthModel): Observable<Response<Void>> {
        return endPoints.registerPushToken(pushNotificationAuthModel)
    }

    fun registerUserPushToken(pushNotificationAuthModel: PushNotificationAuthModel): Observable<Response<Void>> {
        return endPoints.registerUserPushToken(pushNotificationAuthModel)
    }

    fun unlikeOffer(string: String): Observable<Response<StringOnlyResponse>> {
        return endPoints.unlikeOffer(string)
    }

    fun searchOffers(
        string: String,
        bl: Boolean,
        n: Int
    ): Observable<Response<SearchOffersResponseModel>> {
        return endPoints.searchOffers(string, "createdAt", "-1", n, bl)
    }

    fun signUpUserEmailResend(signUpEmailRequestModel: SignUpEmailRequestModel): Observable<Response<LoginResponseModel>> {
        return endPoints.registerUserByEmailResend(signUpEmailRequestModel)
    }

    fun signUpUserEmailVerify(signUpEmailVerifyRequestModel: SignUpEmailVerifyRequestModel): Observable<Response<LoginResponseModel>> {
        return endPoints.registerUserByEmailVerify(signUpEmailVerifyRequestModel)
    }

    fun signUpUserPhoneResend(signUpPhoneRequestModel: SignUpPhoneRequestModel): Observable<Response<LoginResponseModel>> {
        return endPoints.registerUserByPhoneResend(signUpPhoneRequestModel)
    }

    fun signUpUserPhoneVerify(signUpPhoneVerifyRequestModel: SignUpPhoneVerifyRequestModel): Observable<Response<LoginResponseModel>> {
        return endPoints.registerUserByPhoneVerify(signUpPhoneVerifyRequestModel)
    }

    fun unFollowPlace(followUnFollowRequestModel: FollowUnFollowRequestModel): Observable<Response<StringOnlyResponse>> {
        return endPoints.unfollowPlace(followUnFollowRequestModel)
    }

    fun updateEmail(changeEmailRequest: ChangeEmailRequest?): Observable<Response<LoginResponseModel>> {
        return endPoints.updateEmail(changeEmailRequest)
    }

    fun updateEmailVerification(verifyEmailRequest: VerifyEmailRequest?): Observable<Response<LoginResponseModel>> {
        return endPoints.updateEmailVerification(verifyEmailRequest)
    }

    fun verifyUserMobile(verifyPhoneRequestModel: VerifyPhoneRequestModel): Observable<Response<LoginResponseModel>> {
        return endPoints.verifyCode(verifyPhoneRequestModel)
    }

    fun getProductDetails(string: String?): Observable<Response<ProductDetailsResponseModel>> {
        return endPoints.getProductDetails(
            string,
            SharedPreferencesManager.getInstance().selectedCachedArea?.area?._id
        )
    }

    fun likePlace(placeId: String?): Observable<Response<StringOnlyResponse>> {
        return endPoints.likePlace(placeId)
    }

    fun disLikePlace(_id: String?): Observable<Response<StringOnlyResponse>> {
        return endPoints.disLikePlace(_id)
    }

    suspend fun getNearestLocations(
        lat: String,
        lng: String
    ): Response<NearestAreaResponseModel> {
        return endPoints.getNearestLocations(lat, lng)
    }

    fun getCities(
        country_id: String,
        page: Int
    ): Observable<Response<DeliveryAreaResponseModel>> {
        return endPoints.getCities(country_id, page)
    }

    fun getCountry(): Observable<Response<DeliveryAreaResponseModel>> {
        return endPoints.country
    }

    fun getAreasByCity(
        country_id: String,
        city_id: String,
        page: Int,
        search: String
    ): Observable<Response<DeliveryAreaResponseModel>> {
        return if (search.length == 0) endPoints.getAreasByCity(
            country_id,
            city_id,
            page
        ) else endPoints.searchAreasByCity(country_id, city_id, search, page)
    }

    fun addAddress(addAddressRequestModel: AddAddressRequestModel): Observable<Response<AddAddressResponseModel>> {
        return endPoints.addAddress(addAddressRequestModel)
    }

    fun updateddress(
        id: String,
        addAddressRequestModel: AddAddressRequestModel
    ): Observable<Response<AddAddressResponseModel>> {
        return endPoints.updateAddress(id, addAddressRequestModel)
    }

    fun deleteAddress(id: String): Observable<Response<BaseModel>> {
        return endPoints.deleteAddress(id)
    }

    fun checkDelivering(
        placeId: String?,
        areaId: String?
    ): Observable<Response<DeliveryValidationPayload>> {
        return endPoints.checkDelivering(placeId, areaId)
    }

    fun voteArea(voteRequestModel: VoteRequestModel): Observable<Response<JSONObject>> {
        return endPoints.voteArea(voteRequestModel)
    }

    val featuredPlaces: Observable<Response<NearbyPlacesPayload>>
        get() = endPoints.getFeaturedPlaces(SharedPreferencesManager.getInstance().selectedCachedArea?.area?._id)

    fun getSimilarPlaces(_id: String): Observable<Response<SimilarPlacesPayload>> {
        val map = HashMap<String, String>()
        map["exclude[]"] = _id
        return endPoints.getSimilarPlaces(_id, map)
    }

    fun likeOfferComment(
        offerId: String,
        commentId: String?
    ): Observable<Response<StringOnlyResponse>> {
        return endPoints.likeOfferComment(offerId, commentId)
    }

    fun unLikeOfferComment(
        offerId: String,
        commentId: String?
    ): Observable<Response<StringOnlyResponse>> {
        return endPoints.unLikeOfferComment(offerId, commentId)
    }

    fun replyOfferComment(
        offerId: String,
        commentId: String,
        commetnsRequest: CommetnsRequest
    ): Observable<Response<AddCommentPayload>> {
        return endPoints.replyOfferComment(offerId, commentId, commetnsRequest)
    }

    fun getOfferCommentReplies(
        offerId: String,
        commentId: String,
        map: Map<String, Any>
    ): Observable<Response<RepliesPayload>> {
        return endPoints.getOfferCommentReplies(offerId, commentId, map)
    }

    fun likeProductComment(
        productId: String,
        commentId: String?
    ): Observable<Response<StringOnlyResponse>> {
        return endPoints.likeProductComment(productId, commentId)
    }

    fun unLikeProductComment(
        productId: String,
        commentId: String?
    ): Observable<Response<StringOnlyResponse>> {
        return endPoints.unLikeProductComment(productId, commentId)
    }

    fun replyProductComment(
        productId: String,
        commentId: String?,
        commetnsRequest: CommetnsRequest
    ): Observable<Response<AddCommentPayload>> {
        return endPoints.replyProductComment(productId, commentId, commetnsRequest)
    }

    fun getProductCommentReplies(
        productId: String,
        commentId: String?,
        map: Map<String, Any?>?
    ): Observable<Response<RepliesPayload>> {
        return endPoints.getProductCommentReplies(productId, commentId, map)
    }

    fun likePlaceComment(
        placeId: String,
        commentId: String?
    ): Observable<Response<StringOnlyResponse>> {
        return endPoints.likePlaceComment(placeId, commentId)
    }

    fun unLikePlaceComment(
        placeId: String,
        commentId: String?
    ): Observable<Response<StringOnlyResponse>> {
        return endPoints.unLikePlaceComment(placeId, commentId)
    }

    fun getLikedOffers(page: Int): Observable<Response<OffersResponseModel>> {
        return endPoints.getLikedOffers(
            page, 20,
            SharedPreferencesManager.getInstance().selectedCachedArea?.area?._id
        )
    }

    fun getLikedPlaces(page: Int): Observable<Response<NearbyPlacesResponseModel>> {
        return endPoints.getLikedPlaces(
            page, 20,
            SharedPreferencesManager.getInstance().selectedCachedArea?.area?._id
        )
    }

    fun getLikedProducts(page: Int): Observable<Response<ProductsResponseModel>> {
        return endPoints.getLikedProducts(
            page, 20,
            SharedPreferencesManager.getInstance().selectedCachedArea?.area?._id
        )
    }

    suspend fun getCart(): Response<CartPayload> {
        return endPoints.getCart()
    }

    suspend fun addOffer(createOrdersRequestModel: CreateOrdersRequestModel): Response<BaseModel> {
        return endPoints.addOffer(createOrdersRequestModel)
    }

    suspend fun addProduct(createOrdersRequestModel: CreateOrdersRequestModel): Response<BaseModel> {
        return endPoints.addProduct(createOrdersRequestModel)
    }

    suspend fun deleteOfferFromCart(_id: String): Response<BaseModel> {
        return endPoints.deleteOfferFromCart(_id)
    }

    suspend fun deleteProductFromCart(_id: String): Response<BaseModel> {
        return endPoints.deleteProductFromCart(_id)
    }

    suspend fun deleteCart(): Response<Void> {
        return endPoints.deleteCart()
    }

    suspend fun updateOffer(_id: String, cartModel: CartUpdateModel): Response<BaseModel> {
        return endPoints.updateOffer(_id, cartModel)
    }

    suspend fun updateProduct(_id: String, cartModel: CartUpdateModel): Response<BaseModel> {
        return endPoints.updateProduct(_id, cartModel)
    }

    suspend fun updateNote(cartModel: CartNoteUpdateModel): Response<BaseModel> {
        return endPoints.updateCartNote(cartModel)
    }

    suspend fun updateCartArea(addAddressRequestModel: AddAddressRequestModel): Response<CartPayload> {
        return endPoints.updateCartArea(addAddressRequestModel)
    }

    suspend fun updateProfile(updateUserDataRequestModel: UpdateUserDataRequestModel): Response<UpdateProfileResponseModel> {
        return endPoints.updateProfile(updateUserDataRequestModel)
    }

    fun getCognito(): Observable<Response<CognitoResponseModel>> {
        return endPoints.getCognito()
    }

    suspend fun getProfile(): Response<LoginResponseModel> {
        return endPoints.getProfile()
    }

    companion object {
        internal val instance = APICalls()
        fun getInstance(): APICalls {
            return instance
        }
    }
}