//
//  NewsDetailView.swift
//  Allianz (iOS)
//
//  Created by Prem's on 04/01/22.
//

import SwiftUI

struct NewsDetailView: View {
    
    @State var newsData: NewsModel
    
    var body: some View {
        ZStack{
            VStack {
                TitleViewWithBack(title: "news_title_message")
                ScrollView{
                    VStack(alignment:.leading){
                        CustomImageView(urlString: newsData.url ?? "")
                            .frame(maxWidth: .infinity, minHeight: 230)
                            .clipped(antialiased: true)
                        Spacer()
                        Text(newsData.title)
                            .multilineTextAlignment(.leading)
                            .padding()
                            .font(.headline)
                        Spacer()
                        Text(newsData.text)
                            .multilineTextAlignment(.leading)
                            .padding()
                    }
                }
            }
        }
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
    }
}

struct NewsDetailView_Previews: PreviewProvider {
    static var previews: some View {
        NavigationView {
            NewsDetailView(newsData: NewsModel.preview!)
        }
    }
}
