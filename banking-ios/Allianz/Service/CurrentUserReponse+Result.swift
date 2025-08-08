//
//  CurrentUserReponse.swift
//  Allianz
//
//  Created by harsh vishwakarma on 27/03/23.
//

import Foundation

extension CurrentUserService.CurrentUserReponse {
    struct Result: Decodable {
        let auth: Auth
        let customer: Customer
    }
}
