//
//  DataList.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 15.09.22.
//

import SwiftUI

//TODO: This view have to deleted 
struct DataList<Data, Row>:View where Data:RandomAccessCollection, Data.Element:Identifiable, Row:View {
    let data:Data
    let render:((Data.Element) -> Row)
    
    init(data: Data, render: @escaping (Data.Element) -> Row) {
        self.data = data
        self.render = render
        
        if #available(iOS 14.0, *) {
            
        } else {
            UITableView.appearance().separatorStyle = .none
        }
    }
    
    var body: some View {
        if #available(iOS 14.0, *) {
            ScrollView {
                LazyVStack {
                    ForEach(data) { element in
                        render(element)
                            .padding(.horizontal)
                            .padding(.bottom)
                    }
                }
            }
        } else {
            List {
                ForEach(data) { element in
                    render(element)
                }
            }
            .listStyle(PlainListStyle())
        }
    }
}

struct DataList_Previews: PreviewProvider {
    struct TestData:Identifiable {
        let numb:Int
        var id:Int { self.numb }
    }
    static var previews: some View {
        DataList(data:
                    (1...100).map({ n  in
            TestData(numb: n)
        })
        ) { el in
            HStack {
                Text("\(el.numb)")
                Spacer()
                Text("\(el.numb)")
            }
        }
    }
}
