//
//  Space.swift
//  Chess
//
//  Created by Jake Present on 6/15/16.
//  Copyright © 2016 Jake Present. All rights reserved.
//

import Foundation

class Space: NSObject, NSCoding {
    
    fileprivate var highlighted:Bool
    fileprivate let value:Int
    fileprivate let type:TypeOfSpace
    
    enum TypeOfSpace: Int {
        case empty, pawn, bishop, knight, rook, queen, king
    }
    
    init(value:Int, type:TypeOfSpace){
        highlighted = false
        self.value = value
        self.type = type
    }
    
    required init?(coder aDecoder: NSCoder) {
        
        highlighted = aDecoder.decodeBool(forKey: "highlighted")
        value = aDecoder.decodeInteger(forKey: "value")
        type = TypeOfSpace(rawValue: aDecoder.decodeInteger(forKey: "type")) ?? .empty
        
    }
    
    func encode(with aCoder: NSCoder) {
        aCoder.encode(highlighted, forKey: "highlighted")
        aCoder.encode(value, forKey: "value")
        aCoder.encode(type.rawValue, forKey: "type")
    }
    
    func isHighlighted() -> Bool {
        return highlighted
    }
    
    func setHighlighted(_ highlighted:Bool) {
        self.highlighted = highlighted
    }
    
    func getValue() -> Int{
        return value
    }
    
    func getType() -> TypeOfSpace{
        return type
    }
    
}
