//
//  TabBarControll.swift
//  Allianz (iOS)
//
//  Created by Evgeniy Raev on 20.01.22.
//

import SwiftUI


extension UIView {
    
    func allSubviews() -> [UIView] {
        var res = self.subviews
        for subview in self.subviews {
            let riz = subview.allSubviews()
            res.append(contentsOf: riz)
        }
        return res
    }
}

struct Tool {
    static func showTabBar() {
        UIApplication.shared.windows.first(where: { $0.isKeyWindow })?.allSubviews().forEach({ (v) in
            if let view = v as? UITabBar {
                view.isHidden = false
            }
        })
    }
    
    static func hiddenTabBar() {
        UIApplication.shared.windows.first(where: { $0.isKeyWindow })?.allSubviews().forEach({ (v) in
            if let view = v as? UITabBar {
                view.isHidden = true
            }
        })
    }
}

struct ShowTabBar: ViewModifier {
    func body(content: Content) -> some View {
        return content
            .padding(.zero)
            .onAppear {
                DispatchQueue.main.async {
                    Tool.showTabBar()
                }
            }
    }
}

struct HiddenTabBar: ViewModifier {
    func body(content: Content) -> some View {
        return content.padding(.zero).onAppear {
            DispatchQueue.main.async {
                Tool.hiddenTabBar()
            }
        }
    }
}

extension View {
    func showTabBar() -> some View {
        return self.modifier(ShowTabBar())
    }
    func hiddenTabBar() -> some View {
        return self.modifier(HiddenTabBar())
    }
}


struct TabBarControl: View {
    @State var screen = 2
    var body: some View {
        TabView(selection:$screen){
            
            NavigationView {
                VStack {
                    NavigationLink {
                        VStack {
                            Button {
                                self.screen = 1;
                            } label: {
                                Text("go to view with tab bar")
                            }
                            .onAppear(perform: {
                                Tool.hiddenTabBar()
                            })
                            .hiddenTabBar()
                            
                        }
                    } label: {
                        Text("Deeps")
                    }
                }.showTabBar()

            }
            .tag(2)
            .tabItem {
                Image(systemName: "lock")
                Text("Hide")
            }
            
            VStack {
                Text("the view with task bar")
                Button(action: {
                    screen = 2
                }, label: {
                    Text("hide task bar")
                })
                .onAppear(perform: {
                    Tool.showTabBar()
                })
            }
            .tag(1)
            .tabItem {
                Image(systemName: "lock.open")
                Text("Show")
            }
            
        }
    }
}

struct TabBarControll_Previews: PreviewProvider {
    static var previews: some View {
        TabBarControl()
    }
}
