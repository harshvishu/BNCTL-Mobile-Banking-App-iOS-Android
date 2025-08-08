//
//  Constants.swift
//  Allianz (iOS)
//
//  Created by Prem's on 02/11/21.
//

import Foundation

// Global Values
let APP_NAME = Bundle.main.infoDictionary?["CFBundleName"] as! String
let BUNDLE_ID = Bundle.main.bundleIdentifier! as String
let UPDATE_URL = ReadConfigData.value(for: "UPDATE_URL", defValue: "")

// Base URL
let BASE_URL: String = ReadConfigData.value(for: "BASE_URL", defValue: "")
let LOGIN_TIMEOUT: Int = ReadConfigData.value(for: "LOGIN_TIMEOUT", defValue: 60)
let DECIMAL_SEPARATOR: String = ReadConfigData.value(for: "CURRENCY_DECIMAL_SEPARATOR", defValue: ".")
let GROUP_SEPARATOR: String = ReadConfigData.value(for: "CURRENCY_GROUP_SEPARATOR", defValue: ",")

// Constant Strings
let LOGIN_TOKEN = "LOGIN_TOKEN"
let LAST_ACTIVE_TIME = "LAST_ACTIVE_TIME"


// Previews Language
let PREVIEW_LOCALE = "bg"
//let PREVIEW_LOCALE = "bg"

let BASE_FONT = "Averta PE"
