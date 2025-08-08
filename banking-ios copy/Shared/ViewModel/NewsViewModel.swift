//
//  NewsViewModel.swift
//  Allianz (iOS)
//
//  Created by Prem's on 29/12/21.
//

import Foundation

class NewsViewModel: ObservableObject {
 
    @Published var newsData: [NewsModel]? = nil
    
    init(_ isLocal:Bool = false){
        if( isLocal ) {
            newsData = NewsModel.previewList
        }
    }
    
    func getNewsData(){
        NewsService().getNews { error, newsResponse in
            if let error = error {
                Logger.E(tag: APP_NAME, error.localizedDescription)
            }
            if let newsResult = newsResponse {
                self.newsData = newsResult
            } else {
                Logger.E(tag: APP_NAME, error?.localizedDescription ?? "")
            }
        }
    }
}
