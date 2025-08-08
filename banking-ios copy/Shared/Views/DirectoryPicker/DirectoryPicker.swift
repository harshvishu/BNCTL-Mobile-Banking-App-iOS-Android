//
//  DirectoryPicker.swift
//  Allianz (iOS)
//
//  Created by Dimitar Stoyanov Chukov on 7.04.22.
//

import SwiftUI
import UniformTypeIdentifiers

struct DirectoryPicker: UIViewControllerRepresentable {
    var callback: (URL) -> ()
    var onCancelCallback: () -> ()
    
    func makeCoordinator() -> Coordinator {
        return Coordinator(documentController: self)
    }
    
    func updateUIViewController(
        _ uiViewController: UIDocumentPickerViewController,
        context: UIViewControllerRepresentableContext<DirectoryPicker>) {
        }
    
    func makeUIViewController(context: Context) -> UIDocumentPickerViewController {
        let controller: UIDocumentPickerViewController
        if #available(iOS 14.0, *) {
            controller = UIDocumentPickerViewController(forOpeningContentTypes: [UTType.folder])
        } else {
            controller = UIDocumentPickerViewController(documentTypes: ["kUTTypeFolder"], in: .moveToService)
        }
        controller.delegate = context.coordinator
        return controller
    }
    
    class Coordinator: NSObject, UIDocumentPickerDelegate {
        var documentController: DirectoryPicker
        
        init(documentController: DirectoryPicker) {
            self.documentController = documentController
        }
        
        func documentPicker(_ controller: UIDocumentPickerViewController, didPickDocumentsAt urls: [URL]) {
            guard let url = urls.first, url.startAccessingSecurityScopedResource() else { return }
            defer { url.stopAccessingSecurityScopedResource() }
            documentController.callback(urls[0])
        }
        
        func documentPickerWasCancelled(_ controller: UIDocumentPickerViewController) {
            documentController.onCancelCallback()
        }
    }
}
