//
//  CustomImageView.swift
//  Allianz (iOS)
//
//  Created by Prem's on 06/01/22.
//

import Foundation
import UIKit
import SwiftUI

struct CustomImageView: View {
    var urlString: String?
    @ObservedObject var imageLoader = ImageLoaderService()
    @State var image: UIImage = UIImage()
    
    var body: some View {
        if let url = urlString {
            if #available(iOS 15.0, *) {
                AsyncImage(url: URL(string: url)) { phase in
                    if let image = phase.image {
                        image // Displays the loaded image.
                    } else {
                        Image("news.placeholder")
                    }
                }
            } else {
                Image(uiImage: image)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .onReceive(imageLoader.$image) { image in
                        self.image = image
                    }
                    .onAppear {
                        imageLoader.loadImage(for: url)
                    }
            }
        } else {
            Image("news.placeholder")
        }
    }
}
