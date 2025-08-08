//
//  FloatingLabelTextView.swift
//  Allianz (iOS)
//
//  Created by Dimitar Stoyanov Chukov on 25.02.22.
//

import SwiftUI
import Combine

fileprivate
struct FloatingLabelField<Content: View, V: View>: View {
    
    let label: Text
    @Binding var text: String
    let error: String?
    let content: (() -> Content)
    let additionalContent: (() -> V)?
    
    private let userDefaults = UserDefaults.standard
    
    @State private var showLabel: Bool = false
    @State private var showError: Bool = false
    
    init(
        label: Text,
        text: Binding<String>,
        error: String?,
        @ViewBuilder content: @escaping () -> Content,
        @ViewBuilder additionalContent: @escaping () -> V
    ) {
        self.label = label
        _text = text
        self.error = error
        self.content = content
        self.additionalContent = additionalContent
    }
    
    func getLocalizationBundle() -> Bundle? {
        let language = Language(rawValue: userDefaults.string(forKey: "preferedLanguage") ?? Language.bg.code)
        let languageCodeParts = language.code.split(separator: "_")
        let languageCode = String(languageCodeParts[0])
        let path = Bundle.main.path(forResource: languageCode, ofType: "lproj")!
        return Bundle(path: path)
    }
    
    //This is to support iOS 14
    func getLocalization(key: String) -> Text {
        let hasArgument = key.contains(where: { c in
            c == "/"
        })
        if hasArgument {
            let split = key.split(separator: "/")
            let key = String(split[0])
            let param = String(split[1])          
            let bundle = getLocalizationBundle()
            return Text(String.localizedStringWithFormat(NSLocalizedString(key, bundle: bundle!, comment: ""), param))
        } else {
            return Text(LocalizedStringKey(String(key)))
        }
    }
    
    var body: some View {
        VStack(alignment: .leading) {
            ZStack(alignment: .leading) {
                label
                    .foregroundColor(showLabel && showError ? .red : Color("SecondaryTextColor"))
                    .offset(y: text.isEmpty ? 0 : -40)
                    .scaleEffect(text.isEmpty ? 1 : 0.7, anchor: .leading)
                    .animation(.spring(), value: showLabel)
                    .onReceive(Just(text), perform: { newText in
                        showLabel = newText.isEmpty == false
                    })
                
                HStack {
                    content()
                    Image(systemName: "exclamationmark.circle.fill")
                        .foregroundColor(.red)
                        .opacity(showError ? 1 : 0)
                        .animation(.easeOut, value: showError)
                    Spacer()
                    if let additionalContent = additionalContent {
                        additionalContent()
                    }
                }
            }
            Divider()
                .background(showError ? Color.red : Color("TertiaryColor"))
            if showError, let error = error {
                getLocalization(key: error)
                    .foregroundColor(.red)
                    .opacity(showError ? 1 : 0)
                    .offset(y: showError ? 0 : -25)
                    .scaleEffect(showError ? 1 : 0.7, anchor: .leading)
                    .font(.footnote)
                    .animation(.easeInOut, value: showError)
                    .frame(maxWidth: .infinity, alignment: .leading)
            }
        }
        .padding(.top, showLabel ? 20 : 0)
        .onReceive(Just(error ?? ""), perform: { newErrorText in
            showError = newErrorText.isEmpty == false
        })
    }
}

fileprivate
extension FloatingLabelField where V == EmptyView {
    init(
        label: Text,
        text: Binding<String>,
        error: String?,
        @ViewBuilder content: @escaping () -> Content
    ) {
        self.label = label
        _text = text
        self.error = error
        self.content = content
        self.additionalContent = nil
    }
}

struct FloatingLabelTextField: View {
    
    let label: Text
    @Binding var text: String
    let error: String?
    let onCommit: (() -> Void)?
    
    init(
        label: Text,
        text: Binding<String>,
        error: String? = nil,
        onCommit: (() -> Void)? = nil
    ) {
        self.label = label
        _text = text
        self.error = error
        self.onCommit = onCommit
    }
    
    var body: some View{
        FloatingLabelField(label: label, text: $text, error: error) {
            TextField("", text: $text) {
                if let onCommit = onCommit {
                    onCommit()
                }
            }
        }
    }
}

struct FloatingLabelLimitTextField: View {
    
    let label: Text
    @Binding var text: String
    let error: String?
    let limit:Int
    let onCommit: (() -> Void)?
    
    init(
        label: Text,
        text: Binding<String>,
        error: String? = nil,
        limit: Int,
        onCommit: (() -> Void)? = nil
    ) {
        self.label = label
        _text = text
        self.error = error
        self.onCommit = onCommit
        self.limit = limit
    }
    
    var body: some View{
        FloatingLabelField(label: label, text: $text, error: error) {
            LimitedTextFileld("", value: $text, limit:limit)
        }
    }
}

struct FloatingLabelSecureField: View {
    
    let label: Text
    @Binding var text: String
    let error: String?
    let onCommit: (() -> Void)?
    
    init(
        label: Text,
        text: Binding<String>,
        error: String? = nil,
        onCommit: (() -> Void)? = nil
    ) {
        self.label = label
        _text = text
        self.error = error
        self.onCommit = onCommit
    }
    
    var body: some View{
        FloatingLabelField(label: label, text: $text, error: error) {
            SecureField("", text: $text) {
                if let onCommit = onCommit {
                    onCommit()
                }
            }
        }
    }
}

struct FloatingLabelAmountTextField<Content: View>: View {
    
    let label: Text
    @Binding var value:Double?
    let error: String?
    let content: (() -> Content)?
    let onCommit: (() -> Void)?
    
    @State private var text: String
    
    init(
        label: Text,
        value: Binding<Double?>,
        error: String? = nil,
        @ViewBuilder content: @escaping () -> Content,
        onCommit: (() -> Void)? = nil
    ) {
        self.label = label
        _value = value
        self.error = error
        self.content = content
        self.onCommit = onCommit
        
        _text = State(initialValue: "\(value)")
    }
    
    var body: some View{
        FloatingLabelField(
            label: label,
            text: $text,
            error: error,
            content: {
                CurrencyTextField(value: $value)
                    .keyboardType(.decimalPad)
                    .onChange(of: value) { value in
                        if let _ = value {
                            text = "-"
                        } else {
                            text = ""
                        }
                    }
            },
            additionalContent: {
                if let content = content {
                    content()
                }
            }
        )
    }
}

extension FloatingLabelAmountTextField where Content == EmptyView {
    init(
        label: Text,
        value: Binding<Double?>,
        error: String? = nil,
        onCommit: (() -> Void)? = nil
    ) {
        self.init(
            label: label,
            value: value,
            error: error,
            content: {
            EmptyView()
        }, onCommit: onCommit)
    }
}

struct FloatingLabelCurrencyTextField: View {
    
    let label: Text
    @Binding var value:Double?
    let currency: String
    let error: String?
    let onCommit: (() -> Void)?
    
    @State private var text: String = ""
    
    init(
        label: Text,
        value: Binding<Double?>,
        currency: String,
        error: String? = nil,
        onCommit: (() -> Void)? = nil
    ) {
        self.label = label
        _value = value
        self.currency = currency
        self.error = error
        self.onCommit = onCommit
    }
    
    var body: some View{
        FloatingLabelAmountTextField(
            label: label,
            value: $value,
            error: error,
            content: {
                Text(currency)
                    .frame(alignment: .trailing)
            },
            onCommit: onCommit)
    }
}

struct FloatingLabelTextField_Previews: PreviewProvider {
    static var previews: some View {
        PreviewWrapper()
    }
    
    struct PreviewWrapper: View {
        @State var sampleText: String = ""
        @State var value:Double? = 0
        
        var body: some View {
            VStack {
                FloatingLabelTextField(
                    label: Text("login_placeholder_username"),
                    text: $sampleText,
                    error: "common_error_field_required")
                FloatingLabelCurrencyTextField(
                    label: Text("test"),
                    value: $value,
                    currency: "BGN")
            }
            .padding()
        }
    }
}
