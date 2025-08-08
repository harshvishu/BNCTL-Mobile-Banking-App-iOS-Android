//
//  Logger.swift
//  Allianz (iOS)
//
//  Created by Prem's on 02/11/21.
//

import Foundation
import os.log

class Logger {
    private static let subsystem = "Allianz"
    /*
     useSimpleLogOnly - the purpose of the flag is to set if to use logging using simple "print" or os_log method from OSLog package.
     There are big differences between the two approaches:
     1. Simple log with "print"(which is private func log) - just push to console the provided message with set log level and a tag
     2. os_log - logs are saved on device. Logs are visualized in the console, but also can be browsed/filtered in a dedicated Console App.
     Another difference is that message should be of type StaticString, not String. It has other functionalities - check https://developer.apple.com/documentation/os/logging
     os_log is persisting the logs, which can consume hundreds of MBs, but could be helpful in further profiling.
     
     Usage:
     simple:
     Logger.I(tag: "NETWORKING", message: "Request data...")
     
     os_log:
     let a: StaticString = "Request data..."
     Logger.I(tag: "NETWORKING, message: a)
     */
    private static let useSimpleLogOnly = true
    
    private static let LEVEL_INFO = "INFO"
    private static let LEVEL_DEBUG = "DEBUG"
    private static let LEVEL_ERROR = "ERROR"
    private static let LEVEL_FAULT = "FAULT"
    
    private init() {}
    
    public static func I(tag: String, _ message: String) {
        log(level: LEVEL_INFO, tag: tag, message)
    }
    
    public static func D(tag: String, _ message: String) {
        log(level: LEVEL_DEBUG, tag: tag, message)
    }
    
    public static func E(tag: String, _ message: String) {
        log(level: LEVEL_ERROR, tag: tag, message)
    }
    
    public static func F(tag: String, _ message: String) {
        log(level: LEVEL_FAULT, tag: tag, message)
    }
    
    public static func I(tag: String, _ message: StaticString) {
        if useSimpleLogOnly {
            log(level: LEVEL_INFO, tag: tag, message.description)
        } else {
            oslog(type: .info, category: tag, message)
        }
    }
    
    public static func D(tag: String, _ message: StaticString) {
        if useSimpleLogOnly {
            log(level: LEVEL_DEBUG, tag: tag, message.description)
        } else {
            oslog(type: .debug, category: tag, message)
        }
    }
    
    public static func E(tag: String, _ message: StaticString) {
        if useSimpleLogOnly {
            log(level: LEVEL_ERROR, tag: tag, message.description)
        } else {
            oslog(type: .error, category: tag, message)
        }
    }
    
    public static func F(tag: String, _ message: StaticString) {
        if useSimpleLogOnly {
            log(level: LEVEL_FAULT, tag: tag, message.description)
        } else {
            oslog(type: .fault, category: tag, message)
        }
    }
    
    private static func oslog(type: OSLogType, category: String, _ message: StaticString) {
        log(level: type.description, tag: category, message.description)
    }
    
    private static func log(level: String, tag: String, _ message: String) {
        NSLog("[%@] [%@] Message: %@", level, tag, message)
    }
}

extension OSLogType: CustomStringConvertible {
    
    public var description: String {
        switch self {
        case .default:
            return "DEFAULT"
        case .info:
            return "INFO"
        case .debug:
            return "DEBUG"
        case .error:
            return "ERROR"
        case .fault:
            return "FAULT"
        default:
            return "UNKNOWN"
        }
    }
}
