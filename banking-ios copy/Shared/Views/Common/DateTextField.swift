//
//  DateTextField.swift
//  ComponentDebugger
//
//  Created by Kavitha Sambandam on 28/11/22.
//

import SwiftUI
import UIKit

public struct DateTextField: UIViewRepresentable {
    @Binding var date: Date?
    var pickerMode: UIDatePicker.Mode = .date
    var limit:Int = 32
    var allowEmptyDate: Bool = false
    private var minimumDate: Date?
    private var maximumDate: Date?

    init(date: Binding<Date?>,
         mode: UIDatePicker.Mode,
         allowEmptyDate: Bool = false) {
        self._date = date
        self.pickerMode = mode
        self.allowEmptyDate = allowEmptyDate
    }

    init(date: Binding<Date?>,
         in range: PartialRangeThrough<Date>,
         mode: UIDatePicker.Mode,
         allowEmptyDate: Bool = false) {
        self._date = date
        self.pickerMode = mode
        self.maximumDate = range.upperBound
        self.allowEmptyDate = allowEmptyDate
    }

    init(date: Binding<Date?>,
         in range: PartialRangeFrom<Date>,
         mode: UIDatePicker.Mode,
         allowEmptyDate: Bool = false) {
        self._date = date
        self.pickerMode = mode
        self.minimumDate = range.lowerBound
        self.allowEmptyDate = allowEmptyDate
    }

    init(date: Binding<Date?>,
         in range: ClosedRange<Date>,
         mode: UIDatePicker.Mode,
         allowEmptyDate: Bool = false) {
        self._date = date
        self.pickerMode = mode
        self.minimumDate = range.lowerBound
        self.maximumDate = range.upperBound
        self.allowEmptyDate = allowEmptyDate
    }

    public func makeUIView(context: Context) -> UITextField {
        let textField = UITextField()
        textField.delegate = context.coordinator
        textField.text = context.coordinator.getDateString(for: date)
        textField.tintColor = .clear

        /* let image = UIImage(named: "selectDate")
        let imageView = UIImageView(frame: CGRect(x: 25, y: 0, width: 20, height: 20))
        imageView.image = image
        imageView.clipsToBounds = true
        imageView.contentMode = .scaleAspectFit */

        let calenderButton = UIButton(type: .custom)
        calenderButton.frame = CGRect(x: 40, y: 8, width: 20, height: 18)
        calenderButton.setBackgroundImage(
            UIImage(named: "Calendar"),
            for: .normal
        )
        
        calenderButton.addTarget(context.coordinator, action: #selector(context.coordinator.calendarAction), for: .touchUpInside)

        let clearButton = UIButton(type: .custom)
        clearButton.frame = CGRect(x: 0, y: 0, width: 36, height: 36)
        clearButton.setBackgroundImage(UIImage(named: "IconClose"), for: .normal)
        clearButton.addTarget(context.coordinator, action: #selector(context.coordinator.clearButtonAction), for: .touchUpInside)
        clearButton.isHidden = !allowEmptyDate || date == nil

        let rightView = UIView()
        rightView.frame = CGRect(x: 0, y: 0, width: 76, height: 36)
        rightView.backgroundColor = .clear
        rightView.addSubview(clearButton)
        rightView.addSubview(calenderButton)
        textField.rightView = rightView
        textField.rightViewMode = .always

        context.coordinator.textField = textField
        context.coordinator.clearButton = clearButton

        return textField
    }

    public func updateUIView (
        _ textField: UITextField,
        context: UIViewRepresentableContext<DateTextField>) {
        textField.text = context.coordinator.getDateString(for: date)
        context.coordinator.minimumDate = self.minimumDate
        context.coordinator.maximumDate = self.maximumDate
    }

    public func makeCoordinator() -> DateTextField.Coordinator {
        return Coordinator(
            self,
            date: $date,
            mode: pickerMode,
            minimumDate: minimumDate,
            maximumDate: maximumDate,
            allowEmptyDate: allowEmptyDate
        )
    }

    public class Coordinator: NSObject, UITextFieldDelegate {
        var parent: DateTextField
        @Binding var date: Date?
        var didBecomeFirstResponder = false
        let pickerMode: UIDatePicker.Mode
        var minimumDate: Date?
        var maximumDate: Date?
        var allowEmptyDate: Bool
        var textField: UITextField?
        var clearButton: UIButton?

        init(_ textField: DateTextField,
             date:Binding<Date?>,
             mode: UIDatePicker.Mode,
             minimumDate: Date?,
             maximumDate: Date?,
             allowEmptyDate: Bool)
        {
            self.parent = textField
            self._date = date
            self.pickerMode = mode
            self.minimumDate = minimumDate
            self.maximumDate = maximumDate
            self.allowEmptyDate = allowEmptyDate
        }

        public func textField(_ textField: UITextField,
                              shouldChangeCharactersIn range: NSRange,
                              replacementString string: String) -> Bool {
            return false
        }

        public func textFieldShouldBeginEditing(_ textField: UITextField) -> Bool {
            textField.inputView = getDatePicker()
            textField.inputAccessoryView = getToolBar()
           return true
        }

        public func textFieldDidEndEditing(_ textField: UITextField) {
            /* let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "dd-MMM-yyyy"
            textField.text = dateFormatter.string(from: date) */
            textField.text = getDateString(for: date)
        }

        public func getDateString(for date: Date?) -> String {
            clearButton?.isHidden = !allowEmptyDate || date == nil
            if let currDate = date {
                return currDate.formatToLocalDate()
            } else {
                return ""
            }
        }

        private func getDatePicker() -> UIDatePicker {
            let picker = UIDatePicker()
            picker.preferredDatePickerStyle = .wheels
            picker.datePickerMode = pickerMode
            picker.minimumDate = minimumDate
            picker.maximumDate = maximumDate
            picker.addTarget(self, action: #selector(handleDatePicker(sender:)), for: .valueChanged)
            return picker
        }

        @objc func handleDatePicker(sender: UIDatePicker) {
            date = sender.date
        }

        private func getToolBar() -> UIToolbar {
            let toolbar: UIToolbar = UIToolbar()
            toolbar.barStyle = UIBarStyle.default
            let space = UIBarButtonItem(barButtonSystemItem: .flexibleSpace, target: nil, action: nil)
            let doneButton = UIBarButtonItem(barButtonSystemItem: .done, target: self, action: #selector(donePicker))
            toolbar.items = [space, doneButton]
            toolbar.sizeToFit()
            return toolbar
        }

        @objc func donePicker() {
            UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: nil, from: nil, for: nil)
        }

        @objc func clearButtonAction(_ sender: Any) {
            date = nil
            UIApplication.shared.sendAction(#selector(UIResponder.resignFirstResponder), to: textField, from: nil, for: nil)
            textField?.text = getDateString(for: date)
        }

        @objc func calendarAction(_ sender: UIButton) {
            UIApplication.shared.sendAction(#selector(UIResponder.becomeFirstResponder), to: textField, from: nil, for: nil)
        }
    }
}


struct DateTextFileld_Previews: PreviewProvider {
    static var previews: some View {
        PreviewWrapper()
    }

    struct PreviewWrapper:View {
        @State var v:Date? = Date()
        var body: some View {
            DateTextField(date:$v, mode: .date)
            .padding()
        }
    }
}

