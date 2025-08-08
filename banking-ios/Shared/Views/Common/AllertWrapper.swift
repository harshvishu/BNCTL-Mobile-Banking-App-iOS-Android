//
//  AllertWrapper.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 31.03.22.
//

import SwiftUI

extension View {
    ///  Combination between post iOS 15 alerts and pre iOS 15 allers
    /// - Parameters:
    ///     - showAlert: Binding Bool for the showing the alert
    ///     - titleKey:  the title of the Alert
    ///     - message:  the main body of the allert
    ///     - defaultLabel: default behavior label, usually "OK"
    ///     - cancelLabel: cancel behavior label
    ///     - defaultAction:  thios is called when the default button is pressed
    ///     - cancelAction:  this is called when the cancel button is pressed
    ///
    func compatibleAllert(
        showAlert:Binding<Bool>,
        titleKey:LocalizedStringKey,
        message:LocalizedStringKey,
        defailtLabel:LocalizedStringKey,
        cancelLabel:LocalizedStringKey? = nil,
        defaultAction:(() -> Void)? = nil,
        cancelAction:(() -> Void)? = nil
    ) -> some View {
        self.modifier(
            AlertWrapperWithMessage(
                showAlert: showAlert,
                titleKey: titleKey,
                defaultLabel:defailtLabel,
                defaultAction: defaultAction ?? {},
                cancelLabel: cancelLabel,
                cancelAction:cancelAction ?? {},
                message:message
            )
        )
    }
    
    ///  Combination between post iOS 15 alerts and pre iOS 15 allers
    /// - Parameters:
    ///     - showAlert: Binding Bool for the showing the alert
    ///     - titleKey:  the title of the Alert
    ///     - defaultLabel: default behavior label, usually "OK"
    ///     - cancelLabel: cancel behavior label
    ///     - defaultAction:  thios is called when the default button is pressed
    ///     - cancelAction:  this is called when the cancel button is pressed
    ///
    func compatibleAllert(
        showAlert:Binding<Bool>,
        titleKey:LocalizedStringKey,
        defailtLabel:LocalizedStringKey,
        cancelLabel:LocalizedStringKey? = nil,
        defaultAction:(() -> Void)? = nil,
        cancelAction:(() -> Void)? = nil
    ) -> some View {
        self.modifier(AlertWrapper(
                showAlert: showAlert,
                titleKey: titleKey,
                defaultLabel:defailtLabel,
                defaultAction: defaultAction ?? {},
                cancelLabel: cancelLabel,
                cancelAction:cancelAction ?? {}
        ))
    }
}

struct AlertWrapperWithMessage: ViewModifier {
    @Binding var showAlert:Bool
    var titleKey:LocalizedStringKey
    
    let defaultLabel:LocalizedStringKey
    let defaultAction:(() -> Void)?
    let cancelLabel:LocalizedStringKey?
    let cancelAction:(() -> Void)?
    let message:LocalizedStringKey
    
    func body(content: Content) -> some View {
        if #available(iOS 15.0, *) {
            content
                .alert(
                    titleKey,
                    isPresented: $showAlert,
                    actions: actions
                ) {
                    Text(message)
                }
        } else {
            content
                .alert(isPresented: $showAlert) {
                    if let cancelLabel = cancelLabel {
                        return Alert(
                            title: Text(titleKey),
                            message: Text(message),
                            primaryButton: .default(
                                Text(defaultLabel),
                                action: defaultAction
                            ),
                            secondaryButton: .cancel(
                                Text(cancelLabel),
                                action: cancelAction
                            )
                        )
                    } else {
                        return Alert(
                            title: Text(titleKey),
                            message: Text(message),
                            dismissButton: .default(
                                Text(defaultLabel),
                                action: defaultAction
                            )
                        )
                    }
                }
        }
    }
    
    @available(iOS 15.0, *)
    @ViewBuilder
    private func actions() -> some View {
        Button(role:.none, action: defaultAction ?? {}) {
            Text(defaultLabel)
        }
        if let cancelLabel = cancelLabel {
            Button(role:.cancel, action: cancelAction ?? {}) {
                Text(cancelLabel)
            }
        }
    }
}

struct AlertWrapper: ViewModifier {
    @Binding var showAlert:Bool
    var titleKey:LocalizedStringKey
    
    let defaultLabel:LocalizedStringKey
    let defaultAction:() -> Void
    let cancelLabel:LocalizedStringKey?
    let cancelAction:() -> Void
    
    func body(content: Content) -> some View {
        if #available(iOS 15.0, *) {
            content
                .alert(
                    titleKey,
                    isPresented: $showAlert,
                    actions: actions
                )
        } else {
            content
                .alert(isPresented: $showAlert) {
                    if let cancelLabel = cancelLabel {
                        return Alert(
                            title: Text(titleKey),
                            primaryButton: .default(
                                Text(defaultLabel),
                                action: defaultAction
                            ), secondaryButton: .cancel(
                                Text(cancelLabel),
                                action: cancelAction
                            )
                        )
                    } else {
                        return Alert(
                            title: Text(titleKey),
                            message: nil,
                            dismissButton: .default(
                                Text(defaultLabel),
                                action: defaultAction)
                        )
                    }
                }
        }
    }
    
    @available(iOS 15.0, *)
    @ViewBuilder
    private func actions() -> some View {
        Button(role:.none, action: defaultAction) {
            Text(defaultLabel)
        }
        if let cancelLabel = cancelLabel {
            Button(role:.cancel, action: cancelAction) {
                Text(cancelLabel)
            }
        }
    }
}

// MARK: - Preview
struct AllertWrapper_Previews: PreviewProvider {
    static var previews: some View {
        PreviewWrapper()
    }
    
    struct PreviewWrapper: View {
        @State var isPresented:Bool = false
        @State var isPresentedTwo:Bool = false
        @State var count = 0
        
        var body: some View {
            VStack {
                Button {
                    isPresented = true;
                } label: {
                    Text("Show allert")
                }
                .compatibleAllert(
                    showAlert: $isPresented,
                    titleKey: "title",
                    message: "message",
                    defailtLabel: "ok",
                    cancelLabel: "cancel"
                )
                
                Text("asdasd")
                Button {
                    isPresentedTwo = true;
                } label: {
                    Text("increase label \(count)")
                }
                .compatibleAllert(
                    showAlert: $isPresentedTwo,
                    titleKey: "title 2",
                    defailtLabel: "+1",
                    cancelLabel: nil,
                    defaultAction: {
                        count += 1
                    })
            }
        }
    }
}
