//
//  MeaPushProvisioning.h
//  MeaPushProvisioning
//
//  Copyright © 2019 MeaWallet AS. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <PassKit/PassKit.h>

#import <MeaPushProvisioning/MppCardDataParameters.h>
#import <MeaPushProvisioning/MppCompleteOemTokenizationData.h>
#import <MeaPushProvisioning/MppCompleteOemTokenizationResponseData.h>
#import <MeaPushProvisioning/MppInitializeOemTokenizationResponseData.h>
#import <MeaPushProvisioning/MppGetTokenRequestorsResponseData.h>
#import <MeaPushProvisioning/MppGetTokenizationReceiptResponseData.h>
#import <MeaPushProvisioning/MppIntent.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * Provides API for interaction with the MeaPushProvisioning library using class methods.
 */
@interface MeaPushProvisioning : NSObject

#pragma mark Configuration

/**
 * Returns payment app instance id.
 *
 * Method returns `paymentAppInstanceId` if it exists or generates a new one.
 *
 * @return Payment app instance id.
 */
+ (NSString *_Nonnull)paymentAppInstanceId;

/**
 * Loads provided client configuration file.
 *
 * @param configFileName  File name of the provided client configuration file.
 */
+ (void)loadConfig:(NSString *_Nonnull)configFileName;

/**
 * Returns hash of the loaded configuration.
 *
 * @return hash of the loaded configuration or an empty string when configuration is not loaded.
 */
+ (NSString *_Nonnull)configurationHash;

/**
 * Returns version code of the SDK.
 *
 * @return Version code.
 */
+ (NSString *)versionCode;

/**
 * Returns version name of the SDK.
 *
 * Example: "mpp-test-1.0.0"
 *
 * @return Version name.
 */
+ (NSString *)versionName;

/**
 * Switch enable/disable debug logging.
 *
 * @param enabled  Enable or disable debug logging.
 */
+ (void)setDebugLoggingEnabled:(BOOL)enabled;

#pragma mark In-App Provisioning

/**
 * Initiate in-app push provisioning with MppCardDataParameters parameter.
 *
 * Check if the payment card can be added to Apple Pay by using primaryAccountIdentifier in response.
 *
 * @param cardDataParameters Card data parameters as instance of MppCardDataParameters containing the card information.
 * @param completionHandler The code block invoked when request is completed.
 *
 *      Parameters for the `completionHandler`:
 *
 *      - MppInitializeOemTokenizationResponseData `*_Nullable data` -Initialization response data in case of success
 *      - `NSError *_Nullable error` - Error object in case of failure
 */
+ (void)initializeOemTokenization:(MppCardDataParameters *_Nonnull)cardDataParameters
                completionHandler:(void (^)(MppInitializeOemTokenizationResponseData *_Nullable data, NSError *_Nullable error))completionHandler;

/**
 * Complete in-app push provisioning. Exchanges Apple certificates and signature with Issuer Host.
 *
 * Delegate should implement `PKAddPaymentPassViewControllerDelegate` protocol to call completeOemTokenization:completionHandler: method,
 * once the data is exchanged `PKAddPaymentPassRequest` is passed to the handler to add the payment card to Apple Wallet.
 * In the end and delegate method is invoked to inform you if request has succeeded or failed.
 *
 * @param tokenizationData Card data parameters as instance of MppCardDataParameters containing the card information.
 * @param completionHandler The code block invoked when request is completed.
 *
 *      Parameters for the `completionHandler`:
 *
 *      - MppCompleteOemTokenizationResponseData `*_Nullable data` - Completition response data in case of success
 *      - `NSError *_Nullable error` - Error object in case of failure
 */
+ (void)completeOemTokenization:(MppCompleteOemTokenizationData *_Nonnull)tokenizationData
              completionHandler:(void (^)(MppCompleteOemTokenizationResponseData *_Nullable data, NSError *_Nullable error))completionHandler;

/**
 * Verify if primaryAccountIdentifier can be used to add payment pass.
 *
 * @param primaryAccountIdentifier Primary account identifier returned by initializeOemTokenization:completionHandler: method in [MppInitializeOemTokenizationResponseData primaryAccountIdentifier] property.
 *
 * @return Bool value if payment pass can be added with given primaryAccountIdentifier.
 */
+ (BOOL)canAddSecureElementPassWithPrimaryAccountIdentifier:(NSString *_Nonnull)primaryAccountIdentifier API_AVAILABLE(ios(13.4), watchos(6.4));

/**
 * Verify if primaryAccountNumberSuffix can be used to add payment pass. Check specific for iPhone.
 *
 * @param primaryAccountNumberSuffix PAN suffix.
 *
 * @return Bool value if payment pass can be added with given primaryAccountNumberSuffix.
 */
+ (BOOL)canAddSecureElementPassWithPrimaryAccountNumberSuffix:(NSString *_Nonnull)primaryAccountNumberSuffix API_AVAILABLE(ios(13.4), watchos(6.4));

/**
 * Verify if payment pass exists with primaryAccountIdentifier. Check specific for iPhone.
 *
 * @param primaryAccountIdentifier Primary account identifier returned by initializeOemTokenization:completionHandler: method in [MppInitializeOemTokenizationResponseData primaryAccountIdentifier] property.
 *
 * @return Bool value if payment pass exists with given primaryAccountIdentifier.
 */
+ (BOOL)secureElementPassExistsWithPrimaryAccountIdentifier:(NSString *_Nonnull)primaryAccountIdentifier API_AVAILABLE(ios(13.4));

/**
 * Verify if remote payment pass exists with primaryAccountIdentifier. Check specific for Watch. Call when watch is paired.
 *
 * @param primaryAccountIdentifier Primary account identifier returned by initializeOemTokenization:completionHandler: method in [MppInitializeOemTokenizationResponseData primaryAccountIdentifier] property.
 *
 * @return Bool value if remote payment pass exists with given primaryAccountIdentifier.
 */
+ (BOOL)remoteSecureElementPassExistsWithPrimaryAccountIdentifier:(NSString *_Nonnull)primaryAccountIdentifier API_AVAILABLE(ios(13.4), watchos(6.4));

/**
 * Verify if payment pass exists with primaryAccountNumberSuffix. Check specific for iPhone.
 *
 * @param primaryAccountNumberSuffix PAN suffix.
 *
 * @return Bool value if payment pass exists with given primaryAccountNumberSuffix.
 */
+ (BOOL)secureElementPassExistsWithPrimaryAccountNumberSuffix:(NSString *_Nonnull)primaryAccountNumberSuffix  API_AVAILABLE(ios(13.4));

/**
 * Verify if remote payment pass exists with primaryAccountNumberSuffix. Check specific for Watch. Call when watch is paired.
 *
 * @param primaryAccountNumberSuffix PAN suffix.
 *
 * @return Bool value if remote payment pass exists with given primaryAccountNumberSuffix.
 */
+ (BOOL)remoteSecureElementPassExistsWithPrimaryAccountNumberSuffix:(NSString *_Nonnull)primaryAccountNumberSuffix API_AVAILABLE(ios(13.4), watchos(6.4));

/**
 * Returns secure element pass with primaryAccountIdentifier.
 *
 * @param primaryAccountIdentifier Primary account identifier returned by initializeOemTokenization:completionHandler: method in [MppInitializeOemTokenizationResponseData primaryAccountIdentifier] property.
 *
 * @return Bool value if remote secure element can be added with given primaryAccountIdentifier. Returns true if Watch is not paired.
 */
+ (PKSecureElementPass *)secureElementPassWithPrimaryAccountIdentifier:(NSString *_Nonnull)primaryAccountIdentifier API_AVAILABLE(ios(13.4), watchos(6.4));

/**
 * Returns secure element pass with primaryAccountNumberSuffix.
 *
 * @param primaryAccountNumberSuffix PAN suffix.
 *
 * @return Bool value if payment pass can be added with given primaryAccountNumberSuffix. Returns true if Watch is not paired.
 */
+ (PKSecureElementPass *)secureElementPassWithPrimaryAccountNumberSuffix:(NSString *_Nonnull)primaryAccountNumberSuffix API_AVAILABLE(ios(13.4), watchos(6.4));

/**
 * Returns secure element pass with primaryAccountIdentifier.
 *
 * @param primaryAccountIdentifier Primary account identifier returned by initializeOemTokenization:completionHandler: method in [MppInitializeOemTokenizationResponseData primaryAccountIdentifier] property.
 *
 * @return remote secure element pass.
 */
+ (PKSecureElementPass *)remoteSecureElementPassWithPrimaryAccountIdentifier:(NSString *_Nonnull)primaryAccountIdentifier API_AVAILABLE(ios(13.4), watchos(6.4));

/**
 * Returns remote secure element pass with primaryAccountNumberSuffix.
 *
 * @param primaryAccountNumberSuffix PAN suffix.
 *
 * @return remote secure element pass.
 */
+ (PKSecureElementPass *)remoteSecureElementPassWithPrimaryAccountNumberSuffix:(NSString *_Nonnull)primaryAccountNumberSuffix API_AVAILABLE(ios(13.4), watchos(6.4));

/**
 * Presents a Secure Element pass with Primary Account Identifier.
 *
 * @param primaryAccountIdentifier Primary account identifier returned by initializeOemTokenization:completionHandler: method in [MppInitializeOemTokenizationResponseData primaryAccountIdentifier] property.
 */
+ (void)presentSecureElementPassWithPrimaryAccountIdentifier:(NSString *_Nonnull)primaryAccountIdentifier API_AVAILABLE(ios(13.4), watchos(6.4));

/**
 * Presents a Secure Element pass with PAN Suffix.
 *
 * @param primaryAccountNumberSuffix PAN suffix.
 */
+ (void)presentSecureElementPassWithPrimaryAccountNumberSuffix:(NSString *_Nonnull)primaryAccountNumberSuffix API_AVAILABLE(ios(13.4), watchos(6.4));

#pragma mark Token Requestor

/**
 * Retrieves eligible Token Requestors which support push provisioning for provided card data.
 *
 * Once list of requestors is received, user has an option to select the one to be used.
 *
 * @param cardDataParameters Card data parameters as instance of MppCardDataParameters containing the card information to be provisioned by the token requestor.
 * @param completionHandler The code block invoked when request is completed.
 *
 *      Parameters for the `completionHandler`:
 *
 *      - MppGetTokenRequestorsResponseData `*_Nullable data` - Eligible Token Requestors in case of success
 *      - `NSError *_Nullable error` - Error object in case of failure
 */
+ (void)getTokenRequestors:(MppCardDataParameters *_Nonnull)cardDataParameters
         completionHandler:(void (^)(MppGetTokenRequestorsResponseData *_Nullable data, NSError *_Nullable error))completionHandler;

/**
 * Retrieves eligible Token Requestors which support push provisioning for provided card secrets.
 *
 * Once list of requestors is received, user has an option to select the one to be used.
 *
 * @param cards Array of the card secrets to retrieve the eligible token requestors for.
 * @param completionHandler The code block invoked when request is completed.
 *
 *      Parameters for the `completionHandler`:
 *
 *      - MppGetTokenRequestorsResponseData `*_Nullable data` - Eligible Token Requestors in case of success
 *      - `NSError *_Nullable error` - Error object in case of failure
 */
+ (void)getTokenRequestorsWithSecret:(NSArray *_Nonnull)cards
                   completionHandler:(void (^)(MppGetTokenRequestorsResponseData *_Nullable data, NSError *_Nullable error))completionHandler;

/**
 * Retrieves eligible Token Requestors which support push provisioning for provided encrypted PAN.
 *
 * Once list of requestors is received, user has an option to select the one to be used.
 *
 * @param encryptedData         Encrypted card data.
 * @param publicKeyFingerprint  Public Key Fingerprint. Used to recognise the key to be used for AES key decryption.
 * @param encryptedKey          Encrypted AES key used for encrypted card data.
 * @param initialVector         Initial Vector used for encrypted card data.
 * @param completionHandler     The code block invoked when request is completed.
 *
 *      Parameters for the `completionHandler`:
 *
 *      - MppGetTokenRequestorsResponseData `*_Nullable data` - Eligible Token Requestors in case of success
 *      - `NSError *_Nullable error` - Error object in case of failure
 */
+ (void)getTokenRequestorsWithEncryptedPan:(NSString *_Nonnull)encryptedData
                      publicKeyFingerprint:(NSString *_Nonnull)publicKeyFingerprint
                              encryptedKey:(NSString *_Nonnull)encryptedKey
                             initialVector:(NSString *_Nonnull)initialVector
                         completionHandler:(void (^)(MppGetTokenRequestorsResponseData *_Nullable data, NSError *_Nullable error))completionHandler;

/**
 * Retrieves eligible Token Requestors which support push provisioning for provided account ranges.
 *
 * Once list of requestors is received, user has an option to select the one to be used.
 *
 * @param accountRanges Array of the starting numbers of the account ranges to retrieve the eligible token requestors for.
 * @param completionHandler The code block invoked when request is completed.
 *
 *      Parameters for the `completionHandler`:
 *
 *      - MppGetTokenRequestorsResponseData `*_Nullable tokenRequestors` - Eligible Token Requestors in case of success
 *      - `NSError *_Nullable error` - Error object in case of failure
 */
+ (void)getTokenRequestorsWithAccountRanges:(NSArray *_Nonnull)accountRanges
                          completionHandler:(void (^)(MppGetTokenRequestorsResponseData *_Nullable tokenRequestors, NSError *_Nullable error))completionHandler;

/**
 * Pushes particular card data to a selected Token Requestor.
 *
 * Token Requestor selection is done from the list of eligible Token Requestors
 * previously returned by getTokenRequestors:completionHandler: method. In response Token Requestor will return a receipt, which needs to be
 * provided to a merchant or any other instance where the card will be digitized in. Receipt can be a deep-link to a bank's or merchant
 * application, and it can also be a URL to a web page.
 *
 * @param tokenRequestorId Identifies the Token Requestor, received from getTokenRequestors:completionHandler: method.
 * @param cardDataParameters Card data parameters as instance of MppCardDataParameters containing the card information to be provisioned by the token requestor.
 * @param completionHandler The code block invoked when request is completed.
 *
 *      Parameters for the `completionHandler`:
 *
 *      - MppGetTokenizationReceiptResponseData `*_Nullable data` - Tokenization receipt data in case of success
 *      - `NSError *_Nullable error` - Error object in case of failure
 */
+ (void)getTokenizationReceipt:(NSString *_Nonnull)tokenRequestorId
            cardDataParameters:(MppCardDataParameters *_Nonnull)cardDataParameters
             completionHandler:(void (^)(MppGetTokenizationReceiptResponseData *_Nullable data, NSError *_Nullable error))completionHandler;

/**
 * Pushes particular card data to a selected Token Requestor.
 *
 * Token Requestor selection is done from the list of eligible Token Requestors
 * previously returned by getTokenRequestors:completionHandler: method. In response Token Requestor will return a receipt, which needs to be
 * provided to a merchant or any other instance where the card will be digitized in. Receipt can be a deep-link to a bank's or merchant
 * application, and it can also be a URL to a web page.
 *
 * @param tokenRequestorId   Identifies the Token Requestor, received from getTokenRequestors:completionHandler: method.
 * @param cardDataParameters Card data parameters as instance of MppCardDataParameters containing the card information to be provisioned by the token requestor.
 * @param intent             Optional, required for VISA. The intent helps VCEH to determine the relevant user experience.
 *                          PUSH_PROV_MOBILE, PUSH_PROV_ONFILE - Synchronous flow. Enrollment of card credentials is completed as part of the same session on the same device as issuer and TR.
 *                          PUSH_PROV_CROSS_USER, PUSH_PROV_CROSS_DEVICE - Asynchronous flow.
 * @param completionHandler The code block invoked when request is completed.
 *
 *      Parameters for the `completionHandler`:
 *
 *      - MppGetTokenizationReceiptResponseData `*_Nullable data` - Tokenization receipt data in case of success
 *      - `NSError *_Nullable error` - Error object in case of failure
 */

+ (void)getTokenizationReceipt:(NSString *_Nonnull)tokenRequestorId
            cardDataParameters:(MppCardDataParameters *_Nonnull)cardDataParameters
                        intent:(MppIntent) intent
             completionHandler:(void (^)(MppGetTokenizationReceiptResponseData *_Nullable data, NSError *_Nullable error))completionHandler;

#pragma mark - Deprecated since iOS 13.4

/**
 * @name Deprecated since iOS 13.4
 */

/**
 * Verify if primaryAccountIdentifier can be used to add payment pass.
 *
 * @param primaryAccountIdentifier Primary account identifier returned by initializeOemTokenization:completionHandler: method in [MppInitializeOemTokenizationResponseData primaryAccountIdentifier] property.
 *
 * @return Bool value if payment pass can be added with given primaryAccountIdentifier.
 */
+ (BOOL)canAddPaymentPassWithPrimaryAccountIdentifier:(NSString *_Nonnull)primaryAccountIdentifier API_DEPRECATED("Use +[MeaPushProvisioning canAddSecureElementPassWithPrimaryAccountIdentifier:] instead", ios(9.0, 13.4), watchos(2.0, 6.2));

/**
 * Verify if primaryAccountNumberSuffix can be used to add payment pass. Check specific for iPhone.
 *
 * @param primaryAccountNumberSuffix PAN suffix.
 *
 * @return Bool value if payment pass can be added with given primaryAccountNumberSuffix.
 */
+ (BOOL)canAddPaymentPassWithPrimaryAccountNumberSuffix:(NSString *_Nonnull)primaryAccountNumberSuffix API_DEPRECATED("Use +[MeaPushProvisioning canAddSecureElementPassWithPrimaryAccountNumberSuffix:] instead", ios(9.0, 13.4), watchos(2.0, 6.2));

/**
 * Verify if payment pass exists with primaryAccountIdentifier. Check specific for iPhone.
 *
 * @param primaryAccountIdentifier Primary account identifier returned by initializeOemTokenization:completionHandler: method in [MppInitializeOemTokenizationResponseData primaryAccountIdentifier] property.
 *
 * @return Bool value if payment pass exists with given primaryAccountIdentifier.
 */
+ (BOOL)paymentPassExistsWithPrimaryAccountIdentifier:(NSString *_Nonnull)primaryAccountIdentifier API_DEPRECATED("Use +[MeaPushProvisioning secureElementPassExistsWithPrimaryAccountIdentifier:] instead", ios(10.0, 13.4));

/**
 * Verify if remote payment pass exists with primaryAccountIdentifier. Check specific for Watch. Call when watch is paired.
 *
 * @param primaryAccountIdentifier Primary account identifier returned by initializeOemTokenization:completionHandler: method in [MppInitializeOemTokenizationResponseData primaryAccountIdentifier] property.
 *
 * @return Bool value if remote payment pass exists with given primaryAccountIdentifier.
 */
+ (BOOL)remotePaymentPassExistsWithPrimaryAccountIdentifier:(NSString *_Nonnull)primaryAccountIdentifier API_DEPRECATED("Use +[MeaPushProvisioning remoteSecureElementPassExistsWithPrimaryAccountIdentifier:] instead", ios(9.0, 13.4), watchos(2.0, 6.2));

/**
 * Verify if payment pass exists with primaryAccountNumberSuffix. Check specific for iPhone.
 *
 * @param primaryAccountNumberSuffix PAN suffix.
 *
 * @return Bool value if payment pass exists with given primaryAccountNumberSuffix.
 */
+ (BOOL)paymentPassExistsWithPrimaryAccountNumberSuffix:(NSString *_Nonnull)primaryAccountNumberSuffix API_DEPRECATED("Use +[MeaPushProvisioning secureElementPassExistsWithPrimaryAccountNumberSuffix:] instead", ios(9.0, 13.4));

/**
 * Verify if remote payment pass exists with primaryAccountNumberSuffix. Check specific for Watch. Call when watch is paired.
 *
 * @param primaryAccountNumberSuffix PAN suffix.
 *
 * @return Bool value if remote payment pass exists with given primaryAccountNumberSuffix.
 */
+ (BOOL)remotePaymentPassExistsWithPrimaryAccountNumberSuffix:(NSString *_Nonnull)primaryAccountNumberSuffix API_DEPRECATED("Use +[MeaPushProvisioning remoteSecureElementPassExistsWithPrimaryAccountNumberSuffix:] instead", ios(9.0, 13.4), watchos(2.0, 6.2));

@end

NS_ASSUME_NONNULL_END
