//
//  NewsModel.swift
//  Allianz (iOS)
//
//  Created by Prem's on 29/12/21.
//

import Foundation

class NewsModel: Codable, Identifiable{
    
    let id: String
    let date: String
    let title: String
    let text: String
    let url: String?
    
    static var preview: NewsModel? {
        let rawData = Data("""
        {
                "id": "5",
                "date": "08.11.2021",
                "title": "Сега празниците идват с още подаръци!",
                "text": "Направете до 08.01.2021 повече от 10 покупки с дебитна карта Mastercard от Алианц Банк България за поне 10 лв. всяка и може да спечелите награда! Първите 1520 от вас с най-голям брой трансакции ще получат ваучери в размер на 50 лв. за пазаруване в IKEA.",
                "url": "https://www.allianz.bg/bg_BG/individuals/banking/promos-and-offers/mastercard-debit-cards.html"
            }
        """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode(NewsModel.self, from: rawData) else {
            return nil
        }
        
        return parsedData
    }
    
    static var previewList: [NewsModel] {
        let rawData = Data("""
        [
            {
                "id": "1",
                "date": "11.01.2022",
                "title": "Важна информация!",
                "text": "Уважаеми клиенти,  За ваше удобство предлагаме нова възможност за вход и подписване в електронното банкиране – чрез SMS.  1. Въведете потребителско име и парола. 2. Изчакайте 2мин. без да потвърждавате вход в системата чрез SmartID. 3. Ще се визуализира нов начален екран с възможност за избор на „Вход със SMS“. 4. Въведете потребителско име, парола и кода, който сте получили в SMS-а."
            },
            {
                "id": "2",
                "date": "31.12.2021",
                "title": "Промени в общи условия ",
                "text": "Уважаеми клиенти, От 01.03.2022 г. влизат в сила промени в общи условия за откриване, водене и закриване на платежни сметки на физически лица и за предоставяне на платежни услуги и инструменти:",
                "url": "https://www.allianz.bg/bg_BG/individuals/banking/tariffs-and-documents/bank-documents.html#TabVerticalNegative8123355035"
            },
            {
                "id": "3",
                "date": "31.12.2021",
                "title": "Промени в общи условия ",
                "text": "Уважаеми клиенти, От 01.03.2022 г. влизат в сила промени в общи условия за издаване и използване на електронен платежен инструмент – банкова платежна карта (за бизнес клиенти): ",
                "url": "https://www.allianz.bg/bg_BG/business/banking/tariffs-and-documents/bank-documents.html#TabVerticalNegative91349094121"
            },
            {
                "id": "4",
                "date": "20.12.2021",
                "title": "Важна информация!",
                "text": "Уважаеми клиенти, Уведомяваме Ви, че поради технически причини има кратковременни прекъсвания на услугите Интернет и мобилно банкиране. Препоръчваме Ви при възникнал проблем да изтриете и преинсталирате приложението SmartID. Уверяваме Ви, че активно работим по решаване на проблема и предоставяне на нови решения за влизане и потвърждение в електронното банкиране.  Молим да ни извините за причиненото неудобство!",
                "url": "https://www.allianz.bg/bg_BG/individuals.html"
            }
        ]
        """.utf8)
        
        guard let parsedData = try? JSONDecoder().decode([NewsModel].self, from: rawData) else {
            return []
        }
        
        return parsedData
    }
}
