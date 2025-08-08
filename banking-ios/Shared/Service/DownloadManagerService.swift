//
//  DownloadManagerService.swift
//  Allianz (iOS)
//
//  Created by Dimitar Stoyanov Chukov on 5.04.22.
//

import Foundation
import Combine

final class DownloadManagerService: ObservableObject {
    
    static let instance = DownloadManagerService()
    
    @Published var isDownloading: Bool = false
    @Published var isDownloaded: Bool = false {
        didSet {
            if (isDownloaded == false) {
                removeTempFile()
            }
        }
    }
    @Published var tempFile: URL? = nil

    var cancellables = Set<AnyCancellable>()
    
    private func createDirectoryIfMissing(_ tempDir: URL) {
        if FileManager.default.fileExists(atPath: tempDir.relativePath) == false {
            do {
                try FileManager.default.createDirectory(atPath: tempDir.relativePath, withIntermediateDirectories: true, attributes: nil)
            } catch let error {
                print("Error creating directory \(error)")
            }
        }
    }
    
    private func removeTempFile() {
       
        do {
            if let tempFile = tempFile {
                if FileManager.default.fileExists(atPath: tempFile.relativePath) {
                    try FileManager.default.removeItem(at: tempFile)
                }
            }
        } catch let error {
            print("Could not remove temp file \(error)")
        }
        tempFile = nil
    }
    
    func downloadFile<Body: Encodable>(
        fileName: String,
        url: ServiceType,
        method: HTTPMethod,
        parameters: Body? = nil,
        queryItems: [String: String]? = nil
    ) {
        isDownloading = true
        isDownloaded = false
        guard let url = URL(string: BASE_URL + url.description) else {
            isDownloading = false
            return
        }
        var request = URLRequest(url: url)
        request.timeoutInterval = TimeInterval(120)
        request.httpMethod = method.description
        request.addValue("application/json", forHTTPHeaderField: "Content-Type")
        if let loginToken = DataStore.instance.getLoginToken(){
            request.addValue("Bearer \(loginToken)", forHTTPHeaderField: "Authorization")
        }
            // Add Query parameters
        if let query = queryItems  {
            var urlComponents = URLComponents(string: url.description)!
            urlComponents.queryItems = []
            for (key, value) in query {
                let queryItem = URLQueryItem(name: key, value: value)
                urlComponents.queryItems?.append(queryItem)
            }
            request.url = urlComponents.url
            Logger.D(tag: APP_NAME,"Query Url: \(String(describing: request.url))")
        }
        
        URLSession.shared.dataTaskPublisher(for: request)
            .subscribe(on: DispatchQueue.global(qos: .background))
            .receive(on: DispatchQueue.main)
            .map { (data, response) -> Data in
                data
            }
            .sink(receiveCompletion: { [weak self] completion in
                switch completion {
                    case .failure(let error):
                        self?.isDownloading = false
                        print(error)
                        return
                    case .finished:
                        break
                }
            }, receiveValue: { [weak self] data in
                let tempDir = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first
                guard var tempDir = tempDir else {
                    return
                }
                tempDir = tempDir.appendingPathComponent(fileName)
                
                FileManager.default.createFile(atPath: tempDir.relativePath, contents: data)
                
                self?.tempFile = tempDir
                self?.isDownloading = false
                self?.isDownloaded = true
            })
            .store(in: &cancellables)
            
    }
    
}
