/*
 * Decompiled with CFR 0.0.
 *
 * Could not load the following classes:
 *  com.qurba.android.network.models.LikeProductPayload
 *  java.lang.Object
 *  java.lang.String
 *  java.util.ArrayList
 *  java.util.List
 *  org.json.JSONObject
 *  retrofit2.Response
 *  retrofit2.http.Body
 *  retrofit2.http.Field
 *  retrofit2.http.FormUrlEncoded
 *  retrofit2.http.GET
 *  retrofit2.http.Header
 *  retrofit2.http.POST
 *  retrofit2.http.Path
 *  retrofit2.http.Query
 *  rx.Observable
 */
package com.qurba.android.network

import com.google.gson.JsonElement
import com.qurba.android.network.models.*
import com.qurba.android.network.models.request_models.*
import com.qurba.android.network.models.request_models.auth.*
import com.qurba.android.network.models.response_models.*
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*
import rx.Observable

interface EndPoints {
    @POST(value = "/offers/offers/{offer_id}/comments")
    fun addOfferComments(
        @Path(value = "offer_id") var1: String,
        @Body commetnsRequest: CommetnsRequest
    ): Observable<Response<AddCommentPayload>>

    @PATCH(value = "/offers/offers/{offer_id}/comments/{comment_id}")
    fun updateOfferComments(
        @Path(value = "offer_id") var1: String,
        @Path(value = "comment_id") var2: String,
        @Body commetnsRequest: CommetnsRequest
    ): Observable<Response<UpdateCommentPayload>>

    @POST(value = "/places/places/{place_id}/comments")
    fun addPlaceComment(
        @Path(value = "place_id") var1: String,
        @Body commetnsRequest: CommetnsRequest
    ): Observable<Response<AddCommentPayload>>

    @PATCH(value = "places/places/{place_id}/comments/{comment_id}")
    fun updatePlaceComments(
        @Path(value = "place_id") var1: String,
        @Path(value = "comment_id") var2: String,
        @Body commetnsRequest: CommetnsRequest
    ): Observable<Response<UpdateCommentPayload>>

    @DELETE(value = "/offers/offers/{offer_id}/comments/{comment_id}")
    fun deleteOfferComment(
        @Path(value = "offer_id") var1: String,
        @Path(value = "comment_id") var2: String
    ): Observable<Response<UpdateCommentPayload>>

    @DELETE(value = "places/places/{place_id}/comments/{comment_id}")
    fun deletePlaceComment(
        @Path(value = "place_id") var1: String,
        @Path(value = "comment_id") var2: String
    ): Observable<Response<UpdateCommentPayload>>

    @JvmSuppressWildcards
    @GET(value = "/places/places/{place_id}/comments/{comment_id}/replies")
    fun getPlaceCommentReplies(
        @Path(value = "place_id") var1: String,
        @Path(value = "comment_id") commentId: String,
        @QueryMap params: Map<String, Any?>?
    ): Observable<Response<RepliesPayload>>

    @POST(value = "places/places/{place_id}/comments/{comment_id}/replies")
    fun replyPlaceComment(
        @Path(value = "place_id") var1: String,
        @Path(value = "comment_id") commentId: String,
        @Body commetnsRequest: CommetnsRequest
    ): Observable<Response<AddCommentPayload>>

    @POST(value = "places/places/{place_id}/comments/{comment_id}/like")
    fun likePlaceComment(
        @Path(value = "place_id") var1: String,
        @Path(value = "comment_id") commentId: String?
    ): Observable<Response<StringOnlyResponse>>

    @POST(value = "places/places/{place_id}/comments/{comment_id}/unlike")
    fun unLikePlaceComment(
        @Path(value = "place_id") var1: String,
        @Path(value = "comment_id") commentId: String?
    ): Observable<Response<StringOnlyResponse>>

    @POST(value = "offers/offers/{offer_id}/like")
    fun likeOffer(@Path(value = "offer_id") var1: String): Observable<Response<StringOnlyResponse>>

    @POST(value = "auth/add-mobile-to-user")
    fun addMobileToUser(@Body var1: SignUpPhoneRequestModel): Observable<Response<BaseModel>>

    @POST(value = "auth/change-password")
    fun changePassword(@Body var1: ChangePasswordRequest): Observable<Response<LoginResponseModel>>

    @POST(value = "orders/orders")
    fun createOrder(@Body var1: CreateOrdersRequestModel): Observable<Response<OrderResponseModel>>

    @POST(value = "places/places/follow")
    fun followPlace(@Body var1: FollowUnFollowRequestModel): Observable<Response<StringOnlyResponse>>

    @POST(value = "auth/forgot-password-email")
    fun forgotPasswordEmail(@Body var1: SignUpEmailRequestModel): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/forgot-password-email-password")
    fun forgotPasswordEmailReset(@Body var1: ForgotEmailVerifyRequestModel): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/forgot-password-email-verify")
    fun forgotPasswordEmailVerify(@Body var1: ForgotEmailVerifyRequestModel): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/forgot-password-mobile")
    fun forgotPasswordMobile(@Body var1: SignUpPhoneRequestModel): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/forgot-password-mobile-password")
    fun forgotPasswordMobileReset(@Body var1: ForgotPhoneVerifyRequestModel): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/forgot-password-mobile-verify")
    fun forgotPasswordMobileVerify(@Body var1: ForgotPhoneVerifyRequestModel): Observable<Response<LoginResponseModel>>

    @get:GET(value = "auth/app-settings")
    val appSettings: Observable<Response<AppSettingsPayload>>

    //    @GET(value = "places/categories")
    //    public Observable<Response<CategoriesResponseModel>> getCategories();
    @JvmSuppressWildcards
    @GET(value = "/offers/offers/{offer_id}/comments")
    fun getOfferComments(
        @Path(value = "offer_id") var1: String,
        @QueryMap params: Map<String, Any?>?
        //in case of notification only = true
    ): Observable<Response<CommentsPayload>>

    @JvmSuppressWildcards
    @GET(value = "/products/products/{product_id}/comments")
    fun getProductComments(
        @Path(value = "product_id") var1: String,
        @QueryMap params: Map<String, Any?>?
    ): Observable<Response<CommentsPayload>>

    @JvmSuppressWildcards
    @GET(value = "/places/places/{place_id}/comments")
    fun getPlaceComments(
        @Path(value = "place_id") var1: String,
        @QueryMap params: Map<String, Any?>?
    ): Observable<Response<CommentsPayload>>

    @get:GET(value = "/offers/users/liked-offers")
    val likedOffers: Observable<Response<SearchOffersResponseModel>>

    @get:GET(value = "places/places/user-liked-places")
    val followedPlaces: Observable<Response<NearbyPlacesResponseModel>>

    @GET(value = "orders/orders/{id}")
    fun getOrderDetails(@Path(value = "id") id: String): Observable<Response<OrderResponseModel>>

    @GET(value = "offers/offers/{offer_id}")
    fun getOfferDetails(
        @Path(value = "offer_id") var1: String,
        @Query(value = "area") area: String?
    ): Observable<Response<OfferDetailsResponseModel>>

    @JvmSuppressWildcards
    @GET(value = "offers/offers")
    fun getOffers(
        @Query(value = "page") page: Int,
        @QueryMap params: Map<String, Any?>?
    ): Observable<Response<OffersResponseModel>>

    @GET(value = "offers/offers")
    fun getOffers(
        @Query(value = "page") page: Int,
        @Query(value = "area") area: String,
        @Query(value = "city") city: String,
        @Query(value = "country") country: String
    ): Observable<Response<OffersResponseModel>>

    @JvmSuppressWildcards
    @GET(value = "/places/places")
    suspend fun getPlaces(
        @Query(value = "page") page: Int,
        @QueryMap params: Map<String, Any>
    ): Response<NearbyPlacesResponseModel>

    @JvmSuppressWildcards
    @GET(value = "/places/places")
    fun searchPlaces(
        @Query(value = "page") page: Int,
        @Query(value = "search") search: String,
        @QueryMap params: Map<String, Any>
    ): Observable<Response<NearbyPlacesResponseModel>>

    @get:GET(value = "/places/categories")
    val placeCategories: Observable<Response<CategoriesResponseModel>>

    @POST(value = "offers/offers/{offer_id}/share/")
    fun shareOffer(@Path(value = "offer_id") var1: String): Observable<Response<StringOnlyResponse>>

    @POST(value = "places/places/{place_id}/share/")
    fun sharePlace(@Path(value = "place_id") var1: String): Observable<Response<StringOnlyResponse>>

    @JvmSuppressWildcards
    @GET(value = "orders/orders/order-now")
    fun getOrderNow(
        @QueryMap params: Map<String, Any?>?, @Query(value = "page") page: Int,
        @Query(value = "limit") limit: Int
    ): Observable<Response<OrderNowModel>>

    @GET(value = "orders/orders")
    fun getOrders(@Query(value = "page") var4: Int): Observable<Response<OrdersPayload>>

    @GET(value = "orders/orders")
    fun getActiveOrders(
        @Query(value = "page") var4: Int,
        @Query(value = "status") var5: String
    ): Observable<Response<OrdersPayload>>

    @GET(value = "places/places/{place_id}")
    fun getPlaceDetails(@Path(value = "place_id") var1: String): Observable<Response<PlaceDetailsResponseModel>>

    @GET(value = "/places/places/{place_id}")
    fun getPlaceDetailsHeader(
        @Path(value = "place_id") var1: String,
        @Query(value = "area") area: String?
    ): Observable<Response<PlaceDetailsHeaderResponse>>

    @JvmSuppressWildcards
    @GET(value = "/offers/places/{place_id}")
    fun getPlaceOffers(
        @Path(value = "place_id") var1: String,
        @QueryMap params: Map<String, Any>,
        @Query(value = "area") area: String?
    ): Observable<Response<OffersResponseModel>>

    @GET(value = "/products/places/{placeId}")
    fun getPlaceProducts(
        @Path(value = "placeId") var1: String,
        @Query(value = "page") page: Int,
        @Query(value = "area") area: String?
    ): Observable<Response<ProductsResponseModel>>

    @POST(value = "/products/products/{product_id}/like")
    fun likeProduct(@Path(value = "product_id") var1: String?): Observable<Response<LikeProductPayload>>

    @POST(value = "/products/products/{product_id}/unlike")
    fun disLikeProduct(@Path(value = "product_id") var1: String?): Observable<Response<LikeProductPayload>>

    @POST(value = "/products/products/{product_id}/share")
    fun shareProduct(@Path(value = "product_id") var1: String?): Observable<Response<StringOnlyResponse>>

    @POST(value = "auth/login-guest")
    suspend fun loginGuest(@Body var1: GuestAuthModel): Response<GuestLoginResponseModel>

    @PATCH(value = "auth/language")
    fun changeLanguage(@Body var1: LanguageModel): Observable<Response<GuestLoginResponseModel>>

    @POST(value = "auth/login-facebook")
    fun loginUserByFacebook(@Body var1: LoginFacebookPayload?): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/login-google")
    fun loginUserByGoogle(@Body var1: LoginFacebookPayload?): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/register-push-token-guest")
    fun registerPushToken(@Body var1: PushNotificationAuthModel): Observable<Response<Void>>

    @POST(value = "auth/signup-email-resend")
    fun registerUserByEmailResend(@Body var1: SignUpEmailRequestModel): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/signup-email-verify")
    fun registerUserByEmailVerify(@Body var1: SignUpEmailVerifyRequestModel): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/signup-mobile-resend")
    fun registerUserByPhoneResend(@Body var1: SignUpPhoneRequestModel): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/signup-mobile-verify")
    fun registerUserByPhoneVerify(@Body var1: SignUpPhoneVerifyRequestModel): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/register-push-token")
    fun registerUserPushToken(@Body var1: PushNotificationAuthModel): Observable<Response<Void>>

    @POST(value = "offers/offers/{offer_id}/unlike")
    fun unlikeOffer(@Path(value = "offer_id") var1: String): Observable<Response<StringOnlyResponse>>

    @GET(value = "offers/offers")
    fun searchOffers(
        @Query(value = "search") var1: String,
        @Query(value = "sortBy") var2: String,
        @Query(value = "sortOrder") var3: String,
        @Query(value = "page") var4: Int,
        @Query(value = "autocomplete") var5: Boolean
    ): Observable<Response<SearchOffersResponseModel>>

    @POST(value = "places/places/unfollow")
    fun unfollowPlace(@Body var1: FollowUnFollowRequestModel): Observable<Response<StringOnlyResponse>>

    @POST(value = "auth/update-email")
    fun updateEmail(@Body var1: ChangeEmailRequest?): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/update-email-verify")
    fun updateEmailVerification(@Body var1: VerifyEmailRequest?): Observable<Response<LoginResponseModel>>

    @POST(value = "auth/verify-code")
    fun verifyCode(@Body var1: VerifyPhoneRequestModel): Observable<Response<LoginResponseModel>>

    @GET(value = "products/products/{id}")
    fun getProductDetails(
        @Path(value = "id") var1: String?,
        @Query(value = "area") area: String?
    ): Observable<Response<ProductDetailsResponseModel>>

    @POST(value = "/places/places/{place_id}/like")
    fun likePlace(@Path(value = "place_id") var1: String?): Observable<Response<StringOnlyResponse>>

    @POST(value = "places/places/{place_id}/unlike")
    fun disLikePlace(@Path(value = "place_id") var1: String?): Observable<Response<StringOnlyResponse>>

    @GET(value = "places/areas/nearest-area")
    suspend fun getNearestLocations(
        @Query(value = "lat") var1: String,
        @Query(value = "lon") var2: String
    ): Response<NearestAreaResponseModel>

    @get:GET(value = "/places/countries/?page=1&search=eg")
    val country: Observable<Response<DeliveryAreaResponseModel>>

    @GET(value = "/places/countries/{country_id}/cities")
    fun getCities(
        @Path(value = "country_id") country_id: String,
        @Query(value = "page") page: Int
    ): Observable<Response<DeliveryAreaResponseModel>>

    @GET(value = "places/countries/{country_id}/cities/{city_id}/areas/")
    fun getAreasByCity(
        @Path(value = "country_id") country_id: String,
        @Path(value = "city_id") city_id: String,
        @Query(value = "page") page: Int
    ): Observable<Response<DeliveryAreaResponseModel>>

    @GET(value = "places/countries/{country_id}/cities/{city_id}/areas/")
    fun searchAreasByCity(
        @Path(value = "country_id") country_id: String,
        @Path(value = "city_id") city_id: String,
        @Query(value = "search") search: String,
        @Query(value = "page") page: Int
    ): Observable<Response<DeliveryAreaResponseModel>>

    @POST(value = "auth/delivery-addresses")
    fun addAddress(@Body var1: AddAddressRequestModel): Observable<Response<AddAddressResponseModel>>

    @PATCH(value = "auth/delivery-addresses/{address_id}")
    fun updateAddress(
        @Path(value = "address_id") address_id: String,
        @Body var1: AddAddressRequestModel
    ): Observable<Response<AddAddressResponseModel>>

    @DELETE(value = "auth/delivery-addresses/{address_id}")
    fun deleteAddress(@Path(value = "address_id") address_id: String): Observable<Response<BaseModel>>

    @GET(value = "/places/places/{place_id}/delivery-areas/{area_id}")
    fun checkDelivering(
        @Path(value = "place_id") place_id: String?,
        @Path(value = "area_id") area_id: String?
    ): Observable<Response<DeliveryValidationPayload>>

    @POST(value = "places/areas/vote")
    fun voteArea(@Body var1: VoteRequestModel): Observable<Response<JSONObject>>

    @POST(value = "orders/orders/total-savings")
    fun getTotalSavings(@Body var1: CreateOrdersRequestModel): Observable<Response<JsonElement>>

    @POST(value = "/orders/orders/validate")
    fun validateOrder(@Body var1: CreateOrdersRequestModel): Observable<Response<ValidateOrderRequestModel>>

    @GET(value = "places/places/featured")
    fun getFeaturedPlaces(@Query(value = "area") area: String?): Observable<Response<NearbyPlacesPayload>>

    @JvmSuppressWildcards
    @GET(value = "places/places/{place_id}/similar-places")
    fun getSimilarPlaces(
        @Path(value = "place_id") place_id: String,
        @QueryMap params: Map<String, Any?>?
    ): Observable<Response<SimilarPlacesPayload>>

    ///
    @POST(value = "offers/offers/{offer_id}/comments/{comment_id}/like")
    fun likeOfferComment(
        @Path(value = "offer_id") var1: String,
        @Path(value = "comment_id") commentId: String?
    ): Observable<Response<StringOnlyResponse>>

    @POST(value = "offers/offers/{offer_id}/comments/{comment_id}/unlike")
    fun unLikeOfferComment(
        @Path(value = "offer_id") var1: String,
        @Path(value = "comment_id") commentId: String?
    ): Observable<Response<StringOnlyResponse>>

    @POST(value = "offers/offers/{offer_id}/comments/{comment_id}/replies")
    fun replyOfferComment(
        @Path(value = "offer_id") var1: String,
        @Path(value = "comment_id") commentId: String,
        @Body commetnsRequest: CommetnsRequest?
    ): Observable<Response<AddCommentPayload>>

    @JvmSuppressWildcards
    @GET(value = "offers/offers/{offer_id}/comments/{comment_id}/replies")
    fun getOfferCommentReplies(
        @Path(value = "offer_id") var1: String,
        @Path(value = "comment_id") commentId: String?,
        @QueryMap params: Map<String, Any?>?
    ): Observable<Response<RepliesPayload>>

    ///
    @POST(value = "products/products/{product_id}/comments/{comment_id}/like")
    fun likeProductComment(
        @Path(value = "product_id") var1: String,
        @Path(value = "comment_id") commentId: String?
    ): Observable<Response<StringOnlyResponse>>

    @POST(value = "products/products/{product_id}/comments/{comment_id}/unlike")
    fun unLikeProductComment(
        @Path(value = "product_id") var1: String,
        @Path(value = "comment_id") commentId: String?
    ): Observable<Response<StringOnlyResponse>>

    @POST(value = "products/products/{product_id}/comments")
    fun addProductComments(
        @Path(value = "product_id") var1: String?,
        @Body commetnsRequest: CommetnsRequest?
    ): Observable<Response<AddCommentPayload>>

    @PATCH(value = "products/products/{product_id}/comments/{comment_id}")
    fun updateProductComments(
        @Path(value = "product_id") var1: String?,
        @Path(value = "comment_id") var2: String?,
        @Body commetnsRequest: CommetnsRequest?
    ): Observable<Response<UpdateCommentPayload>>

    @DELETE(value = "products/products/{product_id}/comments/{comment_id}")
    fun deleteProductComment(
        @Path(value = "product_id") var1: String?,
        @Path(value = "comment_id") var2: String?
    ): Observable<Response<UpdateCommentPayload>>

    @POST(value = "products/products/{product_id}/comments/{comment_id}/replies")
    fun replyProductComment(
        @Path(value = "product_id") var1: String,
        @Path(value = "comment_id") commentId: String?,
        @Body commetnsRequest: CommetnsRequest?
    ): Observable<Response<AddCommentPayload>>

    @JvmSuppressWildcards
    @GET(value = "products/products/{product_id}/comments/{comment_id}/replies")
    fun getProductCommentReplies(
        @Path(value = "product_id") var1: String,
        @Path(value = "comment_id") commentId: String?,
        @QueryMap params: Map<String, Any?>?
    ): Observable<Response<RepliesPayload>>

    @GET(value = "/offers/users/liked-offers")
    fun getLikedOffers(
        @Query(value = "page") page: Int,
        @Query(value = "limit") limit: Int,
        @Query(value = "area") area: String?
    ): Observable<Response<OffersResponseModel>>

    @GET(value = "/places/users/liked-places")
    fun getLikedPlaces(
        @Query(value = "page") page: Int,
        @Query(value = "limit") limit: Int,
        @Query(value = "area") area: String?
    ): Observable<Response<NearbyPlacesResponseModel>>

    @GET(value = "/products/users/liked-products")
    fun getLikedProducts(
        @Query(value = "page") page: Int,
        @Query(value = "limit") limit: Int,
        @Query(value = "area") area: String?
    ): Observable<Response<ProductsResponseModel>>

    @GET(value = "/orders/cart")
    suspend fun getCart(): Response<CartPayload>

    @POST(value = "/orders/cart/offers")
    suspend fun addOffer(
        @Body var1: CreateOrdersRequestModel
//        @Query(value = "override") override: Boolean = true
    ): Response<BaseModel>

    @POST(value = "/orders/cart/products")
    suspend fun addProduct(
        @Body var1: CreateOrdersRequestModel
//        @Query(value = "override") override: Boolean = true
    ): Response<BaseModel>

    @DELETE(value = "/orders/cart")
    suspend fun deleteCart(): Response<Void>

    @POST(value = "/orders/cart/products/{product_id}")
    suspend fun updateProductInCart(@Path(value = "product_id") _id: String): Response<BaseModel>

    @POST(value = "/orders/cart/offers/{offer_id}")
    suspend fun updateOfferInCart(@Path(value = "offer_id") _id: String): Response<BaseModel>

    @DELETE(value = "/orders/cart/offers/{offer_id}")
    suspend fun deleteOfferFromCart(@Path(value = "offer_id") _id: String): Response<BaseModel>

    @DELETE(value = "/orders/cart/products/{product_id}")
    suspend fun deleteProductFromCart(@Path(value = "product_id") _id: String): Response<BaseModel>

    @PATCH(value = "/orders/cart/offers/{offer_id}")
    suspend fun updateOffer(
        @Path(value = "offer_id") _id: String,
        @Body cartModel: CartUpdateModel
    ): Response<BaseModel>

    @PATCH(value = "/orders/cart/products/{product_id}")
    suspend fun updateProduct(
        @Path(value = "product_id") _id: String,
        @Body cartModel: CartUpdateModel
    ): Response<BaseModel>

    @PATCH(value = "/orders/cart/note")
    suspend fun updateCartNote(@Body cartModel: CartNoteUpdateModel): Response<BaseModel>

    @PATCH(value = "/orders/cart/area")
    suspend fun updateCartArea(@Body addressModel: AddAddressRequestModel): Response<CartPayload>

    @PATCH(value = "/auth/profile")
    suspend fun updateProfile(@Body var1: UpdateUserDataRequestModel): Response<UpdateProfileResponseModel>

    @GET(value = "/auth/get-cognito")
    fun getCognito(): Observable<Response<CognitoResponseModel>>

    @GET(value = "/auth/profile")
    suspend fun getProfile(): Response<LoginResponseModel>

}