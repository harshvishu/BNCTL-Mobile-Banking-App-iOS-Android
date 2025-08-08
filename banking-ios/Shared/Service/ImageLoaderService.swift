//
//  ImageLoaderService.swift
//  Allianz (iOS)
//
//  Created by Prem's on 06/01/22.
//

import Foundation
import UIKit

class ImageLoaderService: ObservableObject {
    
    @Published var image: UIImage = UIImage()
    
    func loadImage(for urlString: String) {
        guard let url = URL(string: urlString) else { return }
        
        let task = URLSession.shared.dataTask(with: url) { data, response, error in
            guard let data = data else { return }
            DispatchQueue.main.async {
                self.image = UIImage(data: data) ?? UIImage(named: "news.placeholder") ?? UIImage()
            }
        }
        task.resume()
    }
}
