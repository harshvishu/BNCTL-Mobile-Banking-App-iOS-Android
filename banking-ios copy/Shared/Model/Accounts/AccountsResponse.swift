//
//  AccountsResponse.swift
//  BNCTL
//
//  Created by Evgeniy Raev on 20.02.23.
//

import Foundation

struct AccountsResponse:Decodable {
    let result: [Account]
}
