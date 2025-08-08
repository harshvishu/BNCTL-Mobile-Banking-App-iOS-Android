    //
    //  NewsView.swift
    //  Allianz (iOS)
    //
    //  Created by Prem's on 29/12/21.
    //

import SwiftUI

struct NewsView: View {
    
    @ObservedObject var viewModel: NewsViewModel
    
    init(_ isLocal:Bool = false) {
        viewModel = NewsViewModel(isLocal)
    }
    
    var body: some View {
        ZStack{
            Color("background").edgesIgnoringSafeArea(.all)
            VStack {
                TitleViewWithBack(title: "news_title")
                if let news = viewModel.newsData {
                    if (news.isEmpty) {
                        Spacer()
                        Text("No news data available")
                    } else {
                        ScrollView {
                            ForEach(news) { newsData in
                                VStack(spacing:7) {
                                    HStack {
                                        Text(newsData.date)
                                            .font(.footnote)
                                            .foregroundColor(Color.gray)
                                        Spacer()
                                    }
                                    NavigationLink {
                                        NewsDetailView(newsData: newsData)
                                    } label: {
                                        HStack(alignment:.top) {
                                            
                                            CustomImageView(urlString: newsData.url)
                                                .frame(width: 85, height: 85)
                                                .clipped(antialiased: true)
                                            VStack(alignment:.leading) {
                                                HStack {
                                                    Text(newsData.title)
                                                        .fontWeight(.bold)
                                                        .multilineTextAlignment(.leading)
                                                    Spacer()
                                                }
                                                Text(newsData.text)
                                                    .multilineTextAlignment(.leading)
                                                    .lineLimit(3)
                                            }
                                            .frame(maxWidth:.infinity)
                                        }
                                        .padding(10)
                                    }
                                    .frame(maxWidth: .infinity)
                                    .background(Color.white.cornerRadius(10))
                                }
                            }
                        }
                        .frame(maxWidth: .infinity)
                        .padding(.horizontal, 10)
                        .foregroundColor(Color.black)
                    }
                } else {
                    Spacer()
                    Text("Loading news")
                }
                Spacer()
            }
        }
        .hiddenNavigationBarStyle()
        .hiddenTabBar()
        .onAppear {
            viewModel.getNewsData()
        }
    }
    
}

struct NewsView_Previews: PreviewProvider {
    
    static var previews: some View {
        NavigationView {
            NewsView(true)
        }
    }
}
